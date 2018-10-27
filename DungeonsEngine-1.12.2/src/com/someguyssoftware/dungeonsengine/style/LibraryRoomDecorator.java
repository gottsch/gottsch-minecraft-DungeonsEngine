/**
 * 
 */
package com.someguyssoftware.dungeonsengine.style;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import com.someguyssoftware.dungeonsengine.chest.ChestSheet;
import com.someguyssoftware.dungeonsengine.config.IDungeonsEngineConfig;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.IDungeonsBlockProvider;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.spawner.SpawnSheet;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Feb 15, 2017
 *
 */
public class LibraryRoomDecorator extends RoomDecorator {
	private static final int CARPET_PERCENT_CHANCE = 85;
	
	/**
	 * 
	 * @param chestSheet
	 * @param spawnSheet
	 */
	public LibraryRoomDecorator(ChestSheet chestSheet, SpawnSheet spawnSheet) {
		super(chestSheet, spawnSheet);
	}

	@Override
	public void decorate(World world, Random random, IDungeonsBlockProvider provider, IDecoratedRoom room, LevelConfig config, IDungeonsEngineConfig engineConfig) {
		/*
		 * NOTE these streams aren't needed for the multimap - just access to get the collection
		 */
		List<Entry<IArchitecturalElement, ICoords>> surfaceAirZone = room.getFloorMap().entries().stream().filter(x -> x.getKey().getBase() == Elements.SURFACE_AIR)
				.collect(Collectors.toList());		

		if (surfaceAirZone == null || surfaceAirZone.size() == 0) return;

		List<ICoords> wallZone = null;
		List<ICoords> floorZone = null;

		wallZone = (List<ICoords>) room.getFloorMap().get(Elements.WALL_AIR);
		floorZone = (List<ICoords>) room.getFloorMap().get(Elements.FLOOR_AIR);
		
		List<ICoords> removeFloorZones = new ArrayList<>();
		List<ICoords> removeWallZones = new ArrayList<>();
		
		// select a color for the carpet
		EnumDyeColor dye = EnumDyeColor.values()[random.nextInt(EnumDyeColor.values().length)];

		for (ICoords coords : floorZone) {
			BlockPos floorPos = coords.toPos().down();

			// get the x,z indexes
			int xIndex = coords.getX() - room.getCoords().getX();
			int zIndex = coords.getZ() - room.getCoords().getZ();

			// check if against a wall
			if (coords.getX() == room.getMinX() + 1 || coords.getX() == room.getMaxX() -1
					|| coords.getZ() == room.getMinZ() +1 || coords.getZ() == room.getMaxZ() -1) {
				// check if the block has support
				if (hasSupport(world, coords, Elements.FLOOR_AIR, provider.getLocation(coords, room))) {	
					// check if wall AND the 4th block
					if (((coords.getX() == room.getMinX() + 1 || coords.getX() == room.getMaxX() -1) && Math.abs(zIndex) % 4 == 0)
							|| ((coords.getZ() == room.getMinZ() + 1 || coords.getZ() == room.getMaxZ() -1) && Math.abs(xIndex) % 4 == 0)) {
						world.setBlockState(coords.toPos(), Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
					}
					else {
						world.setBlockState(coords.toPos(), Blocks.BOOKSHELF.getDefaultState(), 3);
					}
					removeFloorZones.add(coords);	
				}
			}
			else {
				// add carpet
				IBlockState carpet = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, dye);
				if (random.nextInt(100) < CARPET_PERCENT_CHANCE) {
					if (world.getBlockState(floorPos).isSideSolid(world, floorPos, EnumFacing.UP)) {
						// update the world
						world.setBlockState(coords.toPos(), carpet, 3);	
					}
				}
			}

			// replace the floor block with planks
			if (world.getBlockState(floorPos).isSideSolid(world, floorPos, EnumFacing.UP)) {
				world.setBlockState(floorPos, Blocks.PLANKS.getDefaultState());
			}
		}

		for (ICoords coords : wallZone) {
			IArchitecturalElement elem = Elements.WALL_AIR;
			if (hasSupport(world, coords, elem, provider.getLocation(coords, room))) {
				// get the x,z indexes
				int xIndex = coords.getX() - room.getCoords().getX();
				int zIndex = coords.getZ() - room.getCoords().getZ();
				
				if (((coords.getX() == room.getMinX() + 1 || coords.getX() == room.getMaxX() -1) && Math.abs(zIndex) % 4 == 0)
						|| ((coords.getZ() == room.getMinZ() + 1 || coords.getZ() == room.getMaxZ() -1) && Math.abs(xIndex) % 4 == 0)) {
					world.setBlockState(coords.toPos(), Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));					
				}
				else {
					// update the world
					world.setBlockState(coords.toPos(), Blocks.BOOKSHELF.getDefaultState(), 3);
				}
				// add the zone to the remove list
				removeWallZones.add(coords);
			}
		}

		for(ICoords c : removeFloorZones) {
			room.getFloorMap().remove(Elements.FLOOR_AIR, c);
		}

		for (ICoords c : removeWallZones) {
			room.getFloorMap().remove(Elements.WALL_AIR, c);
		}
		
		floorZone.removeAll(removeFloorZones);
		removeFloorZones.clear();
		// remove location from wallZone
		wallZone.removeAll(removeWallZones); // <--- this doesn't really matter
		removeWallZones.clear();

		// add shelves that extend from the wall

		// decorate as normal
		super.decorate(world, random, provider, room, config, engineConfig);
	}

}
