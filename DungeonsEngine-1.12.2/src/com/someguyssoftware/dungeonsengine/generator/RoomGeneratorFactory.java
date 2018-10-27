/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.BossRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.CheckedFloorRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.EndRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.EntranceRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.HallwayBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.PillarRingRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.ShaftBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.SinglePillarRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.StandardBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.StartRoomBlockProvider;
import com.someguyssoftware.dungeonsengine.generator.strategy.StandardHallwayGenerationStrategy;
import com.someguyssoftware.dungeonsengine.generator.strategy.StandardRoomGenerationStrategy;
import com.someguyssoftware.dungeonsengine.generator.strategy.SupportedHallwayGenerationStrategy;
import com.someguyssoftware.dungeonsengine.generator.strategy.SupportedRoomGenerationStrategy;
import com.someguyssoftware.dungeonsengine.model.IHallway;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;

/**
 * @author Mark Gottschling on Aug 28, 2016
 *
 */
public class RoomGeneratorFactory {
	Multimap<String, IRoomGenerator> registry;
	StyleSheet defaultStyleSheet;
	
	public RoomGeneratorFactory(StyleSheet styleSheet) {
		this.defaultStyleSheet = styleSheet;
	}
	
	// TODO finish this
	public RoomGeneratorFactory(Multimap<String, IRoomGenerator> registry) {
		this.registry = registry;
		/*
		 * add all standard rooms
		 */
		// add all rooms
		registry.put("standard-room", new RoomGenerator(new StandardRoomGenerationStrategy(new StandardBlockProvider(defaultStyleSheet))));
		registry.put("standard-room", new RoomGenerator(new StandardRoomGenerationStrategy(new PillarRingRoomBlockProvider(defaultStyleSheet))));
		registry.put("standard-room", new RoomGenerator(new StandardRoomGenerationStrategy(new SinglePillarRoomBlockProvider(defaultStyleSheet))));
		registry.put("standard-room", new RoomGenerator(new StandardRoomGenerationStrategy(new CheckedFloorRoomBlockProvider(defaultStyleSheet))));
		// add all entrance rooms
		registry.put("standard-entrance", new EntranceRoomGenerator(new StandardRoomGenerationStrategy(new EntranceRoomBlockProvider(defaultStyleSheet))));
		// add all start rooms
		
		// add all end rooms
		
		// add all boss rooms
		
		// add all treasure rooms
		
		/*
		 * add all supported rooms
		 */
		registry.put("supported-room", new RoomGenerator(new SupportedRoomGenerationStrategy(new StandardBlockProvider(defaultStyleSheet))));
		registry.put("supported-room", new RoomGenerator(new SupportedRoomGenerationStrategy(new PillarRingRoomBlockProvider(defaultStyleSheet))));
		registry.put("supported-room", new RoomGenerator(new SupportedRoomGenerationStrategy(new SinglePillarRoomBlockProvider(defaultStyleSheet))));
		registry.put("supported-room", new RoomGenerator(new SupportedRoomGenerationStrategy(new CheckedFloorRoomBlockProvider(defaultStyleSheet))));
		// add all entrance rooms
		registry.put("supported-entrance", new EntranceRoomGenerator(new SupportedRoomGenerationStrategy(new EntranceRoomBlockProvider(defaultStyleSheet))));

	}
	
	/**
	 * Convenience method
	 * @param room
	 * @param useSupport
	 * @return
	 */
	public IRoomGenerator createRoomGenerator(Random random, IDecoratedRoom room, Boolean useSupport) {
		if (useSupport) {
			return createSupportedRoomGenerator(random, room);
		}
		else {
			return createStandardRoomGenerator(random, room);
		}		
	}
	
	/**
	 * 
	 * @param hallway
	 * @param rooms
	 * @param hallways A list of hallways to check against for intersection.
	 * @param useSupport
	 * @return
	 */
	public IRoomGenerator createHallwayGenerator(IDecoratedRoom hallway, List<IRoom> rooms, List<IHallway> hallways, Boolean useSupport) {
		if (useSupport) {
			return createSupportedHallwayGenerator(hallway, rooms, hallways);
		}
		else {
			return createStandardHallwayGenerator(hallway, rooms, hallways);
		}
	}
	
	/**
	 * 
	 * @param room
	 * @param useSupport
	 * @return
	 */
	public IRoomGenerator createShaftGenerator(IDecoratedRoom shaft, Boolean useSupport) {
		if (useSupport) {
			return createSupportedShaftGenerator(shaft);
		}
		else {
			return createStandardShaftGenerator(shaft);
		}
	}
	
