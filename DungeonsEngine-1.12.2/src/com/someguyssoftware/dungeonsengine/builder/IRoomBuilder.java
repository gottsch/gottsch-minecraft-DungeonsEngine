package com.someguyssoftware.dungeonsengine.builder;

import java.util.List;

import com.someguyssoftware.dungeonsengine.model.IRoom;

import net.minecraft.util.math.AxisAlignedBB;

public interface IRoomBuilder {

	/**
	 * 
	 * @return
	 */
	IRoom buildStartRoom();

	/**
	 * 
	 * @param plannedRooms
	 * @return
	 */
	IRoom buildEndRoom(List<IRoom> plannedRooms);

	/**
	 * 
	 * @param plannedRooms
	 * @return
	 */
	IRoom buildRoom(List<IRoom> plannedRooms);

}