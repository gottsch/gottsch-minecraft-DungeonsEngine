package com.someguyssoftware.dungeonsengine.generator.strategy;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.blockprovider.IDungeonsBlockProvider;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;

import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Oct 1, 2018
 *
 */
public interface IRoomGenerationStrategy {

	/**
	 * 
	 * @param world
	 * @param random
	 * @param room
	 * @param theme
	 * @param styleSheet
	 * @param config
	 */
	public void generate(World world, Random random, IDecoratedRoom room, Theme theme, StyleSheet styleSheet, LevelConfig config);
	
	public IDungeonsBlockProvider getBlockProvider();
//	public void setBlockProvider(IDungeonsBlockProvider blockProvider);

	
	
}