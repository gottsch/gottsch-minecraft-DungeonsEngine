/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.someguyssoftware.dungeonsengine.chest.ChestSheet;
import com.someguyssoftware.dungeonsengine.chest.ChestSheetLoader;
import com.someguyssoftware.dungeonsengine.config.IDungeonsEngineConfig;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.IHallway;
import com.someguyssoftware.dungeonsengine.model.ILevel;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.IShaft;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.dungeonsengine.spawner.SpawnSheet;
import com.someguyssoftware.dungeonsengine.spawner.SpawnSheetLoader;
import com.someguyssoftware.dungeonsengine.style.BossRoomDecorator;
import com.someguyssoftware.dungeonsengine.style.DecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.IRoomDecorator;
import com.someguyssoftware.dungeonsengine.style.LayoutAssigner;
import com.someguyssoftware.dungeonsengine.style.LibraryRoomDecorator;
import com.someguyssoftware.dungeonsengine.style.RoomDecorator;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.StyleSheetLoader;

import net.minecraft.world.World;

/**
 * This class is responsible for building the dungeon in game.
 * 
 * @author Mark Gottschling on Jul 27, 2016
 *
 */
public class DungeonGenerator {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	// TODO either these shouldn't be static or they need to be loaded by static {}, not constructor or this class is singleton
	/*
	 * default stylesheet that is within the classpath of the mod jar
	 */
	private static StyleSheet defaultStyleSheet;

	/*
	 * default chestSheet that is within the classpath of the mod jar
	 */
	private static ChestSheet defaultChestSheet;
	
	/*
	 * default spawnSheet that is within the classpath of the mod jar
	 */
	private static SpawnSheet defaultSpawnSheet;
	
	// TODO should this be part of the RoomGeneratorFactory? - makes more sense
	private static Multimap<String, IRoomGenerator> roomGenerators = ArrayListMultimap.create();
	
	/**
	 * dungeon engine (mod) config
	 */
	IDungeonsEngineConfig engineConfig;
	
	/**
	 * TODO should throw custom exception
	 * @throws Exception 
	 * 
	 */
	public DungeonGenerator(IDungeonsEngineConfig engineConfig) throws Exception {
		// TODO is this is the default sheets, then they shouldn't be in the mods DungeonGenerator class
		// load the default style sheet
		if (defaultStyleSheet == null) {
			setDefaultStyleSheet(StyleSheetLoader.load());
		}
		if (defaultChestSheet == null) {
			setDefaultChestSheet(ChestSheetLoader.load());
		}
		if (defaultSpawnSheet == null) {
			setDefaultSpawnSheet(SpawnSheetLoader.load());
		}		
		
		this.engineConfig = engineConfig;
	}

	/**
	 * TODO return false if error or gen fails
	 * @param world
	 * @param random
	 * @param dungeon
	 * @param styleSheet
	 * @param chestSheet
	 * @param spawnSheet
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean generate(World world, Random random, IDungeon dungeon, StyleSheet styleSheet, ChestSheet chestSheet, SpawnSheet spawnSheet) throws FileNotFoundException {

		// if styleSheet is null then use the default style sheet
		if (styleSheet == null) {
			logger.warn("Provided style sheet is null. Using default style sheet.");
			styleSheet = DungeonGenerator.getDefaultStyleSheet();
		}
		
		/*
		 *  create a room generator factory
		 */
		RoomGeneratorFactory factory = new RoomGeneratorFactory(roomGenerators);
		factory.defaultStyleSheet = styleSheet; // TODO fix this
		
		/*
		 * 
		 */
		IRoomGenerator roomGen = null;

		/*
		 * a layout assigner. it determine the layout to use for each room and what design elements to enable
		 */
		LayoutAssigner layoutAssigner = new LayoutAssigner(styleSheet);
		
		/*
		 * create the room decorators
		 */
		IRoomDecorator roomDecorator = new RoomDecorator(chestSheet, spawnSheet);
		IRoomDecorator bossRoomDecorator = new BossRoomDecorator(chestSheet);
		IRoomDecorator libraryDecorator = new LibraryRoomDecorator(chestSheet, spawnSheet);
		
		/*
		 *  NOTE careful here. IRoomGenerator can alter the state of the IGenerationStrategy with a
		 *  IDungeonsBlockProvider of it's choosing. Don't share between generators or have to synchronize
		 */		

		// build the entrance
		buildEntrance(world, random, dungeon, layoutAssigner, factory, roomDecorator, styleSheet);

