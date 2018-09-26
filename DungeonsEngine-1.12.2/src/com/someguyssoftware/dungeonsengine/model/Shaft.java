/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

/**
 * 
 * @author Mark Gottschling on Aug 15, 2016
 *
 */
public class Shaft extends Room {
	IRoom parent;

	public Shaft() {
		super();
	}

	/**
	 * @return the parent
	 */
	public IRoom getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(IRoom parent) {
		this.parent = parent;
	}
}
