/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Aug 18, 2016
 *
 */
public interface IDungeonBuilder {

	public IDungeon EMPTY_DUNGEON = new Dungeon();

	/**
	 * 
	 * @param world
	 * @param rand
	 * @param startPoint
	 * @param config
	 * @return
	 */
	IDungeon build(World world, Random rand, AxisAlignedBB field, ICoords startPoint, DungeonConfig config);

	/**
	 * @return the levelBuilder
	 */
	ILevelBuilder getLevelBuilder();

	/**
	 * @param levelBuilder the levelBuilder to set
	 */
	void setLevelBuilder(ILevelBuilder levelBuilder);

}