		// TODO need to keep the abstract dungeon/levels/rooms separate from the decorated dungeon/levels/rooms.
		// TODO will need to create DecoratedRoom instance for every room in the dungeon, and then pass that forward.
		/*
		 * build all the levels 
		 */
		int levelCount = 0;
		int libraryCount = 0;
		// generate all the rooms
		for (ILevel level : dungeon.getLevels()) {
			logger.debug("Level: " + levelCount);
//			logger.debug("Is Level Support On? " + level.getConfig().isSupportOn());
			// build the rooms for the level
			for (IRoom room : level.getRooms()) {
				// create a new decorated room
				IDecoratedRoom decoratedRoom = new DecoratedRoom(room);
				// assign a layout to the room 
				layoutAssigner.assign(random, decoratedRoom); 
				
				// TODO change the factory method - should take in IRoom to create a DecoratedRoomGenerator
				// get the room generator
				roomGen = factory.createRoomGenerator(random, decoratedRoom, level.getConfig().isSupportOn());

				
				// generate the room into the world
				roomGen.generate(world, random, decoratedRoom, dungeon.getTheme(), styleSheet, level.getConfig());
				
				// TODO need a decorator factory
				if (room.getType() == Type.BOSS) {
					bossRoomDecorator.decorate(
							world, random, 
							roomGen.getGenerationStrategy().getBlockProvider(), 
							decoratedRoom, 
							level.getConfig(), 
							engineConfig);
				}
				
				/*
				 * TODO this should be a random selection of special rooms
				 * TODO there should be something to describe where a special room can occur (ex levels 4-6)
				 *  select any special decorators
				 */					
				// ensure room fits the criteria to host a library	
				else if (room.getWidth() > 5
						&& room.getDepth() > 5
						&& room.getHeight() >=5
						&& !decoratedRoom.has(Elements.ElementsEnum.PILLAR)
						&& random.nextInt(100) < 10
						&& libraryCount < 3) {
					logger.debug("Using library decorator for room @ " + room.getCoords().toShortString());
						libraryDecorator.decorate(world, random, 
								roomGen.getGenerationStrategy().getBlockProvider(), 
								decoratedRoom, level.getConfig(), engineConfig);
						libraryCount++;
				}
				else {
					// decorate the room (spawners, chests, webbings, etc)
					roomDecorator.decorate(world, random, 
							roomGen.getGenerationStrategy().getBlockProvider(), 
							decoratedRoom, level.getConfig(), engineConfig);
				}
			
				// TODO add to JSON output

			}
			
			// create a list of generated hallways
			List<IHallway> generatedHallways = new ArrayList<>();
			// generate the hallways
			for (IHallway hallway : level.getHallways()) {
				DecoratedRoom decoratedHallway = new DecoratedRoom(hallway);
				// assign a layout
				layoutAssigner.assign(random, decoratedHallway);
				// NOTE passing hallways here is a list of hallways (excluding the current one, to check if they intersect
				roomGen = factory.createHallwayGenerator(decoratedHallway, level.getRooms(), generatedHallways, level.getConfig().isSupportOn());
				roomGen.generate(world, random, decoratedHallway, dungeon.getTheme(), styleSheet, level.getConfig());
				// add the hallway to the list of generated hallways
				generatedHallways.add(hallway);
			}
			
			// generate the shafts
			for (IShaft shaft : level.getShafts()) {
				DecoratedRoom decoratedShaft = new DecoratedRoom(shaft);
//				logger.debug("Building Shaft: " + shaft);
				// assign the layout
				// TODO the source room no longer has layouts - where to get the layout from?
				layoutAssigner.assign(random, decoratedShaft);
//				shaft.setLayout(shaft.getSource().getLayout());
				roomGen = factory.createShaftGenerator(decoratedShaft, level.getConfig().isSupportOn());
				roomGen.generate(world, random, decoratedShaft, dungeon.getTheme(), styleSheet, level.getConfig());
			}
			levelCount++;
		}
		return true;
	}

	/**
	 * 
	 * @param world
	 * @param random
	 * @param dungeon
	 * @param layoutAssigner
	 * @param factory
	 * @param roomDecorator
	 */
	private void buildEntrance(World world, Random random,
			IDungeon dungeon, LayoutAssigner layoutAssigner, RoomGeneratorFactory factory,
			IRoomDecorator roomDecorator, StyleSheet styleSheet) {
		
		IDecoratedRoom entranceRoom = new DecoratedRoom(dungeon.getEntrance());
		// create and setup a config for entrance
		LevelConfig entranceLevelConfig = dungeon.getLevels().get(0).getConfig().copy();
		entranceLevelConfig.setDecayMultiplier(Math.min(5, entranceLevelConfig.getDecayMultiplier())); // increase the decay multiplier to a minimum of 5
		// assign a layout to the entrance room
		layoutAssigner.assign(random, entranceRoom);
		IRoomGenerator roomGen = factory.createRoomGenerator(random, entranceRoom, dungeon.getLevels().get(0).getConfig().isSupportOn());
		// TODO need to provide the entrance room generator with a different level config that uses a higher decay multiplier
		// to create a much more decayed surface structure.
		roomGen.generate(world, random, entranceRoom, dungeon.getTheme(), styleSheet, entranceLevelConfig);
		roomDecorator.decorate(world, random, 
				roomGen.getGenerationStrategy().getBlockProvider(), entranceRoom, entranceLevelConfig, engineConfig);
	}

	/**
	 * @return the defaultStyleSheet
	 */
	public static StyleSheet getDefaultStyleSheet() {
		return defaultStyleSheet;
	}

	/**
	 * @param defaultStyleSheet the defaultStyleSheet to set
	 */
	private void setDefaultStyleSheet(StyleSheet defaultStyleSheet) {
		DungeonGenerator.defaultStyleSheet = defaultStyleSheet;
	}

	/**
	 * @return the defaultChestSheet
	 */
	public static ChestSheet getDefaultChestSheet() {
		return defaultChestSheet;
	}

	/**
	 * @param defaultChestSheet the defaultChestSheet to set
	 */
	public static void setDefaultChestSheet(ChestSheet defaultChestSheet) {
		DungeonGenerator.defaultChestSheet = defaultChestSheet;
	}

	/**
	 * @return the defaultSpawnSheet
	 */
	public static SpawnSheet getDefaultSpawnSheet() {
		return defaultSpawnSheet;
	}

	/**
	 * @param defaultSpawnSheet the defaultSpawnSheet to set
	 */
	public static void setDefaultSpawnSheet(SpawnSheet defaultSpawnSheet) {
		DungeonGenerator.defaultSpawnSheet = defaultSpawnSheet;
	}
}
