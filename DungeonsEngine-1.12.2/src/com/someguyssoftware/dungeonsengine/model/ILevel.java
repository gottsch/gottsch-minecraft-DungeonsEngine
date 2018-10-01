package com.someguyssoftware.dungeonsengine.model;

import java.util.List;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;

public interface ILevel {

	/**
	 * @return the startRoom
	 */
	IRoom getStartRoom();

	/**
	 * @param startRoom the startRoom to set
	 */
	void setStartRoom(IRoom startRoom);

	/**
	 * @return the endRoom
	 */
	IRoom getEndRoom();

	/**
	 * @param endRoom the endRoom to set
	 */
	void setEndRoom(IRoom endRoom);

	/**
	 * @return the rooms
	 */
	List<IRoom> getRooms();

	/**
	 * @param rooms the rooms to set
	 */
	void setRooms(List<IRoom> rooms);

	/**
	 * 
	 * @return
	 */
	AxisAlignedBB getBoundingBox();

	/**
	 * Creates a bounding box by the XZ dimensions with a height (Y) of 1
	 * @return
	 */
	AxisAlignedBB getXZBoundingBox();

	/**
	 * @return the id
	 */
	int getId();

	/**
	 * @param id the id to set
	 */
	void setId(int id);

	/**
	 * @return the NAME
	 */
	String getName();

	/**
	 * @param NAME the NAME to set
	 */
	void setName(String name);

	/**
	 * @return the minX
	 */
	int getMinX();

	/**
	 * @param minX the minX to set
	 */
	void setMinX(int minX);

	/**
	 * @return the maxX
	 */
	int getMaxX();

	/**
	 * @param maxX the maxX to set
	 */
	void setMaxX(int maxX);

	/**
	 * @return the minY
	 */
	int getMinY();

	/**
	 * @param minY the minY to set
	 */
	void setMinY(int minY);

	/**
	 * @return the maxY
	 */
	int getMaxY();

	/**
	 * @param maxY the maxY to set
	 */
	void setMaxY(int maxY);

	/**
	 * @return the minZ
	 */
	int getMinZ();

	/**
	 * @param minZ the minZ to set
	 */
	void setMinZ(int minZ);

	/**
	 * @return the maxZ
	 */
	int getMaxZ();

	/**
	 * @param maxZ the maxZ to set
	 */
	void setMaxZ(int maxZ);

	/**
	 * @return the startPoint
	 */
	ICoords getStartPoint();

	/**
	 * @param startPoint the startPoint to set
	 */
	void setStartPoint(ICoords startPoint);

	/**
	 * @return the config
	 */
	LevelConfig getConfig();

	/**
	 * @param config the config to set
	 */
	void setConfig(LevelConfig config);

	/**
	 * @return the shafts
	 */
	List<IShaft> getShafts();

	/**
	 * @param shafts the shafts to set
	 */
	void setShafts(List<IShaft> shafts);

	List<Hallway> getHallways();

	void setHallways(List<Hallway> hallways);

	/**
	 * @return the spawnPoint
	 */
	ICoords getSpawnPoint();

	/**
	 * @param spawnPoint the spawnPoint to set
	 */
	void setSpawnPoint(ICoords spawnPoint);

	/**
	 * Convenience method
	 * @return
	 */
	int getDepth();

	/**
	 * Convenience method
	 * @return
	 */
	int getWidth();

	/**
	 * @return the field
	 */
	AxisAlignedBB getField();

	/**
	 * @param field the field to set
	 */
	void setField(AxisAlignedBB field);

}