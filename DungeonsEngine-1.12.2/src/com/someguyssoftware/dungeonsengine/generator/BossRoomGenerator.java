/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.strategy.IRoomGenerationStrategy;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;

import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Aug 30, 2016
 *
 */
public class BossRoomGenerator extends AbstractRoomGenerator {

	private IRoomGenerationStrategy roomGenerationStrategy;
	
	/**
	 * Enforce that the room generator has to have a structure generator.
	 * @param generator
	 */
	public BossRoomGenerator(IRoomGenerationStrategy generator) {
		setGenerationStrategy(generator);
	}
	
	@Override
	public void generate(World world, Random random, IDecoratedRoom room, Theme theme, StyleSheet styleSheet,
			LevelConfig config) {
		
		// generate the room structure
		getGenerationStrategy().generate(world, random, room, theme, styleSheet, config);

	}

	/**
	 * @return the roomGenerationStrategy
	 */
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
