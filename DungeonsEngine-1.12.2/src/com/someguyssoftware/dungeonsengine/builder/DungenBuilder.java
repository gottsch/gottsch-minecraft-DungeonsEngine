/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeons2.Dungeons2;
import com.someguyssoftware.dungeons2.model.Room;
import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class DungenBuilder implements IDungeonBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private DungeonConfig config;
	private ILevelBuilder levelBuilder;
	
	/**
	 * 
	 */
	public DungenBuilder() {

	}

	/**
	 * 
	 */
	@Override
	public IDungeon build(World world, Random rand, AxisAlignedBB field, ICoords startPoint, DungeonConfig config) {
		// 0. determine if valid coords
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn() && !WorldInfo.isValidY(startPoint)) {
			logger.debug("{} is not a valid y value.", startPoint.getY());
			return EMPTY_DUNGEON;
		}
		
		//  1. get a valid surface location		
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
		
		// TODO should DungeonBuilder have it's own room builder or should the level builder's room builder be used? ie. surfaceLevelBuilder
		// 2. builder entrance level
		// TODO is level == null return EMPTY_DUNGEON
		IRoom entranceRoom = buildEntranceRoom(world, random, startPoint);
		logger.debug("Entrance Room:" + entranceRoom);
		
		determine if the entrance room can be build at this spot
		// TODO should the entrance room be constructed prior to all the minecraft constraint tests?
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn() &&
				!WorldInfo.isValidAboveGroundBase(world, entranceRoom.getCoords().resetY(surfaceCoords.getY()),
				entranceRoom.getWidth(), entranceRoom.getDepth(), 50, 20, 50)) {
			if (Dungeons2.log.isDebugEnabled())
			Dungeons2.log.debug(String.format("Surface area does not meet ground/air criteria @ %s", surfaceCoords));
			return EMPTY_DUNGEON;		
		}
		
		/*
		 * 2. ensure that all required objects exist and all conditions are met
		 */
		if (!performValidations(world, surfaceCoords)) return EMPTY_DUNGEON;
		

		/*
		 * The resultant dungeon object that contains all levels and the entrance of the dungeon.
		 */
		IDungeon dungeon = new Dungeon(config);
		
		return dungeon;
	}

	/**
	 * 
	 * @param startPoint
	 * @return
	 */
	public boolean performValidations(World world, ICoords startPoint) {		
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

}
