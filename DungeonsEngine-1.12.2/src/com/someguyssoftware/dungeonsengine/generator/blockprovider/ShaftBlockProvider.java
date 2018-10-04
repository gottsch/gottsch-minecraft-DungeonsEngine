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
 * @author Mark Gottschling on Aug 27, 2016
 *
 */
public class ShaftBlockProvider extends AbstractBlockProvider {
	
	/**
	 * 
	 * @param sheet
	 */
	public ShaftBlockProvider(StyleSheet sheet) {
		super(sheet);
	}

	/**
	 * 
	 */
	@Override
	public IArchitecturalElement getArchitecturalElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (isLadderElement(coords, room, layout)) return Elements.LADDER;	
		return Elements.WALL;
	}

	/**
	 * Determines whether the position at (x, y, z) is a ladder
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	public boolean isLadderElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		ICoords center = room.getCenter();
		if (coords.getX() == center.getX() && coords.getZ() == (center.getZ()) ) return true;
		return false;
	}

}
