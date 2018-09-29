/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class SurfaceRoomBuilder extends RoomBuilder {

	private World world;
	
	/**
	 * 
	 * @param field
	 */
	public SurfaceRoomBuilder(World world, AxisAlignedBB field) {
		super(field);
		this.world = world;
	}

	/**
	 * Calculates the y value for the (x,z) coords of the room and ensure that the chunk is loaded.
	 */
	@Override
	protected ICoords randomizeCoords(Random random, AxisAlignedBB field, LevelConfig config) {
		ICoords coords = super.randomizeCoords(random, field, config);
		int y = 0;
		
		Chunk chunk = getWorld().getChunkFromBlockCoords(coords.toPos());
		// ensure that the chunk is loaded		
		if (chunk.isLoaded()) {
			y = WorldInfo.getHeightValue(world, coords);
		}
		else {
			return EMPTY_COORDS;
		}
		return new Coords(coords.getX(), y, coords.getZ());
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
