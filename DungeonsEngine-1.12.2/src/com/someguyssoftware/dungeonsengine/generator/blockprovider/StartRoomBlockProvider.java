/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import org.w3c.dom.Element;

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
public class StartRoomBlockProvider extends AbstractBlockProvider{

	/**
	 * 
	 * @param sheet
	 */
	public StartRoomBlockProvider(StyleSheet sheet) {
		super(sheet);
	}
	

	/**
	 * Generates a simple cube room without any decorations (crown, trim, etc)
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	public IArchitecturalElement getArchitecturalElement(ICoords coords, IDecoratedRoom room, Layout layout) {

		// check for floor
		if (isFloorElement(coords, room, layout))return Elements.FLOOR;
		// check for wall
		if (isWallElement(coords, room, layout)) {
			if (isFacadeSupport(coords, room, layout)) return Elements.FACADE_SUPPORT;
			return Elements.WALL;
		}
		
		// check for ladder pillar - needs to come before ceiling
		if (isLadderPillarElement(coords, room, layout)) return Elements.LADDER_PILLAR;
		if (isLadderElement(coords, room, layout)) return Elements.LADDER;
		
		
		/*
		 * pillars should only be near corners ie only 4 pillars near corners - not necessarily attached.
		 */
		if (room.has(Elements.ElementsEnum.PILLAR) && isPillarElement(coords, room, layout)) {
			if (isBaseElement(coords, room, layout)) return Elements.PILLAR_BASE;
			else if (isCapitalElement(coords, room, layout)) return Elements.PILLAR_CAPITAL;
			else return Elements.PILLAR;			
		}

		// check for crown molding
		if (room.has(Elements.ElementsEnum.CROWN) && isCrownElement(coords, room, layout)) return Elements.CROWN;
		// check for trim
		if (room.has(Elements.ElementsEnum.TRIM) && isTrimElement(coords, room, layout)) return Elements.TRIM;		

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
		if (coords.getX() == center.getX() && coords.getZ() == center.getZ() && coords.getY() > room.getMinY()) return true;
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

		// want the ladder on the opposite side the room is facing ie if room faces north, want the ladder on the south side of pillar (so that the ladder still faces north)
		switch(direction) {		
		case NORTH:
			if (coords.getX() == center.getX() && coords.getZ() == (center.getZ()+1) && coords.getY() > room.getMinY()) return true;
			break;
		case EAST:
			if (coords.getX() == (center.getX()-1) && coords.getZ() == center.getZ() && coords.getY() > room.getMinY()) return true;
			break;
		case SOUTH:
			if (coords.getX() == center.getX() && coords.getZ() == (center.getZ()-1) && coords.getY() > room.getMinY()) return true;
			break;
		case WEST:
			if (coords.getX() == (center.getX()+1) && coords.getZ() == center.getZ() && coords.getY() > room.getMinY()) return true;
			break;
		default:			
		}
		return false;
	}

	//  pillar
	@Override
	public boolean isPillarElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		if (!room.has(Elements.ElementsEnum.PILLAR) || Math.min(room.getWidth(), room.getDepth()) < 7 || y == room.getMaxY()) return false;
		
		if ((x == room.getMinX() + 2 || x == room.getMaxX() -2) && (z == room.getMinZ() + 2 || z == room.getMaxZ() - 2)) return true;
		return false;
	}
}
