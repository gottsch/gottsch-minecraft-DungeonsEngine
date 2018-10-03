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
import com.someguyssoftware.dungeonsengine.generator.ISupportedBlock;
import com.someguyssoftware.dungeonsengine.generator.SupportedBlock;
import com.someguyssoftware.dungeonsengine.generator.SupportedBlockProcessor;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.model.IHallway;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 9, 2016
 *
 */
public class SupportedHallwayGenerationStrategy extends AbstractRoomGenerationStrategy {
	/*
	 * a list of all the rooms in the level 
	 */
	private List<IDecoratedRoom> rooms;
	
	/*
	 * a list of generated hallways
	 */
	private List<IHallway> hallways;
	
	/**
	 * 
	 * @param blockProvider
	 * @param rooms
	 * @param hallways
	 */
	public SupportedHallwayGenerationStrategy(IDungeonsBlockProvider blockProvider, List<IDecoratedRoom> rooms, List<IHallway> hallways) {
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
		IHallway hallway = (IHallway)room;
		IBlockState blockState = null;
		Map<ICoords, Arrangement> postProcessMap = new HashMap<>();
		Multimap<IArchitecturalElement, ICoords> blueprint = room.getFloorMap();

		SupportedBlockProcessor supportProcessor = new SupportedBlockProcessor(getBlockProvider(), room);
		ISupportedBlock supportedBlock = null;
		
		// collect a list of rooms that the hallway intersects against
		List<Room> intersectRooms = new ArrayList<>();
		for (IDecoratedRoom otherRoom : getRooms()) {
			if (room.getBoundingBox().intersects(otherRoom.getBoundingBox())) {
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

					// update support calculations for air
					if (blockState == null || arrangement.getElement() == Elements.AIR || blockState == Blocks.AIR.getDefaultState() || blockState == IDungeonsBlockProvider.NULL_BLOCK) {
						// create a supported block instance
						supportedBlock = new SupportedBlock(blockState, 100); // 100 = the block as been processed and is in the world
						// update the world with the blockState
						if (blockState == Blocks.AIR.getDefaultState()) {
//							Dungeons2.log.debug("Updating hallway with AIR @ " + worldCoords.toShortString());
							// update the world
							world.setBlockState(worldCoords.toPos(), blockState, 3);
							// add the design element to the blueprint (if floor level)
							if (worldCoords.getY() == room.getMinY() + 1) blueprint.put(arrangement.getElement(), worldCoords);
						}
					}
					else {
						// NOTE we already know at this point that the design element is not AIR				
						boolean buildBlock = isBlockBuildable(worldCoords, hallway, intersectRooms);
//Dungeons2.log.debug(String.format("Supported buildBlock %s: %b", blockState.getBlock().getRegistryName(), buildBlock));
						// update the world with the blockState
						if (blockState != null && buildBlock && blockState != DungeonBuilder.NULL_BLOCK) {
							// apply the pass 1 support 
							// perform support rules and set the supportedBlock array
							int amount = supportProcessor.applySupportRulesPass1(world, indexCoords, worldCoords, arrangement.getElement());
//							Dungeons2.log.debug("Pass 1 Support amount:" + amount);
							if (amount >= 100) {
								supportedBlock = new SupportedBlock(blockState, 100);			
								world.setBlockState(worldCoords.toPos(), blockState, 3);
							}
							else {
								supportedBlock = new SupportedBlock(blockState, amount);
							}							
						}
						else {
							// create a supported block instance of null block
							supportedBlock = new SupportedBlock(DungeonBuilder.NULL_BLOCK, 100); // 100 = the block as been processed and is in the world
//							Dungeons2.log.debug("Adding NULL BLOCK @" + worldCoords.toShortString());
						}			
					}
					// update the supported block matrix
					supportProcessor.getSupportMatrix()[y][z][x] = supportedBlock;
				}
			}			
			
			// second pass
			for (int z = room.getDepth() -1; z >= 0; z--) {
				for (int x = room.getWidth() -1; x >=0; x--) {								
					// check matrix if this entry is less than 100, ie still need checks to determine if to place
					supportedBlock = supportProcessor.getSupportMatrix()[y][z][x];
					if (supportedBlock == null || supportedBlock.getAmount() < 100 ) {

						// create index coords
						ICoords indexCoords = new Coords(x, y, z);
						// get the world coords
						ICoords worldCoords = room.getCoords().add(indexCoords);				

						// get the element arrangement
						Arrangement arrangement = getBlockProvider().getArrangement(worldCoords, room, room.getLayout());

						if (arrangement.getElement() != Elements.AIR) {							
							// get the block state
							blockState = getBlockProvider().getBlockState(random, worldCoords, room, arrangement, theme, styleSheet, config);
						}
						else {
							blockState = Blocks.AIR.getDefaultState();
						}
						
						// if the block is air, update the world
						if (blockState == Blocks.AIR) {
								world.setBlockState(worldCoords.toPos(), blockState, 3);
						}
						// else calculate the support
						else {
							// create a supported block with 0 support
							supportedBlock = new SupportedBlock(blockState, 0);
							// determine if the block should be built
							boolean buildBlock = isBlockBuildable(worldCoords, hallway, intersectRooms);
						
							if (blockState != null && buildBlock && blockState != DungeonBuilder.NULL_BLOCK) {
								// perform support rules to determine amount of support
								int amount = supportProcessor.applySupportRulesPass2(world, indexCoords, worldCoords, arrangement.getElement());
//								Dungeons2.log.debug("Pass 2 Support amount:" + amount);
								// update supportBlock's amount of support
								supportedBlock.setAmount(supportedBlock.getAmount() + amount);
								
								// if amount is now greated than threshold, update the world
								if (supportedBlock.getAmount() >= 100) {
									world.setBlockState(worldCoords.toPos(), blockState, 3);
									if (worldCoords.getY() == room.getMinY() + 1) blueprint.put(arrangement.getElement(), worldCoords);
								}
							}
						}
					}					
				}
			}
			
			// generate the post processing blocks
			postProcess(world, random, postProcessMap, room.getLayout(), theme, styleSheet, config);	
			
			// TODO should we override postProcess for Supported that is a Supported VERSION?
		}
	}

	/**
	 * 
	 * @param worldCoords
	 * @param hallway
	 * @param intersectRooms
	 * @return
	 */
	public boolean isBlockBuildable(ICoords worldCoords, IHallway hallway, List<Room> intersectRooms) {
		// NOTE we already know at this point that the design element is not AIR				
		AxisAlignedBB box = new AxisAlignedBB(worldCoords.toPos());
		boolean buildBlock = true;						
		IRoom room = (IRoom)hallway;
		// get the bounding boxes of the rooms the doors are connected to
		// NOTE may have to change to list in the future if more than 2 doors per hall
		AxisAlignedBB bb1 = room.getDoors().size() > 0 &&
				room.getDoors().get(0) != null ? room.getDoors().get(0).getRoom().getBoundingBox() : null;
		AxisAlignedBB bb2 = room.getDoors().size() > 1 && 
				room.getDoors().get(1) != null ? room.getDoors().get(1).getRoom().getBoundingBox() : null;
		
		// first check the wayline rooms
		if ((bb1 != null && box.intersects(bb1)) || (bb2 != null && box.intersects(bb2))) {
			buildBlock = false;
		}

		// second, check against any rooms in the level that the hallway intersects with
		if (buildBlock) {
			for (Room r : intersectRooms) {
				AxisAlignedBB bb = r.getBoundingBox();
				if (box.intersects(bb)) {
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
//					Dungeons2.log.debug(String.format("Supported Hallway @ %s intersects with hallway @ %s", box, bb));
					buildBlock = false;
					break;
				}
			}
		}
		
		return buildBlock;
	}
	
	/**
	 * @return the rooms
	 */
	public List<IDecoratedRoom> getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(List<IDecoratedRoom> rooms) {
		this.rooms = rooms;
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
}