	/**
	 * 
	 * @param room
	 * @return
	 */
	public IRoomGenerator createStandardRoomGenerator(Random random, IDecoratedRoom room) {
		IRoomGenerator gen = null;
		if (room.getType() == Type.ENTRANCE) {
			gen = new EntranceRoomGenerator(new StandardRoomGenerationStrategy(new EntranceRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.isStart()) {
			gen = new RoomGenerator(new StandardRoomGenerationStrategy(new StartRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.isEnd() && room.getType() != Type.BOSS) {
			gen = new RoomGenerator(new StandardRoomGenerationStrategy(new EndRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.getType() == Type.BOSS) {
			gen = new BossRoomGenerator(new StandardRoomGenerationStrategy(new BossRoomBlockProvider(defaultStyleSheet)));
		}
		else {
			if (registry != null && registry.containsKey("standard-room")) {
				List<IRoomGenerator> list = (List<IRoomGenerator>) registry.get("standard-room");
				gen = list.get(random.nextInt(list.size()));
			}
			else {
				gen = new RoomGenerator(new StandardRoomGenerationStrategy(new StandardBlockProvider(defaultStyleSheet)));
			}
		}
		return gen;
	}
	
	/**
	 * 
	 * @param room
	 * @return
	 */
	public IRoomGenerator createSupportedRoomGenerator(Random random, IDecoratedRoom room) {
		IRoomGenerator gen = null;
		if (room.getType() == Type.ENTRANCE	) {
			gen = new EntranceRoomGenerator(new SupportedRoomGenerationStrategy(new EntranceRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.isStart()) {
			gen = new RoomGenerator(new SupportedRoomGenerationStrategy(new StartRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.isEnd() && room.getType() != Type.BOSS) {
			gen = new RoomGenerator(new SupportedRoomGenerationStrategy(new EndRoomBlockProvider(defaultStyleSheet)));
		}
		else if (room.getType() == Type.BOSS) {
			gen = new BossRoomGenerator(new SupportedRoomGenerationStrategy(new BossRoomBlockProvider(defaultStyleSheet)));
		}
		else {
			if (registry != null && registry.containsKey("supported-room")) {
				List<IRoomGenerator> list = (List<IRoomGenerator>) registry.get("supported-room");
				gen = list.get(random.nextInt(list.size()));
			}
			else {
				gen = new RoomGenerator(new SupportedRoomGenerationStrategy(new StandardBlockProvider(defaultStyleSheet)));
			}
//			gen = new RoomGenerator(new SupportedRoomGenerationStrategy(new StandardBlockProvider()));
		}
		return gen;		
	}
	
	/**
	 * 
	 * @param hallway
	 * @param hallways 
	 * @return
	 */
	public IRoomGenerator createStandardHallwayGenerator(IDecoratedRoom hallway, List<IRoom> rooms, List<IHallway> hallways) {
		HallwayGenerator gen = null;
		gen = new HallwayGenerator(new StandardHallwayGenerationStrategy(new HallwayBlockProvider(defaultStyleSheet), rooms, hallways));
		return gen;
	}
	
	/**
	 * 
	 * @param hallway
	 * @param rooms
	 * @param hallways
	 * @return
	 */
	public IRoomGenerator createSupportedHallwayGenerator(IDecoratedRoom hallway, List<IRoom> rooms, List<IHallway> hallways) {
		HallwayGenerator gen = new HallwayGenerator(new SupportedHallwayGenerationStrategy(new HallwayBlockProvider(defaultStyleSheet), rooms, hallways));
		return gen;
	}
	
	/**
	 * 
	 * @param shaft
	 * @return
	 */
	public IRoomGenerator createStandardShaftGenerator(IDecoratedRoom shaft) {
		IRoomGenerator gen = null;
		gen = new RoomGenerator(new StandardRoomGenerationStrategy(new ShaftBlockProvider(defaultStyleSheet)));
		return gen;
	}
	
	/**
	 * 
	 * @param shaft
	 * @return
	 */
	public IRoomGenerator createSupportedShaftGenerator(IDecoratedRoom shaft) {
		IRoomGenerator gen = new RoomGenerator(new SupportedRoomGenerationStrategy(new ShaftBlockProvider(defaultStyleSheet)));
		return gen;
	}
}
