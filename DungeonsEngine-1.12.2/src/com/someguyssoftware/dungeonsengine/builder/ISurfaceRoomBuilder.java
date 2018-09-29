package com.someguyssoftware.dungeonsengine.builder;

import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;

public interface ISurfaceRoomBuilder {

	/**
	 * 
	 * @param random
	 * @param field
	 * @param startPoint
	 * @param config
	 * @return
	 */
	public IRoom buildEntranceRoom(Random random, ICoords startPoint, LevelConfig config);

}