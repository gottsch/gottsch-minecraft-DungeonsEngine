/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * @author Mark Gottschling on Aug 28, 2016
 *
 */
public interface IExteriorDungeonsBlockProvider extends IDungeonsBlockProvider {
	
	/**
	 * 
	 */
	@Override
	default public boolean isCeilingElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			if (coords.getY() == room.getMaxY() - 2) return true;
			return false;
		}
		if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			if (coords.getY() == room.getMaxY() - 1) return true;
			return false;
		}
		return IDungeonsBlockProvider.super.isCeilingElement(coords, room, layout);
	}
	
	/**
	 * 
	 */
	@Override
	default public boolean isCrownElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (!room.has(Elements.ElementsEnum.CROWN) || room.getHeight() <= 4) return false;
		
		int yOffset = -1;
		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			yOffset = -3;
		}
		else if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			yOffset = -2;
		}
		
		int xzOffset = 1;
		if (room.has(Elements.ElementsEnum.PLINTH, Elements.ElementsEnum.COLUMN)) {
			xzOffset = 2;
		}
		
		if (coords.getY() == room.getMaxY() + yOffset &&
			(
				((coords.getX() == room.getMinX()+xzOffset || coords.getX() == room.getMaxX()-xzOffset)  &&
						coords.getZ() >= room.getMinZ()+xzOffset && coords.getZ() <= room.getMaxZ()-xzOffset) ||
				((coords.getZ() == room.getMinZ()+xzOffset || coords.getZ() == room.getMaxZ()-xzOffset) &&
						coords.getX() >= room.getMinX()+xzOffset && coords.getX() <= room.getMaxX()-xzOffset)
			)
		) return true;

		return false;
	}
	
	/**
	 * 
	 */
	@Override
	default public boolean isTrimElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();

		if (room.has(Elements.ElementsEnum.PLINTH, Elements.ElementsEnum.COLUMN)) { // TODO check for hasInsetWall
			if (layout.getFrames().get(Elements.ElementsEnum.TRIM.name()) != null &&
					/*
					 * room has to be at least 7x7 and 4 high to give enough space to walk around
					 */
					room.getWidth() >=9 &&
					room.getDepth() >=9 &&
					room.getHeight() > 4 &&
					y == room.getMinY()+1 &&				
					(
						((x == room.getMinX()+2 || x == room.getMaxX()-2)  && z > room.getMinZ() && z < room.getMaxZ()) ||
						((z == room.getMinZ()+2 || z == room.getMaxZ()-2) && x > room.getMinX() && x < room.getMaxX()))
					) return true;
		}
		else {
			IDungeonsBlockProvider.super.isTrimElement(coords, room, layout);
		}
		return false;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isPlinthElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (!room.has(Elements.ElementsEnum.PLINTH) || room.getWidth() < 7 || room.getDepth() < 7) return false; 
		if (coords.getY() == room.getMinY()) {
			if (coords.getX() == room.getMinX() || coords.getX() == room.getMaxX() ||
					coords.getZ() == room.getMinZ() || coords.getZ() == room.getMaxZ()) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isCorniceElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		if (!room.has(Elements.ElementsEnum.CORNICE)) return false;

		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			if (y != room.getMaxY() - 3) return false;
		}
		if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			if (y != room.getMaxY() - 2) return false;
		}
		if (!room.has(Elements.ElementsEnum.CRENELLATION) 
				&& !room.has(Elements.ElementsEnum.PARAPET)
				&& !room.has(Elements.ElementsEnum.MERLON)
				&& y != room.getMaxY() -1) return false;		
		if (x == room.getMinX() || x == room.getMaxX() || z == room.getMinZ() || z == room.getMaxZ()) return true;
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isCrenellationElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() >= room.getMaxY() - 2 &&
				(coords.getX() == room.getMinX() || coords.getX() == room.getMaxX() ||
				coords.getZ() == room.getMinZ() || coords.getZ() == room.getMaxZ())) return true;
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isParapetElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if ((room.has(Elements.ElementsEnum.CRENELLATION) && coords.getY() == room.getMaxY() - 1) ||
				!room.has(Elements.ElementsEnum.CRENELLATION)
				&& room.has(Elements.ElementsEnum.PARAPET) && coords.getY() == room.getMaxY()) {
			if (coords.getX() == room.getMinX() || coords.getX() == room.getMaxX() ||
					(coords.getZ() == room.getMinZ() || coords.getZ() == room.getMaxZ())) return true;
		}
		return false;
	}
	
	/**
	 * Merlons occur at each of the four corners, immediately adjacent to the corners and then every other block.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isMerlonElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		if ((room.has(Elements.ElementsEnum.CRENELLATION, Elements.ElementsEnum.MERLON)) &&  y == room.getMaxY()) {
			int xIndex = x - room.getCoords().getX();
			int zIndex = z - room.getCoords().getZ();
			// corners
			if	((x == room.getMinX() || x == room.getMaxX()) && (z == room.getMinZ() || z == room.getMaxZ())) return true;
			// adjacent to the corner
			if (room.getWidth() > 5) {
				if ((x == room.getMinX()+1 || x == room.getMaxX()-1) && (z == room.getMinZ() || z == room.getMaxZ())) return true;
				if ((z == room.getMinZ()+1 || z == room.getMaxZ()-1) && (x == room.getMinX() || x == room.getMaxX())) return true;

				// every other block in the middle
				if (((x == room.getMinX() || x == room.getMaxX()) && Math.abs(zIndex % 2) == 1 && zIndex > 0 && zIndex < room.getDepth() - 1) ||
					((z == room.getMinZ() || z == room.getMaxZ()) && Math.abs(xIndex % 2) == 1 && xIndex > 0 && xIndex < room.getWidth() - 1)) return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isInsetWallElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		/*
		 * NOTE remember to minus an additional 1 for the ceiling so crenellation becomes -3, merlon || parapet becomes -2
		 */
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		if (room.has(Elements.ElementsEnum.CRENELLATION) && y >= room.getMaxY() - 3) return false;
		if ((room.has(Elements.ElementsEnum.MERLON, Elements.ElementsEnum.PARAPET)) && y >= room.getMaxY() - 2) return false;
		
		if (
			((x == room.getMinX()+1 || x == room.getMaxX()-1) && z > room.getMinZ() && z < room.getMaxZ()) ||
			((z == room.getMinZ()+1 || z == room.getMaxZ()-1) && x > room.getMinX() && x < room.getMaxX())
		) return true;
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isWindowElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int xIndex = coords.getX() - room.getCoords().getX();
		int zIndex = coords.getZ() - room.getCoords().getZ();
		int yIndex = coords.getY() - room.getCoords().getY();
		
		/*
		 * very specific windows on either side of door for small entrances, 1 block above door
		 */
		if (room.getWidth() == 5) {
			if ((xIndex ==1 || xIndex == 3
					|| zIndex == 1 || zIndex ==3) 
					&& (yIndex > 2 && yIndex < 5)) {
//				Dungeons2.log.debug("Has Window!");
				return true;
			}
		}
		else if (room.getWidth() ==7) {
			if ((xIndex == 2 || xIndex == 4
					|| zIndex == 2 || zIndex == 4)
					&& (yIndex > 2 && yIndex < 5)) {
//				Dungeons2.log.debug("Has Window");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isColumnElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		if (!room.has(Elements.ElementsEnum.COLUMN) || room.getWidth() < 7) return false; // TODO rework
		if (room.has(Elements.ElementsEnum.CRENELLATION) && y > room.getMaxY() - 3) return false;
		if (!room.has(Elements.ElementsEnum.CRENELLATION) && (room.has(Elements.ElementsEnum.MERLON, Elements.ElementsEnum.PARAPET) && y > room.getMaxY()-2)) return false;
		
		// get the x,z indexes
		int xIndex = x - room.getCoords().getX();
		int zIndex = z - room.getCoords().getZ();

		if (((x == room.getMinX() || x == room.getMaxX()) && Math.abs(zIndex % 2) == 0 && zIndex > 0 && zIndex < room.getDepth() - 1) ||
			((z == room.getMinZ() || z == room.getMaxZ()) && Math.abs(xIndex % 2) == 0 && xIndex > 0 && xIndex < room.getWidth() - 1)) return true;
		return false;
	}
	
	/**
	 * It is assumed that the element at x,y,z has already been determined to be a column. This method simply checks
	 * the vertical position to determine if this element is the base of the column. No additional checks are made to ensure
	 * that is position is indeed a column.
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	default public boolean isBaseElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getMinY()) return true;
		return false;
	}
	
	/**
	 * It is assumed that the element at x,y,z has already been determined to be a column. This method simply checks
	 * the vertical position to determine if this element is the capital of the column. No additional checks are made to ensure
	 * that is position is indeed a column.
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	@Override
	default public boolean isCapitalElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		
		// TODO check if hasCrenellation
		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			if (y == room.getMaxY() - 3) return true;
		}
		else if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			if (y == room.getMaxY() - 2) return true;
		}
		else {
			if (y == room.getMaxY() - 1) return true;
		}
		return false;
	}
	
	/**
	 * move to IExteriorDungeonsBlockProvider
	 * @param room
	 * @param layout
	 * @return
	 */
	default public int getCorniceHeight(IDecoratedRoom room, Layout layout) {

		if (!room.has(Elements.ElementsEnum.CORNICE)) return 0;

		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			return room.getMaxY() - 3;
		}
		if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			return room.getMaxY() - 2;
		}
		if (!room.has(Elements.ElementsEnum.CRENELLATION)
				&& !room.has(Elements.ElementsEnum.PARAPET)
				&& !room.has(Elements.ElementsEnum.MERLON)) return room.getMaxY()-1;

		return 0;
	}
	
	/**
	 * move to IExteriorDungeonsBlockProvider
	 * @param room
	 * @param layout
	 * @return
	 */
	default public int getCeilingHeight(IDecoratedRoom room, Layout layout) {
		int h = room.getMaxY();
		if (room.has(Elements.ElementsEnum.CRENELLATION)) {
			h = room.getMaxY() - 2;
		}
		if (room.has(Elements.ElementsEnum.PARAPET, Elements.ElementsEnum.MERLON)) {
			h = room.getMaxY() - 1;
		}
		return h;
	}
}
