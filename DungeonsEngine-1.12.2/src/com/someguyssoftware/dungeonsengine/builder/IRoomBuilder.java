package com.someguyssoftware.dungeonsengine.builder;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;

public interface IRoomBuilder {
	public static final IRoom EMPTY_ROOM = new Room();
	
	IRoom buildRoom(Random random, ICoords startPoint, LevelConfig config, IRoom roomIn);
	/*
	 * build a generic room
	 */
	IRoom buildRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, IRoom roomIn);
	
	/*
	 * build a 'planned' room, using existing rooms to determine where and if it can be placed within the field
	 */
	IRoom buildPlannedRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms);
	
	IRoom buildStartRoom(Random random, ICoords startPoint, LevelConfig config);
	/**
	 * build a start room
	 * @param random
	 * @param field
	 * @param startPoint
	 * @param config
	 * @param roomIn
	 * @return
	 */
	IRoom buildStartRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config);
	
	IRoom buildEndRoom(Random random, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms);
	/**
	 * build an end room
	 * @param random
	 * @param field
	 * @param startPoint
	 * @param config
	 * @param plannedRooms
	 * @return
	 */
	IRoom buildEndRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms);

	AxisAlignedBB getField();
	void setField(AxisAlignedBB field);
	
	/**
	 * 
	 * @param random
	 * @param startPoint
	 * @param config
	 * @param plannedRooms
	 * @return
	 */
	IRoom buildTreasureRoom(Random random, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms);
	/**
	 * 
	 * @param random
	 * @param field
	 * @param startPoint
	 * @param config
	 * @param plannedRooms
	 * @return
	 */
	IRoom buildTreasureRoom(Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config, List<IRoom> plannedRooms);
	
}