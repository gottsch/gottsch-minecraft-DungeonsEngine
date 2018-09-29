/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.someguyssoftware.dungeonsengine.comparator.RoomDistanceComparator;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Hallway;
import com.someguyssoftware.dungeonsengine.model.ILevel;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Level;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class SurfaceLevelBuilder extends LevelBuilder {
	private LevelConfig config;
	private ISurfaceRoomBuilder surfaceRoomBuilder;
	
	/**
	 * 
	 * @param world
	 * @param random
	 * @param field
	 * @param startPoint
	 */
	public SurfaceLevelBuilder(World world, Random random, AxisAlignedBB field, ICoords startPoint) {
		super(world, random, field, startPoint);
	}

	/**
	 * 
	 */
	@Override
	public ILevel build() {
		/*
		 * local handle to the start room
		 */
		IRoom startRoom = null;
		
		/*
		 * return object containing all the rooms that meet build criteria and the locations of the special rooms.
		 */
		ILevel level = new Level();
		
		// process all predefined rooms and categorize
		for (IRoom room : getPlannedRooms()) {
			if (room.isStart() && startRoom == null) startRoom = room;
			if (room.isAnchor())
				anchors.add(room);
			else
				spawned.add(room);
		}
		
		// create a start room if one is not provided
		if (startRoom == null) {
			startRoom = ((ISurfaceRoomBuilder)getRoomBuilder()).buildEntranceRoom(getRandom(), getStartPoint(), getConfig());
			anchors.add(startRoom);
		}
		
		// add randomly generated rooms
		this.spawned = spawnRooms();
		
		// sort working array based on distance
		Collections.sort(spawned, new RoomDistanceComparator(getStartPoint()));
		
		/*
		 *  move apart any intersecting rooms (uses anti-grav method). this current method uses anti-grav from the spawn only.
		 *  TODO refactor to use anti-grav against all rooms where force is lessened the greater the dist the rooms are from each other.
		 */
		this.rooms = applyDistanceBuffering();
		logger.debug("After Apply Distance Buffering Rooms.size=" + rooms.size());
		System.out.println("After Apply Distance Buffering Rooms.size=" + rooms.size() + ", room loss=" + getRoomLossToDistanceBuffering());
		
		// select rooms to use ie. filter out rooms that don't meet criteria
		this.rooms = selectValidRooms();
		logger.debug("After select valid rooms Rooms.size=" + this.rooms.size());
		System.out.println("After select valid rooms Rooms.size=" + this.rooms.size() + ", room loss=" + getRoomLossToValidation());
		if (this.rooms == null || rooms.size() < MIN_NUMBER_OF_ROOMS) {
			return EMPTY_LEVEL;
		}
		
		// setup the level
		IRoom room = rooms.get(0);
		int minX = room.getMinX();
		int maxX = room.getMaxX();
		int minY = room.getMinY();
		int maxY = room.getMaxY();
		int minZ = room.getMinZ();
		int maxZ = room.getMaxZ();
		
		// record min and max dimension values for level
		for (int i = 1; i < rooms.size(); i++) {
			if (rooms.get(i).getMinX() < minX) minX = rooms.get(i).getMinX();
			if (rooms.get(i).getMaxX() > maxX) maxX = rooms.get(i).getMaxX();
			if (rooms.get(i).getMinY() < minY) minY = rooms.get(i).getMinY();
			if (rooms.get(i).getMaxY() > maxY) maxY = rooms.get(i).getMaxY();
			if (rooms.get(i).getMinZ() < minZ) minZ = rooms.get(i).getMinZ();
			if (rooms.get(i).getMaxZ() > maxZ) maxZ = rooms.get(i).getMaxZ();
		}
		
		// TODO ensure that start and end room still exist
		
		// TODO need a Coords.copy() method in GottschCore
		// TODO need a wrapper for AxisAlignedBB in GottschCore
		
		// set all level properties
		level.setStartPoint(new Coords(getStartPoint()));
		level.setStartRoom(startRoom.copy());
		level.setField(new AxisAlignedBB(getField().minX, getField().minY, getField().minZ, getField().maxX, getField().maxY, getField().maxZ ));
		level.setRooms(new ArrayList<IRoom>(rooms));
		level.setConfig(getConfig().copy());
		
		// TODO refactor into Pair<Int, Int> dimsX, dimsY, dimsZ
		level.setMinX(minX);
		level.setMaxX(maxX);
		level.setMinY(minY);
		level.setMaxY(maxY);
		level.setMinZ(minZ);
		level.setMaxZ(maxZ);
		
		return level;
	}

	/**
	 * @return the roomBuilder
	 */
	@Override
	public IRoomBuilder getRoomBuilder() {
		return (IRoomBuilder) this.surfaceRoomBuilder;
	}

	/**
	 * @param roomBuilder the roomBuilder to set
	 */
	@Override
	public void setRoomBuilder(IRoomBuilder roomBuilder) {
		this.surfaceRoomBuilder = (ISurfaceRoomBuilder) roomBuilder;
	}
}
