/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.enums.Rotate;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Aug 28, 2016
 *
 */
public abstract class AbstractExteriorRoomGenerator extends AbstractRoomGenerator {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	/**
	 * 
	 * @param world
	 * @param room
	 */
	protected void buildDoorway(World world, IDecoratedRoom room) {
		ICoords doorCoords = null;
		Direction moveDirection = null;
		boolean isInsetWall = 
				room.has(
						Elements.ElementsEnum.PLINTH,
						Elements.ElementsEnum.COLUMN,
						Elements.ElementsEnum.CORNICE)
				&& room.getWidth() >= 7 ? true : false;
				
		logger.debug("Has Inset Wall:" + isInsetWall);
		
		int offset = 0;
		switch(room.getDirection()) {
		case NORTH:
			offset = isInsetWall ? 1 : 0;
			doorCoords = new Coords(room.getCenter().getX(), room.getCoords().getY(), room.getMinZ() + offset);
			moveDirection = Direction.SOUTH;
			break;
		case EAST:
			offset = isInsetWall ? -1 : 0;
			doorCoords = new Coords(room.getMaxX() + offset, room.getCoords().getY(), room.getCenter().getZ());
			moveDirection = Direction.WEST;
			break;
		default: // default is SOUTH
		case SOUTH:
			offset = isInsetWall ? -1 : 0;
			doorCoords = new Coords(room.getCenter().getX(), room.getCoords().getY(), room.getMaxZ() + offset);
			moveDirection = Direction.NORTH;
			break;
		case WEST:
			offset = isInsetWall ? 1 : 0;
			doorCoords = new Coords(room.getMinX() + offset, room.getCoords().getY(), room.getCenter().getZ());
			moveDirection = Direction.EAST;
			break;
		}		
		// carve the doorway in both directions
		super.buildDoorway(world, doorCoords, moveDirection);
		super.buildDoorway(world, doorCoords, moveDirection.rotate(Rotate.ROTATE_180));		
	}
}
