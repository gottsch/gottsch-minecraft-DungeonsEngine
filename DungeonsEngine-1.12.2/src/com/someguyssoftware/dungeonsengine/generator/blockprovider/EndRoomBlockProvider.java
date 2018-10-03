/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * @author Mark Gottschling on Aug 27, 2016
 *
 */
public class EndRoomBlockProvider extends AbstractBlockProvider {

	/**
	 * 
	 * @param sheet
	 */
	public EndRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}
	

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
		// TODO check for out of bounds

		// check for ladder pillar - needs to come before floor
		if (isLadderPillarElement(coords, room, layout)) return Elements.LADDER_PILLAR;
		if (isLadderElement(coords, room, layout)) return Elements.LADDER;
		
		// check for floor
		if (isFloorElement(coords, room, layout))return Elements.FLOOR;
				
		// check for wall
		if (isWallElement(coords, room, layout)) {
			if (isFacadeSupport(coords, room, layout)) return Elements.FACADE_SUPPORT;
			return Elements.WALL;
		}
		
		// TODO check for pillar
		
		
		// check for crown molding
		if (isCrownElement(coords, room, layout)) return Elements.CROWN;
		// check for trim
		if (isTrimElement(coords, room, layout)) return Elements.TRIM;		

		// check for ceiling
		if(isCeilingElement(coords, room, layout)) return Elements.CEILING;
		
		return Elements.AIR;
	}

	// TODO move to AbstractAdvanced or AbstractSpecial RoomGenerator --> actually to AbstractStartRoomGenerator
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	public boolean isLadderPillarElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		ICoords center = room.getCenter();
		if (coords.getX() == center.getX() && coords.getZ() == center.getZ() && coords.getY() < room.getMaxY()) return true;
		return false;
	}
	
	/**
	 * Determines whether the position at (x, y, z) is a ladder
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	public boolean isLadderElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		ICoords center = room.getCenter();
		Direction direction = room.getDirection();
		
		// short-circuit if ceiling
		if (coords.getY() == room.getMaxY()) return false;
		
		// want the ladder on the opposite side the room is facing ie if room faces north, want the ladder on the south side of pillar (so that the ladder still faces north)
		switch(direction) {		
		case NORTH:
			if (coords.getX() == center.getX() && coords.getZ() == (center.getZ()+1) ) return true;
			break;
		case EAST:
			if (coords.getX() == (center.getX()-1) && coords.getZ() == center.getZ() ) return true;
			break;
		case SOUTH:
			if (coords.getX() == center.getX() && coords.getZ() == (center.getZ()-1) ) return true;
			break;
		case WEST:
			if (coords.getX() == (center.getX()+1) && coords.getZ() == center.getZ() ) return true;
			break;
		default:			
		}
		return false;
	}
}
