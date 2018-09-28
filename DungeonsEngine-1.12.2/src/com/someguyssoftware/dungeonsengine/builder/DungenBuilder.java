/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Dungeon;
import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public class DungenBuilder implements IDungeonBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private ILevelBuilder levelBuilder;
	
	/**
	 * 
	 */
	public DungenBuilder() {

	}

	/**
	 * 
	 */
	@Override
	public IDungeon build(World world, Random rand, AxisAlignedBB field, ICoords startPoint, DungeonConfig config) {
		/*
		 * The resultant dungeon object that contains all levels and the entrance of the dungeon.
		 */
		IDungeon dungeon = new Dungeon(config);
		
		/*
		 * ensure that all required objects exist and all conditions are met
		 */
		if (!performValidations(world, startPoint)) return EMPTY_DUNGEON;
		
		//  2. get a valid surface location		
		ICoords surfaceCoords = null;
		if (levelBuilder.getConfig().isMinecraftConstraintsOn()) {
			surfaceCoords = WorldInfo.getDryLandSurfaceCoords(world, startPoint);
			if (surfaceCoords == null || surfaceCoords == EMPTY_DUNGEON) {
				logger.debug("Not a valid dry land surface @ {}", startPoint.toShortString());
				return EMPTY_DUNGEON;
			}
		}
		else {
			surfaceCoords = startPoint;
		}
		logger.debug("SurfaceCoords -> {}", surfaceCoords.toShortString());
		
		return null;
	}

	/**
	 * 
	 * @param startPoint
	 * @return
	 */
	public boolean performValidations(World world, ICoords startPoint) {
		/*
		 * 0. ensure a level builder exists
		 */
		if (this.getLevelBuilder() == null) {
			logger.debug("A level builder is required.");
			return false;
		}
		
		/*
		 * Perform all the minecraft world contraint checks
		 */
		// 1. determine if valid coords
		if (getLevelBuilder().getConfig().isMinecraftConstraintsOn() && !WorldInfo.isValidY(startPoint)) {
			logger.debug("{} is not a valid y value.", startPoint.getY());
			return false;
		}
		

		
		return true;
	}
	
	
	@Override
	public ILevelBuilder getLevelBuilder() {
		return levelBuilder;
	}

	@Override
	public void setLevelBuilder(ILevelBuilder levelBuilder) {
		this.levelBuilder = levelBuilder;
	}

}
