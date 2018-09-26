/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;

/**
 * @author Mark Gottschling on Aug 30, 2016
 * @version 2.0
 * @since 1.0.0
 */
public class Door implements IDoor {	
	/**
	 * @since 2.0
	 */
	private int id;
	private ICoords coords;
	private IRoom room;
	/**
	 * @since 2.0
	 */
	private IHallway hallway;
	/**
	 * @since 2.0
	 */
	private Direction direction;
	
	/**
	 * 
	 */
	public Door() {
		setId(RandomHelper.randomInt(5001, 9999));
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 */
	public Door(ICoords coords, Room room) {
		setId(RandomHelper.randomInt(5001, 9999));
		this.coords = coords;
		this.room = room;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param hallway
	 * @param direction
	 * @since 2.0
	 */
	public Door(ICoords coords, IRoom room, IHallway hallway, Direction direction) {
		setId(RandomHelper.randomInt(5001, 9999));
		this.coords = coords;
		this.room = room;
		this.hallway = hallway;
		this.direction = direction;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#getCoords()
	 */
	@Override
	public ICoords getCoords() {
		return coords;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#setCoords(com.someguyssoftware.gottschcore.positional.ICoords)
	 */
	@Override
	public void setCoords(ICoords coords) {
		this.coords = coords;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#getRoom()
	 */
	@Override
	public IRoom getRoom() {
		return room;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#setRoom(com.someguyssoftware.dungeonsengine.model.IRoom)
	 */
	@Override
	public void setRoom(IRoom room) {
		this.room = room;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#getHallway()
	 */
	@Override
	public IHallway getHallway() {
		return hallway;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#setHallway(com.someguyssoftware.dungeonsengine.model.IHallway)
	 */
	@Override
	public void setHallway(IHallway hallway) {
		this.hallway = hallway;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#getDirection()
	 */
	@Override
	public Direction getDirection() {
		return direction;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDoor#setDirection(com.someguyssoftware.gottschcore.enums.Direction)
	 */
	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
