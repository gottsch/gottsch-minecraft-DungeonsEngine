/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Aug 18, 2016
 *
 */
public interface IDungeonBuilder {

	public Dungeon EMPTY_DUNGEON = new Dungeon();

	/**
	 * 
	 * @param world
	 * @param rand
	 * @param startPoint
	 * @param config
	 * @return
	 */
	Dungeon build(World world, Random rand, ICoords startPoint, DungeonConfig config);

	/**
	 * @return the levelBuilder
	 */
	LevelBuilder getLevelBuilder();

	/**
	 * @param levelBuilder the levelBuilder to set
	 */
	void setLevelBuilder(LevelBuilder levelBuilder);

	/**
	 * @param world
	 * @param rand
	 * @param startPoint
	 * @return
	 */
	IRoom buildEntranceRoom(World world, Random rand, ICoords startPoint);

}