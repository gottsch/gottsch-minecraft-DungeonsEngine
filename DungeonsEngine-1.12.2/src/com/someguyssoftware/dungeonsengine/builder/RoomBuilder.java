/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Mark Gottschling on Sep 22, 2018
 *
 */
public class RoomBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	public static final Room EMPTY_ROOM = new Room();
	
	private LevelConfig config;
	private Random random;
	private ICoords origin;
	private AxisAlignedBB field;
	private ICoords startPoint;
	
	/**
	 * 
	 */
	public RoomBuilder(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config) {
		this.random = random;
		this.field = field;
		this.origin = new Coords((int) field.minX, 0, (int) field.minZ);
		this.startPoint = startPoint;
		this.config = config;
	}

//	/**
//	 * 
//	 * @param random
//	 * @return
//	 */
//	public RoomBuilder withRandom(Random random) {
//		this.random = random;
//		return this;
//	}
//	
//	/**
//	 * 
//	 * @param config
//	 * @return
//	 */
//	public RoomBuilder withConfig(LevelConfig config) {
//		this.config = config;
//		return this;
//	}
//	
//	/**
//	 * 
//	 * @param field
//	 * @return
//	 */
//	public RoomBuilder withField(AxisAlignedBB field) {
//		this.setField(field);
//		this.setOrigin(new Coords((int)field.minX, (int)field.minY, (int)field.minZ));
//		return this;
//	}
//	
//	/**
//	 * 
//	 * @param startPoint
//	 * @return
//	 */
//	public RoomBuilder withStartPoint(ICoords startPoint) {
//		this.startPoint = startPoint;
//		return this;
//	}
	
	/**
	 * 
	 * @param rand
	 * @param roomIn
	 * @param config
	 * @return
	 */
	protected Room randomizeDimensions(Room roomIn) {
		Room room = new Room(roomIn);
		room.setWidth(Math.max(Room.MIN_WIDTH, RandomHelper.randomInt(getRandom(), getConfig().getWidth().getMinInt(), getConfig().getWidth().getMaxInt())));
		room.setDepth(Math.max(Room.MIN_DEPTH, RandomHelper.randomInt(getRandom(), getConfig().getDepth().getMinInt(), getConfig().getDepth().getMaxInt())));
		room.setHeight(Math.max(Room.MIN_HEIGHT, RandomHelper.randomInt(getRandom(), getConfig().getHeight().getMinInt(), getConfig().getHeight().getMaxInt())));		
		return room;
	}

	/**
	 * 
	 * @return
	 */
	protected ICoords randomizeCoords() {
		int x = RandomHelper.randomInt(getRandom(), 0, (int) (getField().maxX - getField().minX));
		int y = RandomHelper.randomInt(getRandom(), getConfig().getYVariance().getMinInt(), getConfig().getYVariance().getMaxInt());
		int z = RandomHelper.randomInt(getRandom(), 0, (int) (getField().maxZ - getField().minZ));
		return getOrigin().add(x, y, z);
	}
	
	/**
	 * 
	 * @param roomIn
	 * @return
	 */
	protected Room randomizeRoomCoords(Room roomIn) {
		Room room = new Room(roomIn);
		// generate a ranom set of coords
		ICoords c = randomizeCoords();
		// center room using the random coords
		room.setCoords(c.add(-(room.getWidth()/2), 0, -(room.getDepth()/2)));
		return room;
	}
	
	/**
	 * 
	 * @param roomIn
	 * @return
	 */
	protected Room randomizeRoom(Room roomIn) {
		// randomize dimensions
		Room room = randomizeDimensions(roomIn);

		// randomize the rooms
		room = randomizeRoomCoords(room);
		// calculate distance squared
		room.setDistance(room.getCenter().getDistanceSq(getStartPoint()));
		// set the degrees (number of edges)
		room.setDegrees(RandomHelper.randomInt(random, config.getDegrees().getMinInt(), config.getDegrees().getMaxInt()));
		// randomize a direction
		room.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));

		return room;
	}
	
	/**
	 * 
	 * @return
	 */
	public Room buildStartRoom() {
		/*
		 * the start of the level
		 */
		Room startRoom = new Room().setStart(true).setAnchor(true).setType(Type.LADDER);
		startRoom = randomizeDimensions(startRoom);
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
		//startRoom.setDistance(startRoom.getCoords().getDistanceSq(startPoint));
		startRoom.setDistance(0.0);
		// randomize a direction
		startRoom.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));
		return startRoom;
	}
	
	public Room buildEndRoom(List<Room> plannedRooms) {
		/*
		 * the end room of the level.
		 */
	
		// build the end room
		Room endRoom  = buildPlannedRoom(plannedRooms).setEnd(true).setAnchor(true).setType(Type.LADDER);
		// ensure min dimensions are met for start room
		endRoom.setWidth(Math.max(7, endRoom.getWidth()));
		endRoom.setDepth(Math.max(7,  endRoom.getDepth()));
		// ensure that the room's dimensions are odd in length
		if (endRoom.getWidth() % 2 == 0) endRoom.setWidth(endRoom.getWidth()+1);
		if (endRoom.getDepth() % 2 == 0) endRoom.setDepth(endRoom.getDepth()+1);
		
		return endRoom;
	}
	
	/**
	 * 
	 * @param plannedRooms
	 * @return
	 */
	protected Room buildPlannedRoom(List<Room> plannedRooms) {
		Room plannedRoom = new Room();		
		/* 
		 * check to make sure planned rooms don't intersect.
		 * test up to 10 times for a successful position
		 */
		boolean checkRooms = true;
		int endCheckIndex = 0;
		checkingRooms:
		do {
			plannedRoom = randomizeRoom(plannedRoom);
			logger.debug("New Planned Room:" + plannedRoom);
			endCheckIndex++;
			if (endCheckIndex > 10) {
				logger.warn("Unable to position Planned Room that meets positional criteria.");
				return EMPTY_ROOM;
			}
			for (Room room : plannedRooms) {
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
	
	/**
	 * @return the config
	 */
	public LevelConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	private void setConfig(LevelConfig config) {
		this.config = config;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	private void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * @return the origin
	 */
	public ICoords getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(ICoords origin) {
		this.origin = origin;
	}

	/**
	 * @return the field
	 */
	public AxisAlignedBB getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(AxisAlignedBB field) {
		this.field = field;
	}

	/**
	 * @return the startPoint
	 */
	public ICoords getStartPoint() {
		return startPoint;
	}

	/**
	 * @param startPoint the startPoint to set
	 */
	public void setStartPoint(ICoords startPoint) {
		this.startPoint = startPoint;
	}
}
