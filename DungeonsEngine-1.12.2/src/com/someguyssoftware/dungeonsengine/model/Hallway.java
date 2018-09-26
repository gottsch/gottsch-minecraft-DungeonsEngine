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
public class Hallway extends Room implements IHallway {
	Alignment alignment;

	/**
	 * @since 2.0
	 */
	IHallway hallway;

	/**
	 * 
	 */
	public Hallway() {
		super();
		setDoors(new ArrayList<>(2));
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IHallway#getHallway()
	 */
	@Override
	public IHallway getHallway() {
		return hallway;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IHallway#setHallway(com.someguyssoftware.dungeonsengine.model.IHallway)
	 */
	@Override
	public void setHallway(IHallway hallway) {
		this.hallway = hallway;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IHallway#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IHallway#setAlignment(com.someguyssoftware.gottschcore.enums.Alignment)
	 */
	@Override
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
}
