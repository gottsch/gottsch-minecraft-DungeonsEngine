/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import java.util.ArrayList;

import com.someguyssoftware.gottschcore.enums.Alignment;

/**
 * @author Mark Gottschling on Aug 6, 2016
 * @version 2.0
 * @since 1.0.0
 */
public class Hallway extends Room {
	Alignment alignment;

	/**
	 * @since 2.0
	 */
	Hallway hallway;

	/**
	 * 
	 */
	public Hallway() {
		super();
		setDoors(new ArrayList<>(2));
	}
	
	public Hallway getHallway() {
		return hallway;
	}

	public void setHallway(Hallway hallway) {
		this.hallway = hallway;
	}
	
	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
}
