/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.graph.mst.Edge;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author Mark Gottschling on Jul 18, 2016
 * @version 2.0
 * @since 1.0.0
 *
 */
public class Level {
	private int id;
	private String name;
	private ICoords spawnPoint;
	private ICoords startPoint;
	private Room startRoom;
	private Room endRoom;
	private List<Room> rooms;
	
	@Deprecated private int depth;
	@Deprecated private int width;
	
	private AxisAlignedBB field;
	// TODO maybe create a VisualLevel extends Level that contains the transient data and is only set if a flag is set
	// transient - needed only for visualizing
//	private List<Edge> edges;
	// transient - needed only for visualizing
//	private List<Edge> paths;

	/**
	 * @since 2.0
	 */
	private List<Hallway> hallways;
	private List<Shaft> shafts;
	
	private int minX, maxX;
	private int minY, maxY;
	private int minZ, maxZ;

	private LevelConfig config;

	/**
	 * 
	 */
	public Level() {
		super();
	}

	/**
	 * @return the startRoom
	 */
	public Room getStartRoom() {
		return startRoom;
	}


	/**
	 * @param startRoom the startRoom to set
	 */
	public void setStartRoom(Room startRoom) {
		this.startRoom = startRoom;
	}


	/**
	 * @return the endRoom
	 */
	public Room getEndRoom() {
		return endRoom;
	}


	/**
	 * @param endRoom the endRoom to set
	 */
	public void setEndRoom(Room endRoom) {
		this.endRoom = endRoom;
	}


	/**
	 * @return the rooms
	 */
	public List<Room> getRooms() {
		if (this.rooms == null) this.rooms = new ArrayList<>();
		return rooms;
	}


	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}


	/**
	 * @param startRoom
	 * @param endRoom
	 * @param rooms
	 */
	public Level(Room startRoom, Room endRoom, List<Room> rooms) {
		super();
		this.startRoom = startRoom;
		this.endRoom = endRoom;
		this.rooms = rooms;
	}

	/**
	 * 
	 * @return
	 */
	public AxisAlignedBB getBoundingBox() {
		BlockPos bp1 = getStartPoint().toPos();
		BlockPos bp2 = getStartPoint().add(getWidth(), getStartPoint().getY()+1, getDepth()).toPos(); // TODO update to actual height
		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
		return bb;
	}
	
	/**
	 * Creates a bounding box by the XZ dimensions with a height (Y) of 1
	 * @return
	 */
	public AxisAlignedBB getXZBoundingBox() {
//		BlockPos bp1 = new BlockPos(getStartPoint().getX(), 0, getStartPoint().getZ());
//		BlockPos bp2 = getStartPoint().add(getWidth(), 1, getDepth()).toPos();
//		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
//		return bb;
		return this.getField();
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the NAME
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param NAME the NAME to set
	 */
	public void setName(String name) {
		this.name = name;
	}

//	/**
//	 * @return the edges
//	 */
//	public List<Edge> getEdges() {
//		return edges;
//	}
//
//	/**
//	 * @param edges the edges to set
//	 */
//	public void setEdges(List<Edge> edges) {
//		this.edges = edges;
//	}
//
//	/**
//	 * @return the paths
//	 */
//	public List<Edge> getPaths() {
//		return paths;
//	}
//
//	/**
//	 * @param paths the paths to set
//	 */
//	public void setPaths(List<Edge> paths) {
//		this.paths = paths;
//	}

	/**
	 * @return the minX
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * @param minX the minX to set
	 */
	public void setMinX(int minX) {
		this.minX = minX;
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the minY
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * @param minY the minY to set
	 */
	public void setMinY(int minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY the maxY to set
	 */
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the minZ
	 */
	public int getMinZ() {
		return minZ;
	}

	/**
	 * @param minZ the minZ to set
	 */
	public void setMinZ(int minZ) {
		this.minZ = minZ;
	}

	/**
	 * @return the maxZ
	 */
	public int getMaxZ() {
		return maxZ;
	}

	/**
	 * @param maxZ the maxZ to set
	 */
	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}

	/**
	 * @return the startPoint
	 */
	public ICoords getStartPoint() {
		return startPoint;
	}

	/**
	 * @param startPoint the startPoint to set
	 */
	public void setStartPoint(ICoords startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return the config
	 */
	public LevelConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(LevelConfig config) {
		this.config = config;
	}

	/**
	 * @return the shafts
	 */
	public List<Shaft> getShafts() {
		if (shafts == null) {
			this.shafts = new ArrayList<>();
		}
		return shafts;
	}

	/**
	 * @param shafts the shafts to set
	 */
	public void setShafts(List<Shaft> shafts) {
		this.shafts = shafts;
	}

	public List<Hallway> getHallways() {
		if (hallways == null) {
			this.hallways = new ArrayList<>();
		}
		return hallways;
	}

	public void setHallways(List<Hallway> hallways) {
		this.hallways = hallways;
	}

	/**
	 * @return the spawnPoint
	 */
	public ICoords getSpawnPoint() {
		return spawnPoint;
	}

	/**
	 * @param spawnPoint the spawnPoint to set
	 */
	public void setSpawnPoint(ICoords spawnPoint) {
		this.spawnPoint = spawnPoint;
	}

	/**
	 * @return the length
	 */
	@Deprecated
	public int getDepth() {
		return depth;
	}

	/**
	 * @param length the length to set
	 */
	public void setDepth(int length) {
		this.depth = length;
	}

	/**
	 * @return the width
	 */
	@Deprecated
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the field
	 */
	public AxisAlignedBB getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(AxisAlignedBB field) {
		this.field = field;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Level [id=" + id + ", name=" + name + ", spawnPoint=" + spawnPoint + ", startPoint=" + startPoint + ", startRoom=" + startRoom + ", endRoom=" + endRoom + ", rooms=" + rooms
				+ ", depth=" + depth + ", width=" + width + ", field=" + field + ", hallways=" + hallways + ", shafts=" + shafts + ", minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY="
				+ maxY + ", minZ=" + minZ + ", maxZ=" + maxZ + ", config=" + config + "]";
	}
	
}
