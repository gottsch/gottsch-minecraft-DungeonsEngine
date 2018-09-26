package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.ICoords;

public interface IDoor {

	/**
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 
	 * @param id
	 */
	void setId(int id);

	/**
	 * @return the coords
	 */
	ICoords getCoords();

	/**
	 * @param coords the coords to set
	 */
	void setCoords(ICoords coords);

	/**
	 * @return the room
	 */
	IRoom getRoom();

	/**
	 * @param room the room to set
	 */
	void setRoom(IRoom room);

	IHallway getHallway();

	void setHallway(IHallway hallway);

	Direction getDirection();

	void setDirection(Direction direction);

}