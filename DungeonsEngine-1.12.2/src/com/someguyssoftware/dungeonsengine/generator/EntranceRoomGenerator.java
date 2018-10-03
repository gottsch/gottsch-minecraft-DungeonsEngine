/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.strategy.IRoomGenerationStrategy;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;

import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Aug 28, 2016
 *
 */
public class EntranceRoomGenerator extends AbstractExteriorRoomGenerator {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private IRoomGenerationStrategy roomGenerationStrategy;
	
	/**
	 * Enforce that the room generator has to have a structure generator.
	 * @param generator
	 */
	public EntranceRoomGenerator(IRoomGenerationStrategy generator) {
		setGenerationStrategy(generator);
	}
	
	@Override
	public void generate(World world, Random random, IDecoratedRoom room, Theme theme, StyleSheet styleSheet,
			LevelConfig config) {
		logger.debug("Has Crenellation:" + room.has(Elements.ElementsEnum.CRENELLATION));
		logger.debug("Has Parapet:"+ room.has(Elements.ElementsEnum.PARAPET));
		logger.debug("Has Merlon:" + room.has(Elements.ElementsEnum.MERLON));
		logger.debug("Has Cornice:" + room.has(Elements.ElementsEnum.CORNICE));
		logger.debug("Has Plinth:" + room.has(Elements.ElementsEnum.PLINTH));
		
		
		// generate the room structure
		getGenerationStrategy().generate(world, random, room, theme, styleSheet, config);
		
		/*
		 *  add additional elements
		 */
		
		// build doorway
		buildDoorway(world, room);
		
	}

	/**
	 * @return the roomGenerationStrategy
	 */
	@Override
	public IRoomGenerationStrategy getGenerationStrategy() {
		return roomGenerationStrategy;
	}

	/**
	 * @param roomGenerationStrategy the roomGenerationStrategy to set
	 */
	public void setGenerationStrategy(IRoomGenerationStrategy roomGenerationStrategy) {
		this.roomGenerationStrategy = roomGenerationStrategy;
	}

}
