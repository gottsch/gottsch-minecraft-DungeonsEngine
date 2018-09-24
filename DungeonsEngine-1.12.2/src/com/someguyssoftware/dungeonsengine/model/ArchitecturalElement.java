/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.dungeonsengine.enums.Face;

/**
 * @author Mark Gottschling on Aug 19, 2018
 *
 */
public class ArchitecturalElement implements IArchitecturalElement {

	private String name;
	private boolean hasHorizontalSupport; // TODO should this be a property or just a method ?
	private boolean hasVerticalSupport;
	private double horizontalSupport;
	private double verticalSupport;
	private Face face;
	private IArchitecturalElement base;
	
	/**
	 * 
	 */
	public ArchitecturalElement() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#isHasHorizontalSupport()
	 */
	@Override
	public boolean isHasHorizontalSupport() {
		return hasHorizontalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setHasHorizontalSupport(boolean)
	 */
	@Override
	public void setHasHorizontalSupport(boolean hasHorizontalSupport) {
		this.hasHorizontalSupport = hasHorizontalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#isHasVerticalSupport()
	 */
	@Override
	public boolean isHasVerticalSupport() {
		return hasVerticalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setHasVerticalSupport(boolean)
	 */
	@Override
	public void setHasVerticalSupport(boolean hasVerticalSupport) {
		this.hasVerticalSupport = hasVerticalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#getHorizontalSupport()
	 */
	@Override
	public double getHorizontalSupport() {
		return horizontalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setHorizontalSupport(double)
	 */
	@Override
	public void setHorizontalSupport(double horizontalSupport) {
		this.horizontalSupport = horizontalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#getVerticalSupport()
	 */
	@Override
	public double getVerticalSupport() {
		return verticalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setVerticalSupport(double)
	 */
	@Override
	public void setVerticalSupport(double verticalSupport) {
		this.verticalSupport = verticalSupport;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#getFace()
	 */
	@Override
	public Face getFace() {
		return face;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setFace(com.someguyssoftware.dungeonsengine.enums.Face)
	 */
	@Override
	public void setFace(Face face) {
		this.face = face;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#getBase()
	 */
	@Override
	public IArchitecturalElement getBase() {
		return base;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IArchitecturalElement#setBase(com.someguyssoftware.dungeonsengine.model.IArchitecturalElement)
	 */
	@Override
	public void setBase(IArchitecturalElement base) {
		this.base = base;
	}

}
