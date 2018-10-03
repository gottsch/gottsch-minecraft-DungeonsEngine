/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * @author Mark Gottschling on Aug 28, 2016
 *
 */
public class HallwayBlockProvider extends AbstractBlockProvider {
	
	/**
	 * 
	 * @param sheet
	 */
	public HallwayBlockProvider(StyleSheet sheet) {
		super(sheet);
	}

	/**
	 * 
	 */
	public IArchitecturalElement getDesignElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		
		// check for floor
		if (isFloorElement(coords, room, layout))return Elements.FLOOR;
		
		// check for wall
		if (isWallElement(coords, room, layout)) return Elements.WALL;

		// check for ceiling
		if(isCeilingElement(coords, room, layout)) return Elements.CEILING;
		
		return Elements.AIR;
	}
}
