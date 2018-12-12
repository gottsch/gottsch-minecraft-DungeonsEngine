/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class SurfaceRoomBuilder extends RoomBuilder implements ISurfaceRoomBuilder {
	private static final int MIN_ENTRANCE_XZ = 5;
	private static final int MAX_ENTRANCE_XZ = 9;
	private static final int MIN_ENTRANCE_Y= 7;
	private static final int MAX_ENTRANCE_Y = 13;
	
	private World world;
	
	/**
	 * 
	 * @param field
	 */
	public SurfaceRoomBuilder(World world, AxisAlignedBB field) {
		super(field);
		this.world = world;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.builder.ISurfaceRoomBuilder#buildEntranceRoom(java.util.Random, net.minecraft.util.math.AxisAlignedBB, com.someguyssoftware.gottschcore.positional.ICoords, com.someguyssoftware.dungeonsengine.config.LevelConfig)
	 */
	@Override
	public IRoom buildEntranceRoom(Random random, ICoords startPoint, LevelConfig config) {
		/*
		 * the start of the dungeon
		 */
		IRoom room = new Room().setStart(true).setAnchor(true).setType(Type.ENTRANCE);
		
		/*
		 *  adjust the minimum dimension sizes for entrance room.
		 *  entrances will always be a square-based (xz axis) room and odd numbered (ex length/width=5)
		 */
		int xz = RandomHelper.randomInt(random, MIN_ENTRANCE_XZ, MAX_ENTRANCE_XZ);
		if (xz % 2 == 0) xz++;
		room.setWidth(xz);
		room.setDepth(xz);
		room.setHeight(RandomHelper.randomInt(random, MIN_ENTRANCE_Y, MAX_ENTRANCE_Y));

		// set the starting room coords to be in the middle of the start point
		room.setCoords(
				new Coords(startPoint.getX()-((room.getWidth()-1)/2),
						startPoint.getY(),
						startPoint.getZ()-((room.getDepth()-1)/2)));
		// randomize a direction
		room.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));		
		return room;
	}
	
	/**
	 * Calculates the y value for the (x,z) coords of the room and ensure that the chunk is loaded.
	 */
	@Override
	protected ICoords randomizeCoords(Random random, AxisAlignedBB field, LevelConfig config) {
		ICoords coords = super.randomizeCoords(random, field, config);
		int y = 0;
		
		if (config.isMinecraftConstraintsOn()) {
			Chunk chunk = getWorld().getChunkFromBlockCoords(coords.toPos());
			// ensure that the chunk is loaded		
			if (chunk.isLoaded()) {
				y = WorldInfo.getHeightValue(world, coords);
			}
			else {
				return EMPTY_COORDS;
			}
		}
		return new Coords(coords.getX(), y, coords.getZ());
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SurfaceRoomBuilder [world=" + world + ", getField()=" + getField() + "]";
	}
}
