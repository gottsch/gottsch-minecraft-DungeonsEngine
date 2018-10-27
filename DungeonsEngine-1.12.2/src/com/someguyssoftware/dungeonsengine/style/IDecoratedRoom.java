package com.someguyssoftware.dungeonsengine.style;

import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;

import com.google.common.collect.Multimap;
import com.someguyssoftware.dungeonsengine.generator.Location;
import com.someguyssoftware.dungeonsengine.model.Elements.ElementsEnum;

public interface IDecoratedRoom {

	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean has(ElementsEnum element);

	boolean has(ElementsEnum element, ElementsEnum... extra);
	
	void include(ElementsEnum element);

	boolean include(ElementsEnum element, ElementsEnum[] extras);
	
	void exclude(ElementsEnum element);
	
	/**
	 * @return the room
	 */
	IRoom getRoom();

	/**
	 * @param room the room to set
	 */
	void setRoom(IRoom room);

	Layout getLayout();

	Location getLocation(ICoords coords);
	
	/**
	 * 
	 * @return
	 */
	default public int getMinX() {
		return this.getRoom().getCoords().getX();
	}

	/**
	 * 
	 * @return
	 */
	default public int getMaxX() {
		return this.getRoom().getCoords().getX() + this.getRoom().getWidth() - 1;
	}

	/*
	 * 
	 */
	default public int getMinY() {
		return this.getRoom().getCoords().getY();
	}
	
	/*
	 * 
	 */
	default public int getMaxY() {
		return this.getRoom().getCoords().getY() + this.getRoom().getHeight() - 1;
	}
	
	/**
	 * 
	 * @return
	 */
	default public int getMinZ() {
		return this.getRoom().getCoords().getZ();
	}
	
	/**
	 * 
	 * @return
	 */
	default public int getMaxZ() {
		return this.getRoom().getCoords().getZ() + this.getRoom().getDepth() - 1;
	}
	
	/**
	 * @return the depth
	 */
	default public int getDepth() {
		return this.getRoom().getDepth();
	}

	/**
	 * @return the width
	 */
	default public int getWidth() {
		return this.getRoom().getWidth();
	}
	
	/**
	 * @return the height
	 */
	default public int getHeight() {
		return this.getRoom().getHeight();
	}
	
	/**
	 * 
	 * @return
	 */
	default public ICoords getCenter() {
		return this.getRoom().getCenter();
	}
	
	/**
	 * 
	 * @return
	 */
	default public Direction getDirection() {
		return this.getRoom().getDirection();
	}

	/**
	 * 
	 * @return
	 */
	default public ICoords getCoords() {
		return this.getRoom().getCoords();
	}

	default public ICoords getXZCenter() {
		return this.getRoom().getXZCenter();
	}

	default public boolean isStart() {
		return this.getRoom().isStart();
	}
	
	default public boolean isEnd() {
		return this.getRoom().isEnd();
	}

	default public Object getType() {
		return this.getRoom().getType();
	}

	Multimap<IArchitecturalElement, ICoords> getFloorMap();

	default public AxisAlignedBB getBoundingBox() {
		return this.getRoom().getBoundingBox();
	}

	/**
	 * 
	 * @param layout
	 */
	void setLayout(Layout layout);

}