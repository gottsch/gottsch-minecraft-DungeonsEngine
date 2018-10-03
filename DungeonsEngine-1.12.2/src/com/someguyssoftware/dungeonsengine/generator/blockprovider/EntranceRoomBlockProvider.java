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
public class EntranceRoomBlockProvider extends AbstractBlockProvider implements IExteriorDungeonsBlockProvider {

	/**
	 * 
	 * @param sheet
	 */
	public EntranceRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}

	private static final int INSET_WALL_EXTERIOR_MIN_WIDTH = 7;

	/**
	 * Generates a simple cube room without any decorations (crown, trim, etc)
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	public IArchitecturalElement getArchitecturalElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		// TODO make ceiling height a property of Room, then wouldn't have to recalculate this for every block
		int ceilingHeight = getCeilingHeight(room, layout);
		int corniceHeight = getCorniceHeight(room, layout);
		
		// check for ladder pillar - needs to come before floor
//		if (isLadderPillarElement(x, y, z, room, layout)) return Elements.LADDER_PILLAR;
		if (isLadderElement(coords, room, layout)) return Elements.LADDER;
		
		if ((room.has(Elements.ElementsEnum.PLINTH, Elements.ElementsEnum.COLUMN, Elements.ElementsEnum.CORNICE)) 
				&& room.getWidth() >= INSET_WALL_EXTERIOR_MIN_WIDTH) {	
			if (isInsetWallElement(coords, room, layout)) {
//				return Elements.WALL;
				if (isFacadeSupport(coords, room, layout)) return Elements.FACADE_SUPPORT;
				return Elements.WALL;
			}
			
			IArchitecturalElement element = null;
			// build plinths and cornices before because they will get replaced/overwritten by columns
			if (isPlinthElement(coords, room, layout)) element = Elements.PLINTH;
			else if (isCorniceElement(coords, room, layout)) element = Elements.CORNICE;
			
			if (isColumnElement(coords, room, layout)) {
				// determine if base, shaft or capital
				if (isBaseElement(coords, room, layout)) element = Elements.BASE;
				else if (isCapitalElement(coords, room, layout)) element = Elements.CAPITAL;
				else element = Elements.COLUMN;
//				Dungeons2.log.debug("Is Column Element @ " + coords.toShortString());
			}
			if (element != null) return element;
		}
		else { 
			// TODO should return if any part of the window, then check if base, window, or capital 
			// check for window
			if (isWindowElement(coords, room, layout)) return Elements.WINDOW;
						
			// check for wall
			if (isFacadeSupport(coords, room, layout)) return Elements.FACADE_SUPPORT;
			if (isWallElement(coords, room, layout)) return Elements.WALL;
		}
		
		// check for floor
		if (isFloorElement(coords, room, layout))return Elements.FLOOR;		
				
		// check for trim
		if (room.has(Elements.ElementsEnum.TRIM) && isTrimElement(coords, room, layout)) return Elements.TRIM;		

		// check for crown molding
		if (room.has(Elements.ElementsEnum.CROWN) && isCrownElement(coords, room, layout)) return Elements.CROWN;

		// check for ceiling
		if(isCeilingElement(coords, room, layout)) return Elements.CEILING;
		
		// check for crenellation, merlon and/or parapet
		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			if (isCrenellationElement(coords, room, layout)) {
				if (isParapetElement(coords, room, layout)) return Elements.PARAPET;
				if (isMerlonElement(coords, room, layout)) return Elements.MERLON;
			}
		}
		else if (room.has(Elements.ElementsEnum.MERLON) && isMerlonElement(coords, room, layout)) return Elements.MERLON;
		else if (room.has(Elements.ElementsEnum.PARAPET) && isParapetElement(coords, room, layout)) return Elements.PARAPET;
		
		return Elements.AIR;
	}
	
	/**
	 * Checks for support block for crown and cornice.
	 * It is assumed that the element at x,y,z has already been determined to be a wall. This method simply checks
	 * the vertical position to determine if this element is the 1 less than the ceiling. No additional checks are made to ensure
	 * that is position is indeed a wall.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	public boolean isFacadeSupport(ICoords coords, IDecoratedRoom room, Layout layout) {
		int h = getCeilingHeight(room, layout);
		// check if y is 1 less than top (ceiling)
		if (coords.getY() == h ||
				coords.getY() == h - 1 ||
				coords.getY() == getCorniceHeight(room, layout)) return true;
		return false;
	}
	
}
