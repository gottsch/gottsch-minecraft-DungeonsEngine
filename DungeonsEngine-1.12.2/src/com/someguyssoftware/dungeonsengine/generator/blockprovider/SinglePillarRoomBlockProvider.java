/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * Creates a room where the pillars are generated in a single ring around the inner edge of the room.
 * @author Mark Gottschling on Sep 22, 2016
 *
 */
public class SinglePillarRoomBlockProvider extends AbstractBlockProvider {

	/**
	 * 
	 * @param sheet
	 */
	public SinglePillarRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}

	//  pillar
	@Override
	public boolean isPillarElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		if (!room.has(Elements.ElementsEnum.PILLAR) || Math.min(room.getWidth(), room.getDepth()) < 7 || y == room.getMaxY()) return false;

		// get the x,z indexes
		int xIndex = x - room.getCoords().getX();
		int zIndex = z - room.getCoords().getZ();
		int offset = 1;
		int m = 0;
		
		// if the room also has pilasters, then the offset is increased so there is still space between pillar and pilaster
		if (room.has(Elements.ElementsEnum.PILASTER)) {
			offset = 2;
			m = 1;
		}
		
		// check if at an inner ring either 1 space away from wall or 2 spaces from wall if there are pilasters
		if (xIndex == room.getXZCenter().getX() && zIndex == room.getXZCenter().getZ()) return true;
		return false;
	}
}
