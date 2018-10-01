package com.someguyssoftware.dungeonsengine.style;

import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Elements.ElementsEnum;

public interface IDecoratedRoom {

	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean has(ElementsEnum element);

	/**
	 * @return the room
	 */
	IRoom getRoom();

	/**
	 * @param room the room to set
	 */
	void setRoom(IRoom room);

}