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
 * @author Mark Gottschling on Jul 30, 2016
 *
 */
public interface IRoomGenerator {

	/**
	 * @param world
	 * @param coords
	 * @param room
	 * @param layout
	 * @param styleSheet
	 */
	public void generate(World world, Random random, IDecoratedRoom room, Theme theme, StyleSheet styleSheet, LevelConfig config);
	
	public IRoomGenerationStrategy getGenerationStrategy();
}