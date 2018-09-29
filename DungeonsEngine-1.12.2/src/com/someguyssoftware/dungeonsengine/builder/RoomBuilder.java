/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Mark Gottschling on Sep 22, 2018
 *
 */
public class RoomBuilder implements IRoomBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	public static final ICoords EMPTY_COORDS = new Coords(0, 0, 0);
	
//	private LevelConfig config;
//	private Random random;
//	private ICoords origin;
	/*
	 * a room may have it's own field separate from the level
	 */
	private AxisAlignedBB field;
//	private ICoords startPoint;
	
	/**
	 * 
	 * @param field
	 */
	public RoomBuilder(AxisAlignedBB field) {
		this.field = field;
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public IRoomBuilder withField(AxisAlignedBB field) {
		this.field = field;
		return this;
	}
	
	/**
	 * 
	 * @param rand
	 * @param roomIn
	 * @param config
	 * @return
	 */
	protected IRoom randomizeDimensions(Random random, AxisAlignedBB field, LevelConfig config, IRoom roomIn) {
		IRoom room = roomIn.copy();
		room.setWidth(Math.max(IRoom.MIN_WIDTH, RandomHelper.randomInt(random, config.getWidth().getMinInt(), config.getWidth().getMaxInt())));
		room.setDepth(Math.max(IRoom.MIN_DEPTH, RandomHelper.randomInt(random, config.getDepth().getMinInt(), config.getDepth().getMaxInt())));
		room.setHeight(Math.max(IRoom.MIN_HEIGHT, RandomHelper.randomInt(random, config.getHeight().getMinInt(), config.getHeight().getMaxInt())));		
		return room;
	}

	/**
	 * 
	 * @param random
	 * @param field
	 * @param config
	 * @return
	 */
	protected ICoords randomizeCoords(Random random, AxisAlignedBB field, LevelConfig config) {
		int x = RandomHelper.randomInt(random, 0, (int) (field.maxX - field.minX));
		int y = RandomHelper.randomInt(random, config.getYVariance().getMinInt(), config.getYVariance().getMaxInt());
		int z = RandomHelper.randomInt(random, 0, (int) (field.maxZ - field.minZ));
		return new Coords((int)field.minX, (int)field.minY, (int)field.minZ).add(x, y, z);
	}
	
	/**
	 * 
	 * @param random
	 * @param field
	 * @param config
	 * @param roomIn
	 * @return
	 */
	protected IRoom randomizeRoomCoords(Random random, AxisAlignedBB field, LevelConfig config, IRoom roomIn) {
//		Room room = new Room(roomIn);
		IRoom room = roomIn.copy();
		// generate a ranom set of coords
		ICoords c = randomizeCoords(random, field, config);
		if (c == EMPTY_COORDS) return EMPTY_ROOM;
		// center room using the random coords
		room.setCoords(c.add(-(room.getWidth()/2), 0, -(room.getDepth()/2)));
		return room;
	}
	
	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param config
	 * @param room
	 * @return
	 */
	public IRoom buildRoom(Random random, ICoords startPoint, LevelConfig config, IRoom room) {
		return buildRoom(random, getField(), startPoint, config, room);
	}
	
	/**
	 * 
	 * @param roomIn
	 * @return
	 */
	@Override
	public IRoom buildRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, IRoom roomIn) {
		// randomize dimensions
		IRoom room = randomizeDimensions(random, field, config, roomIn);
		if (room == EMPTY_ROOM) return room;
		
		// randomize the rooms
		room = randomizeRoomCoords(random, field, config, room);
		if (room == EMPTY_ROOM) return room;
		
		// set the degrees (number of edges)
		room.setDegrees(RandomHelper.randomInt(random, 
				config.getDegrees().getMinInt(), 
				config.getDegrees().getMaxInt()));
		
		// randomize a direction
		room.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));

		return room;
	}
	
	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param config
	 * @return
	 */
	public IRoom buildStartRoom(Random random, ICoords startPoint, LevelConfig config) {
		return buildStartRoom(random, getField(), startPoint, config);
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.builder.IRoomBuilder#buildStartRoom()
	 */
	@Override
	public IRoom buildStartRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config) {
		/*
		 * the start of the level
		 */
		IRoom startRoom = new Room().setStart(true).setAnchor(true).setType(Type.LADDER);
		startRoom = randomizeDimensions(random, field, config, startRoom);
		// ensure min dimensions are met for start room
		startRoom.setWidth(Math.max(7, startRoom.getWidth()));
		startRoom.setDepth(Math.max(7,  startRoom.getDepth()));
		// ensure that start room's dimensions are odd in length
		if (startRoom.getWidth() % 2 == 0) startRoom.setWidth(startRoom.getWidth()+1);
		if (startRoom.getDepth() % 2 == 0) startRoom.setDepth(startRoom.getDepth()+1);
		
		// set the starting room coords to be in the middle of the start point
		startRoom.setCoords(
				new Coords(startPoint.getX()-(startRoom.getWidth()/2),
						startPoint.getY(),
						startPoint.getZ()-(startRoom.getDepth()/2)));
		
		// randomize a direction
		startRoom.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));
		return startRoom;
	}
	
	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param config
	 * @param plannedRooms
	 * @return
	 */
	public IRoom buildEndRoom(Random random, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms) {
		return buildEndRoom(random, getField(), startPoint, config, plannedRooms);
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.builder.IRoomBuilder#buildEndRoom(java.util.List)
	 */
	@Override
	public IRoom buildEndRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms) {
		/*
		 * the end room of the level.
		 */
	
		// build the end room
		IRoom endRoom  = buildPlannedRoom(random, field, startPoint, config, plannedRooms).setEnd(true).setAnchor(true).setType(Type.LADDER);
		// ensure min dimensions are met for start room
		endRoom.setWidth(Math.max(7, endRoom.getWidth()));
		endRoom.setDepth(Math.max(7,  endRoom.getDepth()));
		// ensure that the room's dimensions are odd in length
		if (endRoom.getWidth() % 2 == 0) endRoom.setWidth(endRoom.getWidth()+1);
		if (endRoom.getDepth() % 2 == 0) endRoom.setDepth(endRoom.getDepth()+1);
		
		return endRoom;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.builder.IRoomBuilder#buildRoom(java.util.List)
	 */
	@Override
	public IRoom buildPlannedRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms) {
		IRoom plannedRoom = new Room();		
		/* 
		 * check to make sure planned rooms don't intersect.
		 * test up to 10 times for a successful position
		 */
		boolean checkRooms = true;
		int endCheckIndex = 0;
		checkingRooms:
		do {
			plannedRoom = buildRoom(random, field, startPoint, config, plannedRoom);
			if (plannedRoom == EMPTY_ROOM) return plannedRoom;
			logger.debug("New Planned Room:" + plannedRoom);
			endCheckIndex++;
			if (endCheckIndex > 10) {
				logger.warn("Unable to position Planned Room that meets positional criteria.");
				return EMPTY_ROOM;
			}
			for (IRoom room : plannedRooms) {
				if (room.getXZBoundingBox().intersects(plannedRoom.getXZBoundingBox())) {
					logger.debug("New Planned room intersects with planned list room.");
					continue checkingRooms;
				}
			}
//			// test if the room meets conditions to be placed in the minecraft world
//			if (!meetsRoomConstraints(plannedRoom)) {
//				break;
//			}			
			checkRooms = false;			
		} while (checkRooms);		
		return plannedRoom;
	}
//	
//	/**
//	 * @return the config
//	 */
//	public LevelConfig getConfig() {
//		return config;
//	}
//
//	/**
//	 * @param config the config to set
//	 */
//	private void setConfig(LevelConfig config) {
//		this.config = config;
//	}
//
//	/**
//	 * @return the random
//	 */
//	public Random getRandom() {
//		return random;
//	}

//	/**
//	 * @param random the random to set
//	 */
//	private void setRandom(Random random) {
//		this.random = random;
//	}
//
//	/**
//	 * @return the origin
//	 */
//	public ICoords getOrigin() {
//		return origin;
//	}
//
//	/**
//	 * @param origin the origin to set
//	 */
//	public void setOrigin(ICoords origin) {
//		this.origin = origin;
//	}

	/**
	 * @return the field
	 */
	@Override
	public AxisAlignedBB getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	@Override
	public void setField(AxisAlignedBB field) {
		this.field = field;
	}

//	/**
//	 * @return the startPoint
//	 */
//	public ICoords getStartPoint() {
//		return startPoint;
//	}
//
//	/**
//	 * @param startPoint the startPoint to set
//	 */
//	public void setStartPoint(ICoords startPoint) {
//		this.startPoint = startPoint;
//	}
}
