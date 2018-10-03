/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;


import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.gottschcore.enums.Direction;

/**
 * @author Mark Gottschling on Aug 4, 2016
 *
 */
public class Arrangement {
	private IArchitecturalElement element;
	private Direction direction;
	private Location location;
	
	/**
	 * 
	 */
	public Arrangement() {
		
	}
	
	/**
	 * 
	 * @param element
	 * @param location
	 * @param direction
	 */
	public Arrangement(IArchitecturalElement element, Location location, Direction direction) {
		setElement(element);
		setLocation(location);
		setDirection(direction);
	}

	/**
	 * @return the element
	 */
	public IArchitecturalElement getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(IArchitecturalElement element) {
		this.element = element;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return "Arrangement [element=" + element + ", direction=" + direction + ", location=" + location + "]";
	}
}
