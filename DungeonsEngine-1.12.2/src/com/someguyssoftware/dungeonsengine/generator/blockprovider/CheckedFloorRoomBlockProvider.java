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
 * @author Mark Gottschling on Feb 14, 2017
 *
 */
public class CheckedFloorRoomBlockProvider extends AbstractBlockProvider {
	
	/**
	 * 
	 * @param sheet
	 */
	public CheckedFloorRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}

	@Override
	public IArchitecturalElement getArchitecturalElement(ICoords coords, IDecoratedRoom room, Layout layout) {		
		if (isFloorAltElement(coords, room, layout)) {
			return Elements.FLOOR_ALT;
		}		
		return super.getArchitecturalElement(coords, room, layout);
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	public boolean isFloorAltElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int z = coords.getZ();
		
		// get the indexes
		int xIndex = x - room.getCoords().getX();
		int zIndex = z - room.getCoords().getZ();
		
		int xmod = Math.abs(xIndex % 2);
		int zmod = Math.abs(zIndex % 2);
		
		if (coords.getY() == room.getMinY()) {
			if ((xmod == 0 && zmod == 0) || (xmod == 1 && zmod == 1)) return true;
		}
		return false;
	}
	
}
