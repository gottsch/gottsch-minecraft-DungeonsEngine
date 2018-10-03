/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * @author Mark Gottschling on Aug 30, 2016
 *
 */
public class BossRoomBlockProvider extends AbstractBlockProvider {
	/**
	 * 
	 * @param sheet
	 */
	public BossRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}
	

	/*
	 *  TODO is there anything special in a boss room?
	 *  NO pillars
	 *  YES CARPET/RUGS - should rug be a design element.... i guess so.
	 */
	@Override
	public boolean isPillarElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		return false;
	}
}
