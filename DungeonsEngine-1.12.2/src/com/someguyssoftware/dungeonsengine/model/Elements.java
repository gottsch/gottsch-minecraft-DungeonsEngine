/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;

/**
 * @author Mark Gottschling on Aug 19, 2018
 *
 */
public class Elements {

	public static IArchitecturalElement AIR;
	public static IArchitecturalElement FLOOR;
	public static IArchitecturalElement WALL;
	public static IArchitecturalElement CEILING;
	
	/**
	 * 
	 */
	public Elements() {	
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static IArchitecturalElement getElement(String name) {
		return ElementsEnum.valueOf(name.toUpperCase()).getElement();
	}
	
	/**
	 * Wrapper for all the ArchitecturalElements to enable fast lookups.
	 * @author Mark Gottschling on Aug 19, 2018
	 *
	 */
	public enum ElementsEnum {
		AIR(0, Elements.AIR),
		FLOOR(1, Elements.FLOOR),
		WALL(2, Elements.WALL),
		CEILING(3, Elements.CEILING);
		
		private int index;
		private IArchitecturalElement element;
		
		/**
		 * 
		 * @param index
		 * @param element
		 */
		ElementsEnum(int index, IArchitecturalElement element) {
			this.index = index;
			this.element = element;
		}
		
		/**
		 * 
		 * @return
		 */
		public IArchitecturalElement getElement() {
			return this.element;
		}
	}
}
