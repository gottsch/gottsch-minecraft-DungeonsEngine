/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.dungeonsengine.graph.Wayline;
import com.someguyssoftware.dungeonsengine.graph.mst.Edge;

/**
 * @author Mark Gottschling on Sep 23, 2018
 *
 */
public class LevelArtifacts {
	/*
	 * special rooms which are designed as <em>fixed position</em>. ex. ladder rooms, treasure rooms, boss rooms.
	 * these rooms' positions will typically be pre-determined in a location that meets all criteria.
	 * these rooms <em>will</em> be included in the resultant level.
	 */
	List<Room> anchors = new ArrayList<>();

	/*
	 * rooms that are randomly generated
	 */
	List<Room> spawned = null;

	/*
	 * resultant list of buffered/spaced rooms on a single level.
	 */
	List<Room> rooms = null;

	/*
	 * resultant list of edges from triangulation of rooms.
	 */
	List<Edge> edges = null;

	/*
	 * resultant list of edges from performing minimum spanning tree on edges
	 */
	List<Edge> paths = null;

	/*
	 * resultant list of horizontal and vertical lines representing hallways that connect all the rooms together
	 * by "squaring off" the paths
	 */
	List<Wayline> waylines = null;
	
	/*
	 * resultant list of hallways derived from waylines
	 */
	List<Hallway> hallways = null;
	
	int roomLossToDistanceBuffering = 0;
	
	int roomLossToValidation = 0;
	
	/**
	 * 
	 */
	public LevelArtifacts() {

	}

	public void incrementLossToDistanceBuffering(int i) {
		this.roomLossToDistanceBuffering += i;		
	}
	
	public void incrementLossToValidation(int i) {
		this.roomLossToValidation += i;
	}
	
	/**
	 * @return the anchors
	 */
	public List<Room> getAnchors() {
		if (this.anchors == null) this.anchors = new ArrayList<>();
		return anchors;
	}

	/**
	 * @param anchors the anchors to set
	 */
	public void setAnchors(List<Room> anchors) {
		this.anchors = anchors;
	}

	/**
	 * @return the spawned
	 */
	public List<Room> getSpawned() {
		if (this.spawned == null) this.spawned = new ArrayList<>();
		return spawned;
	}

	/**
	 * @param spawned the spawned to set
	 */
	public void setSpawned(List<Room> spawned) {
		this.spawned = spawned;
	}

	/**
	 * @return the rooms
	 */
	public List<Room> getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	/**
	 * @return the edges
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	/**
	 * @return the paths
	 */
	public List<Edge> getPaths() {
		return paths;
	}

	/**
	 * @param paths the paths to set
	 */
	public void setPaths(List<Edge> paths) {
		this.paths = paths;
	}

	/**
	 * @return the waylines
	 */
	public List<Wayline> getWaylines() {
		return waylines;
	}

	/**
	 * @param waylines the waylines to set
	 */
	public void setWaylines(List<Wayline> waylines) {
		this.waylines = waylines;
	}

	/**
	 * @return the hallways
	 */
	public List<Hallway> getHallways() {
		return hallways;
	}

	/**
	 * @param hallways the hallways to set
	 */
	public void setHallways(List<Hallway> hallways) {
		this.hallways = hallways;
	}

	/**
	 * @return the roomLossDueToDistanceBuffering
	 */
	public int getRoomLossToDistanceBuffering() {
		return roomLossToDistanceBuffering;
	}

	/**
	 * @param roomLossDueToDistanceBuffering the roomLossDueToDistanceBuffering to set
	 */
	public void setRoomLossToDistanceBuffering(int roomLossDueToDistanceBuffering) {
		this.roomLossToDistanceBuffering = roomLossDueToDistanceBuffering;
	}

	/**
	 * @return the roomLossDueToValidation
	 */
	public int getRoomLossToValidation() {
		return roomLossToValidation;
	}

	/**
	 * @param roomLossDueToValidation the roomLossDueToValidation to set
	 */
	public void setRoomLossToValidation(int roomLossDueToValidation) {
		this.roomLossToValidation = roomLossDueToValidation;
	}



}
