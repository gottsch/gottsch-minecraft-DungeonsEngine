package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.gottschcore.enums.Alignment;

public interface IHallway extends IRoom {

	IHallway getHallway();

	void setHallway(IHallway hallway);

	/**
	 * @return the alignment
	 */
	Alignment getAlignment();

	/**
	 * @param alignment the alignment to set
	 */
	void setAlignment(Alignment alignment);

}