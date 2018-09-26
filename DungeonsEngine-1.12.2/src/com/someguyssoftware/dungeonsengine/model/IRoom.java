package com.someguyssoftware.dungeonsengine.model;

import java.util.Comparator;
import java.util.List;

import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.positional.Intersect;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public interface IRoom {
	public static final int MIN_DEPTH = 5;
	public static final int MIN_WIDTH = 5;
	public static final int MIN_HEIGHT = 4;
	
	/**
	 * 
	 * @return
	 */
	IRoom copy();
	
	/**
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 
	 * @param id
	 */
	void setId(int id);
	
	/**
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * @return the coords
	 */
	ICoords getCoords();

	/**
	 * @param coords the coords to set
	 */
	IRoom setCoords(ICoords coords);
	

	/**
	 * @return the depth
	 */
	int getDepth();

	/**
	 * @param depth the depth to set
	 */
	IRoom setDepth(int depth);

	/**
	 * @return the width
	 */
	int getWidth();

	/**
	 * @param width the width to set
	 */
	IRoom setWidth(int width);

	/**
	 * @return the height
	 */
	int getHeight();

	/**
	 * @param height the height to set
	 */
	IRoom setHeight(int height);

	/**
	 * @return the direction
	 */
	Direction getDirection();

	/**
	 * @param direction the direction to set
	 */
	IRoom setDirection(Direction direction);

	/**
	 * 
	 * @return
	 */
	List<IDoor> getDoors();

	/**
	 * 
	 * @param doors
	 */
	void setDoors(List<IDoor> doors);
	
	/**
	 * 
	 * @return
	 */
	int getDegrees();

	/**
	 * 
	 * @param degrees
	 * @return
	 */
	Room setDegrees(int degrees);
	
	/**
	 * 
	 * @return
	 */
	default public int getMinX() {
		return this.getCoords().getX();
	}

	/**
	 * 
	 * @return
	 */
	default public int getMaxX() {
		return this.getCoords().getX() + this.getWidth() - 1;
	}

	/*
	 * 
	 */
	default public int getMinY() {
		return this.getCoords().getY();
	}
	
	/*
	 * 
	 */
	default public int getMaxY() {
		return this.getCoords().getY() + this.getHeight() - 1;
	}
	
	/**
	 * 
	 * @return
	 */
	default public int getMinZ() {
		return this.getCoords().getZ();
	}
	
	/**
	 * 
	 * @return
	 */
	default public int getMaxZ() {
		return this.getCoords().getZ() + this.getDepth() - 1;
	}
	
	/**
	 * 
	 * @return
	 */
	default public AxisAlignedBB getBoundingBox() {
		BlockPos bp1 = getCoords().toPos();
		BlockPos bp2 = getCoords().add(getWidth(), getHeight(), getDepth()).toPos();
		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
		return bb;
	}
	
	/**
	 * Creates a bounding box by the XZ dimensions with a height (Y) of 1
	 * @return
	 */
	default public AxisAlignedBB getXZBoundingBox() {
		BlockPos bp1 = new BlockPos(getCoords().getX(), 0, getCoords().getZ());
		BlockPos bp2 = getCoords().add(getWidth(), 1, getDepth()).toPos();
		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
		return bb;
	}
	
	/**
	 * 
	 * @return
	 */
	default public ICoords getCenter() {
		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2) ;
		int y = this.getCoords().getY()  + ((this.getHeight()-1) / 2);
		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
		ICoords coords = new Coords(x, y, z);
		return coords;
	}
	
	/**
	 * 
	 * @return
	 */
	default public ICoords getBottomCenter() {
		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
		int y = this.getCoords().getY();
		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
		ICoords coords = new Coords(x, y, z);
		return coords;	
	}

	/**
	 * 
	 * @return
	 */
	default public ICoords getTopCenter() {
		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
		int y = this.getCoords().getY() + this.getHeight();
		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
		ICoords coords = new Coords(x, y, z);
		return coords;	
	}
	
	/**
	 * 
	 * @return
	 */
	default public ICoords getXZCenter() {
		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
		int y = this.getCoords().getY();
		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
		ICoords coords = new Coords(x, y, z);
		return coords;
	}
	
	/**
	 * 
	 * @param room
	 * @return
	 */
	default public Intersect getIntersect(IRoom room) {
		return Intersect.getIntersect(this.getBoundingBox(), room.getBoundingBox());
	}
	
	/**
	 * Returns a new IRoom with the force applied at the angle on the XZ plane.
	 * @param angle
	 * @param force
	 * @return
	 */
	default public IRoom addXZForce(double angle, double force) {
		double xForce = Math.sin(angle) * force;
        double zForce = Math.cos(angle) * force;

        IRoom room = copy();
        room.setCoords(room.getCoords().add((int)xForce, 0, (int)zForce));
        return room;
	}
	
	/**
	 * Comparator to sort by Id
	 */
	public static Comparator<IRoom> idComparator = new Comparator<IRoom>() {
		@Override
		public int compare(IRoom p1, IRoom p2) {
			if (p1.getId() > p2.getId()) {
				// greater than
				return 1;
			}
			else {
				// less than
				return -1;
			}
		}
	};
	
	default public String printDimensions() {
		return String.format("Dimensions -> [w: %d, h: %d, d: %d]", getWidth(), getHeight(), getDepth());
	}
	
	default public String printCoords() {
		return String.format("Coords -> [x: %d, y: %d, z: %d]", getCoords().getX(), getCoords().getY(), getCoords().getZ());
	}
	
	default public String printCenter() {
		return String.format("Center -> [x: %d, y: %d, z: %d]", getCenter().getX(), getCenter().getY(), getCenter().getZ());		
	}

	// TODO get rid of type
	Type getType();

	Room setType(Type type);

	boolean isReject();

	void setReject(boolean reject);

	boolean isAnchor();

	Room setAnchor(boolean anchor);

	boolean isStart();

	Room setStart(boolean start);

	boolean isEnd();

	Room setEnd(boolean end);

	boolean isObstacle();

	void setObstacle(boolean obstacle);
}