/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

/**
 * 
 * @author Mark Gottschling on Aug 15, 2016
 *
 */
public class Shaft extends Room implements IShaft {
	IRoom source;
	IRoom dest;

	public Shaft() {
		super();
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 */
	public Shaft(IRoom source, IRoom dest) {
		this.source = source;
		this.dest = dest;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IShaft#getSource()
	 */
	@Override
	public IRoom getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IShaft#setSource(com.someguyssoftware.dungeonsengine.model.IRoom)
	 */
	@Override
	public void setSource(IRoom source) {
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IShaft#getDest()
	 */
	@Override
	public IRoom getDest() {
		return dest;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IShaft#setDest(com.someguyssoftware.dungeonsengine.model.IRoom)
	 */
	@Override
	public void setDest(IRoom dest) {
		this.dest = dest;
	}
}
