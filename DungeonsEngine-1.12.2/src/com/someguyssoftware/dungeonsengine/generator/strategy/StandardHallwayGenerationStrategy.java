/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Multimap;
import com.someguyssoftware.dungeonsengine.builder.DungeonBuilder;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.Arrangement;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.IDungeonsBlockProvider;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.model.IHallway;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Builds a structure using the base rule set ie. all blocks are generated regardless of location, adjacent blocks etc.
 * @author Mark Gottschling on Aug 27, 2016
 *
 */
public class StandardHallwayGenerationStrategy extends AbstractRoomGenerationStrategy {
	/*
	 * a list of all the rooms in the level 
	 */
	private List<IRoom> rooms;
	
	/*
	 * a list of generated hallways
	 */
	private List<IHallway> hallways;
		
//	/**
//	 * 
//	 * @param blockProvider
//	 */
//	public StandardHallwayGenerationStrategy(IDungeonsBlockProvider blockProvider) {
//		setBlockProvider(blockProvider);
//	}
	
	/**
	 * 
	 * @param blockProvider
	 * @param rooms
	 * @param hallways
	 */
	public StandardHallwayGenerationStrategy(IDungeonsBlockProvider blockProvider, List<IRoom> rooms, List<IHallway> hallways) {
		super(blockProvider);
		//		setBlockProvider(blockProvider);
		setRooms(rooms);
		setHallways(hallways);
	}
	
	/**
	 * 
	 */
	@Override
	public void generate(World world, Random random, IDecoratedRoom room, Theme theme, StyleSheet styleSheet, LevelConfig config) {
		IHallway hallway = (IHallway)room.getRoom();
		IBlockState blockState = null;
		Map<ICoords, Arrangement> postProcessMap = new HashMap<>();
		Multimap<IArchitecturalElement, ICoords> blueprint = room.getFloorMap();
		
		// collect a list of rooms that the hallway intersects against
		List<IRoom> intersectRooms = new ArrayList<>();
		for (IRoom otherRoom : getRooms()) {
			if (((IRoom)hallway).getBoundingBox().intersects(otherRoom.getBoundingBox())) {
//				Dungeons2.log.debug("Hallway intersects with Room: " + room);
				intersectRooms.add(otherRoom);
			}
		}
		
		// generate the room
		for (int y = 0; y < room.getHeight(); y++) {
			// first pass
			for (int z = 0; z < room.getDepth(); z++) {
				for (int x = 0; x < room.getWidth(); x++) {

					// create index coords
					ICoords indexCoords = new Coords(x, y, z);
					// get the world coords
					ICoords worldCoords = room.getCoords().add(indexCoords);
					
					// get the design arrangement of the block @ xyz
					Arrangement arrangement = getBlockProvider().getArrangement(worldCoords, room, room.getLayout());
					
					// if element is of a type that requires post-processing, save for processing after the rest of the room is generated
					if (isPostProcessed(arrangement, worldCoords, postProcessMap)) continue;
					
					// get the block state
					blockState = getBlockProvider().getBlockState(random, worldCoords, room, arrangement, theme, styleSheet, config);
					if (blockState == DungeonBuilder.NULL_BLOCK) continue;
					
					AxisAlignedBB box = new AxisAlignedBB(worldCoords.toPos());
					boolean buildBlock = true;
					if (arrangement.getElement() != Elements.AIR) {
						// get the bounding boxes of the rooms the doors are connected to
						// NOTE may have to change to list in the future if more than 2 doors per hall
						AxisAlignedBB bb1 = hallway.getDoors().size() > 0 &&
								hallway.getDoors().get(0) != null ? hallway.getDoors().get(0).getRoom().getBoundingBox() : null;
						AxisAlignedBB bb2 = hallway.getDoors().size() > 1 && 
								hallway.getDoors().get(1) != null ? hallway.getDoors().get(1).getRoom().getBoundingBox() : null;
						
						// first check the wayline rooms
						if ((bb1 != null && box.intersects(bb1)) || (bb2 != null && box.intersects(bb2))) {
							buildBlock = false;
						}
						
						// second, check against any rooms in the level that the hallway intersects with
						if (buildBlock) {
							for (IRoom r : intersectRooms) {
								AxisAlignedBB bb = r.getBoundingBox();
								if (box.intersects(bb)) {
//									Dungeons2.log.debug(String.format("Hallway @ %s intersects with room @ %s", box, bb));
									buildBlock = false;
									break;								
								}
							}
						}
						
						// lastly, check against all other hallways
						if (buildBlock) {
							for (IHallway r : getHallways()) {
								AxisAlignedBB bb = ((IRoom)r).getBoundingBox();
								if (box.intersects(bb)) {
//									Dungeons2.log.debug(String.format("Hallway @ %s intersects with hallway @ %s", box, bb));
									buildBlock = false;
									break;
								}
							}
						}
						
					}

					// update the world with the blockState
					if (blockState != null && buildBlock && blockState != DungeonBuilder.NULL_BLOCK) {
						world.setBlockState(worldCoords.toPos(), blockState, 3);
					}
				}				
			}
		}
		
		// generate the post processing blocks
		postProcess(world, random, postProcessMap, room.getLayout(), theme, styleSheet, config);	

	}

	/**
	 * @return the hallways
	 */
	public List<IHallway> getHallways() {
		return hallways;
	}

	/**
	 * @param hallways the hallways to set
	 */
	public void setHallways(List<IHallway> hallways) {
		this.hallways = hallways;
	}

	/**
	 * @return the rooms
	 */
	public List<IRoom> getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(List<IRoom> rooms) {
		this.rooms = rooms;
	}
}
