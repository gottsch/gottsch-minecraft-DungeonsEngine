/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.block.NullBlock;
import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.ILevel;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.IShaft;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class DungeonBuilder implements IDungeonBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	public static String NULL_BLOCK_NAME = "null";
	public static IBlockState NULL_BLOCK;
	
	private DungeonConfig config;
	private ILevelBuilder surfaceLevelBuilder; // TODO how to manage all the level builders - map?
	private ILevelBuilder levelBuilder;
	
	/**
	 * 
	 */
	public DungeonBuilder(IMod mod) {
		NULL_BLOCK = new NullBlock(mod.getId()).getDefaultState();
	}

	/**
	 * 
	 * @param surfaceBuilder
	 * @param levelBuilder
	 * @param config
	 */
	public DungeonBuilder(IMod mod, SurfaceLevelBuilder surfaceBuilder, LevelBuilder levelBuilder, DungeonConfig config) {
		this(mod);
		this.surfaceLevelBuilder = surfaceBuilder;
		this.levelBuilder = levelBuilder;
		this.config = config;
	}
	
	// TODO add .with() methods
	
	/**
	 * 
	 */
	@Override
	public IDungeon build(World world, Random random, AxisAlignedBB field, ICoords startPoint, DungeonConfig config) {
		/*
		 *  0. determine if valid coords
		 */
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn() && !WorldInfo.isValidY(startPoint)) {
			logger.debug("{} is not a valid y value.", startPoint.getY());
			return EMPTY_DUNGEON;
		}
		
		/*
		 *   1. get a valid surface location		
		 */
		ICoords surfaceCoords = null;
		if (levelBuilder.getConfig().isMinecraftConstraintsOn()) {
			surfaceCoords = WorldInfo.getDryLandSurfaceCoords(world, startPoint);
			if (surfaceCoords == null || surfaceCoords == EMPTY_DUNGEON) {
				logger.debug("Not a valid dry land surface @ {}", startPoint.toShortString());
				return EMPTY_DUNGEON;
			}
		}
		else {
			surfaceCoords = startPoint;
		}
		logger.debug("SurfaceCoords -> {}", surfaceCoords.toShortString());
		
		/*
		 * 2. build surface level
		 */
		ILevel surfaceLevel = buildSurfaceLevel(world, random, surfaceCoords);
		if (surfaceLevel.getStartRoom() == IRoomBuilder.EMPTY_ROOM) return EMPTY_DUNGEON;
		
		/*
		 * 3. ensure that all required objects exist and all conditions are met
		 */
		if (!performValidations(world, surfaceCoords, surfaceLevel.getStartRoom())) return EMPTY_DUNGEON;
		

		/*
		 * The resultant dungeon object that contains all levels and the entrance of the dungeon.
		 */
		IDungeon dungeon = new Dungeon(config);
		
		/*
		 * A list of rooms that are pre-planned to be placed in the dungeons (not randomized).
		 */
		List<IRoom> plannedRooms = new ArrayList<>();
		
		IRoom startRoom = null;
		IRoom endRoom = null;
		boolean isBottomLevel = false;
		
		// update the startPoint to be below the surface by surfaceBuffer amount
		startPoint = surfaceCoords.add(0, -(getLevelBuilder().getConfig().getSurfaceBuffer() + getLevelBuilder().getConfig().getHeight().getMaxInt()), 0);
				
		 // determine the number of levels to attempt to build		 
		int numberOfLevels = RandomHelper.randomInt(random,
				(int)getConfig().getNumberOfLevels().getMin(), 
				(int)getConfig().getNumberOfLevels().getMax());
		logger.debug("number of levels -> {}", numberOfLevels);
		
		/*
		 * > main loop <
		 * build all the levels of dungeon and join them together
		 */
		for (int levelIndex = 0; levelIndex < numberOfLevels; levelIndex++) {
			logger.debug("building level -> {} ", levelIndex);
			
			// determine if any levels can be made below the current level
			if (startPoint.getY() - getLevelBuilder().getConfig().getHeight().getMax() <= getConfig().getYBottom()) {
				isBottomLevel = true;
			}
			
 			/* 
			 * build the planned rooms for the level
			 */
			if (levelIndex == 0) {
				// TODO select correct level builder to pass in
				if (!buildTopPlannedRooms(random, startPoint, surfaceLevel, getLevelBuilder(), plannedRooms)) {
					return EMPTY_DUNGEON;
				}
			}
			else if (levelIndex == numberOfLevels - 1 || isBottomLevel) {	
				if (!buildBottomPlannedRooms(random, startPoint, dungeon.getLevels().get(levelIndex-1), levelBuilder, plannedRooms)) {
					return EMPTY_DUNGEON;
				}		
			}
			else {
				if (!buildPlannedRooms(random, startPoint, dungeon.getLevels().get(levelIndex-1), levelBuilder, plannedRooms)) {
					return EMPTY_DUNGEON;
				}
			}
			
			// clear the level builder
			getLevelBuilder().reset();
			
			// build a level
			for (IRoom r : plannedRooms) {
				getLevelBuilder().withRoom(r);
			}
			ILevel level = levelBuilder.build();
			if (level == LevelBuilder.EMPTY_LEVEL)
				return EMPTY_DUNGEON;
			
			/*
			 * add level to dungeon
			 */
			dungeon.getLevels().add(level);
			
			// build and add the shaft to the level
			if (levelIndex > 0) {
				logger.debug("Joing levels " + (levelIndex-1) + " to " + levelIndex);
				getLevelBuilder();
				if (levelBuilder.join(level, dungeon.getLevels().get(levelIndex-1)) == ILevelBuilder.EMPTY_SHAFT) {
					logger.warn("levels don't require joining -> {} to {}", levelIndex, (levelIndex-1));
				}
			}
			// TODO why do we need these values
			// update the min/max values for the dungeon
//			if (dungeon.getMinX() == null || level.getMinX() < dungeon.getMinX()) dungeon.setMinX(level.getMinX());
//			if (dungeon.getMaxX() == null || level.getMaxX() > dungeon.getMaxX()) dungeon.setMaxX(level.getMaxX());
//			if (dungeon.getMinY() == null || level.getMinY() < dungeon.getMinY()) dungeon.setMinY(level.getMinY());
//			if (dungeon.getMaxY() == null || level.getMaxY() > dungeon.getMaxY()) dungeon.setMaxY(level.getMaxY());
//			if (dungeon.getMinZ() == null || level.getMinZ() < dungeon.getMinZ()) dungeon.setMinZ(level.getMinZ());
//			if (dungeon.getMaxZ() == null || level.getMaxZ() > dungeon.getMaxZ()) dungeon.setMaxZ(level.getMaxZ());
			
			// clear planned rooms
			plannedRooms.clear();
			
			// hard break if bottom level
			if (isBottomLevel) {
				logger.debug("At bottom level... break...");
				break;
			}
			
			// update the start point to below the previous level's end room
			startPoint = endRoom.getBottomCenter();
//			startPoint = startPoint.resetY(dungeon.getMinY() - (int)levelConfig.getHeight().getMax());
			
			// end of main loop
		}
		
		/*
		 * join the entrance to the dungeon
		 */
		logger.debug("joining shaft from entrance to start room...");
		logger.debug("start room -> {}", dungeon.getLevels().get(0).getStartRoom());
		logger.debug("entrance room -> {}", surfaceLevel.getStartRoom());
		// join the top start room with the entrance room
		IShaft shaft = levelBuilder.join(dungeon.getLevels().get(0).getStartRoom(), surfaceLevel.getStartRoom());
		if (shaft == ILevelBuilder.EMPTY_SHAFT) {
			logger.debug("unable to join start room to entrance room");
		}
		
		logger.debug("adding shaft to shaft list...");
		logger.debug("dungeon level[0] -> {}", dungeon.getLevels().get(0));
		// add shaft to the top level
		dungeon.getLevels().get(0).getShafts().add(shaft);
		
		// return the dungeon
		return dungeon;
	}

	/**
	 * 
	 * @param random
	 * @param field
	 * @param startPoint
	 * @param config2
	 * @param plannedRooms
	 * @return
	 */
	private boolean buildTopPlannedRooms(Random random, ICoords startPoint, ILevel previousLevel, ILevelBuilder levelBuilder, List<IRoom> plannedRooms) {
		logger.debug("TOP LEVEL");
		// TODO not sure if going to need this or not.... the dungeon/level field should be set, but what about the room field?
		// TODO build roomField according to level config
		AxisAlignedBB roomField = new AxisAlignedBB(startPoint.toPos()).grow(40);
		// build start centered at startPoint
		IRoom startRoom = getLevelBuilder().getRoomBuilder().buildStartRoom(random, roomField, startPoint, levelBuilder.getConfig());
		if (startRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("Unable to generate Top level Start Room.");
			return false;				
		}
		// update to same direction as entrance
		startRoom.setDirection(previousLevel.getStartRoom().getDirection());
		logger.debug("top level start room -> {}", startRoom);
		
		// add to the planned rooms
		plannedRooms.add(startRoom);
		
		// build planned end room
		IRoom endRoom = getLevelBuilder().getRoomBuilder().buildEndRoom(random, roomField.grow(20), startPoint, levelBuilder.getConfig(), plannedRooms);
		if (endRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("unable to generate top level end room.");
			return false;
		}
		plannedRooms.add(endRoom);
		
		return true;
	}

	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param previousLevel
	 * @param levelBuilder
	 * @param plannedRooms
	 * @return
	 */
	private boolean buildBottomPlannedRooms(Random random, ICoords startPoint, ILevel previousLevel, ILevelBuilder levelBuilder, List<IRoom> plannedRooms) {
		logger.debug("BOTTOM LEVEL");
		// build the start room by duplicating the previous level's end room
		IRoom startRoom = previousLevel.getEndRoom();
		if (startRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("unable to generate bottom level start room");
			return false;
		}
		logger.debug("startPoint (bottom level) -> {}", startPoint);
		// update end room settings
		startRoom.setCoords(startRoom.getCoords().resetY(startPoint.getY()));
		startRoom.setAnchor(true).setStart(true).setEnd(false);		
		// add to the planned rooms
		plannedRooms.add(startRoom);
		// build the end (boss/treasure) room
		IRoom endRoom = getLevelBuilder().getRoomBuilder().buildTreasureRoom(random, startPoint, getLevelBuilder().getConfig(), plannedRooms);
		if (endRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("unable to generate bottom level end room.");
			return false;
		}
		logger.debug("boss room (bottom level) -> {}", endRoom);
		logger.debug("bossPoint (bottom level) @ {}", endRoom.getCoords().toShortString());
		plannedRooms.add(endRoom);				
		
		return true;
	}

	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param previousLevel
	 * @param levelBuilder
	 * @param plannedRooms
	 * @return
	 */
	private boolean buildPlannedRooms(Random random, ICoords startPoint, ILevel previousLevel, ILevelBuilder levelBuilder, List<IRoom> plannedRooms) {
		// 1. create start room from previous level end room
		IRoom startRoom = new Room(previousLevel.getEndRoom());
		if (startRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("unable to generate level start room.");
			return false;
		}
		// update end room settings
		startRoom.setCoords(startRoom.getCoords().resetY(startPoint.getY()));
		startRoom.setAnchor(true).setStart(true).setEnd(false);
		plannedRooms.add(startRoom);
		logger.debug("level start room -> {}", startRoom);

		// 2. create end room
		IRoom endRoom = getLevelBuilder().getRoomBuilder().buildPlannedRoom(random, getLevelBuilder().getRoomBuilder().getField().grow(20), startPoint, getLevelBuilder().getConfig(), plannedRooms);
		if (endRoom == IRoomBuilder.EMPTY_ROOM) {
			logger.warn("unable to generate level end room.");
			return false;
		}
		endRoom.setAnchor(true).setStart(false).setEnd(true);//.setType(Type.LADDER);
		plannedRooms.add(endRoom);	
		return true;
	}
	
	/**
	 * 
	 * @param world
	 * @param random
	 * @param startPoint
	 * @return
	 */
	private ILevel buildSurfaceLevel(World world, Random random, ICoords startPoint) {
//		AxisAlignedBB field = ((SurfaceLevelBuilder)this.getSurfaceLevelBuilder()).getField();
		ILevel level  = getSurfaceLevelBuilder().build();	
		return level;
	}

	/**
	 * 
	 * @param startPoint
	 * @return
	 */
	public boolean performValidations(World world, ICoords startPoint, IRoom entranceRoom) {		
		/*
		 * Perform all the minecraft world contraint checks
		 */
		
		// 1. Determine if surfaceCoords is within Dungeon constraints
		if (startPoint.getY() < getConfig().getYBottom() ||
				startPoint.getY() > config.getYTop()) {
			logger.debug("Start position is outside Y constraints -> Start: {}, yBottom: {}, yTop: {}",
					startPoint.toShortString(), getConfig().getYBottom(), getConfig().getYTop());
			return false;			
		}
		
		// 2. determine if startPoint is deep enough to support at least one level
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn()) {
			if (startPoint.getY() - getLevelBuilder().getConfig().getSurfaceBuffer() - getLevelBuilder().getConfig().getHeight().getMax() < config.getYBottom()) {
				logger.debug("Start position is not deep enough to generate a dungeon @ {}", startPoint.toShortString());
				return false;	
			}
		}
		
		// 3. determine if the entrance room can be build at this spot
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn() &&
				!WorldInfo.isValidAboveGroundBase(world, entranceRoom.getCoords().resetY(startPoint.getY()),
				entranceRoom.getWidth(), entranceRoom.getDepth(), 50, 20, 50)) {
			if (logger.isDebugEnabled())
			logger.debug("Surface area does not meet ground/air criteria @ {}", startPoint);
			return false;		
		}		

		return true;
	}
	
	
	@Override
	public ILevelBuilder getLevelBuilder() {
		return levelBuilder;
	}

	@Override
	public void setLevelBuilder(ILevelBuilder levelBuilder) {
		this.levelBuilder = levelBuilder;
	}

	public DungeonConfig getConfig() {
		return config;
	}

	public void setConfig(DungeonConfig config) {
		this.config = config;
	}

	/**
	 * @return the surfaceLevelBuilder
	 */
	public ILevelBuilder getSurfaceLevelBuilder() {
		return surfaceLevelBuilder;
	}

	/**
	 * @param surfaceLevelBuilder the surfaceLevelBuilder to set
	 */
	public void setSurfaceLevelBuilder(ILevelBuilder surfaceLevelBuilder) {
		this.surfaceLevelBuilder = surfaceLevelBuilder;
	}

}
