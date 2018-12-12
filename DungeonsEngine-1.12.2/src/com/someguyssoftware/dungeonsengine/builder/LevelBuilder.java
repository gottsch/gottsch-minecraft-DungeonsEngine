/**
 * 
 */
package com.someguyssoftware.dungeonsengine.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.comparator.RoomDistanceComparator;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.graph.Wayline;
import com.someguyssoftware.dungeonsengine.graph.Waypoint;
import com.someguyssoftware.dungeonsengine.graph.mst.Edge;
import com.someguyssoftware.dungeonsengine.graph.mst.EdgeWeightedGraph;
import com.someguyssoftware.dungeonsengine.graph.mst.LazyPrimMST;
import com.someguyssoftware.dungeonsengine.model.Door;
import com.someguyssoftware.dungeonsengine.model.Hallway;
import com.someguyssoftware.dungeonsengine.model.IDoor;
import com.someguyssoftware.dungeonsengine.model.ILevel;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.IShaft;
import com.someguyssoftware.dungeonsengine.model.Level;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.model.Room.Type;
import com.someguyssoftware.dungeonsengine.model.Shaft;
import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.enums.Alignment;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.enums.Rotate;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.positional.Intersect;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/*
 * TODO update constructor to take in those items absolutely necessary to have a functioning builder
 * ie. world, random, startPoint, and field.
 * TODO update the .with() methods (basically setters()) - allow changing of values
 * TODO update build() methods to check for existence of properties such as startRoom, endRoom and create if neccesary
 * TODO add clear()
 */
/**
 * @author Mark Gottschling on Jul 9, 2016
 *
 */
public class LevelBuilder implements ILevelBuilder {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	/**
	 * 
	 */
	private static final double DEFAULT_FORCE_MODIFIER = 0.85;

	/**
	 * TODO should this be with or without walls?
	 */
	public static final int MIN_HORIZONTAL_DIMENSION = 3;
	/**
	 * 
	 */
	public static final int MIN_VERTICAL_DIMENSION = 2;

	/**
	 * Minimum level size
	 */
	public static final int MIN_NUMBER_OF_ROOMS = 5;
	
	/*
	 * The coords that is used to calculate the force.
	 */
	private static final ICoords FORCE_SOURCE_COORDS = new Coords(0, 0, 0);
	
	/*
	 * empty level
	 */
	public static final ILevel EMPTY_LEVEL = new Level();

	public static final List<IRoom> EMPTY_ROOMS = new ArrayList<>(1);

	public static final List<Wayline> EMPTY_WAYLINES = new ArrayList<>(1);	

	// TODO maybe wrap all these properties in a LevelArtifacts class, that way it is easy to reset
	
	private LevelConfig config;
	
	private World world;
	
	private Random random;

	/*
	 * rooms that are randomly generated
	 */
	List<IRoom> spawned = new ArrayList<>();;

	/*
	 * special rooms which are designed as <em>fixed position</em>. ex. ladder rooms, treasure rooms, boss rooms.
	 * these rooms' positions will typically be pre-determined in a location that meets all criteria.
	 * these rooms <em>will</em> be included in the resultant level.
	 */
	List<IRoom> anchors = new ArrayList<>();

	/*
	 * resultant list of buffered/spaced rooms on a single level.
	 */
	List<IRoom> rooms = new ArrayList<>();

	/*
	 * resultant list of edges from triangulation of rooms.
	 */
	List<Edge> edges = new ArrayList<>();

	/*
	 * resultant list of edges from performing minimum spanning tree on edges
	 */
	List<Edge> paths = new ArrayList<>();

	/*
	 * resultant list of horizontal and vertical lines representing hallways that connect all the rooms together
	 * by "squaring off" the paths
	 */
	List<Wayline> waylines = new ArrayList<>();
	
	/*
	 * resultant list of hallways derived from waylines
	 */
	List<Hallway> hallways = new ArrayList<>();
	
	/*
	 * where the start room should generate if it is not provided
	 */
	private ICoords startPoint;
	
	private List<IRoom> plannedRooms = new ArrayList<>();
	
	/*
	 * the bounding box in which the entire level must reside
	 */
	private AxisAlignedBB field;
	
	/*
	 * the min coords of the field
	 */
	private ICoords origin;	

	private IRoomBuilder roomBuilder;
	
	/*
	 * the number of rooms lost as a result of distance buffering
	 */
	int roomLossToDistanceBuffering = 0;
	
	/*
	 * the number of rooms lost as a result of world validation
	 */
	int roomLossToValidation = 0;
		
	/**
	 * Constructs a level builder with default config and room builder.
	 */
	public LevelBuilder(World world, Random random, AxisAlignedBB field, ICoords startPoint) {
		this.world = world;
		this.random = random;
		this.field = field;
		this.startPoint = startPoint;
		this.config = new LevelConfig();
		this.roomBuilder = new RoomBuilder(field);
		this.plannedRooms = new ArrayList<>();
	}
	
	/**
	 * 
	 * @param config
	 */
	public LevelBuilder(World world, Random random, AxisAlignedBB field, ICoords startPoint, LevelConfig config) {
		this(world, random, field, startPoint);
		this.config = config;
	}
	
	/**
	 * reset/clear any state properties
	 */
	public void reset() {
		this.plannedRooms.clear();
		this.waylines.clear();
		this.edges.clear();
		this.paths.clear();
		this.anchors.clear();
		this.hallways.clear();
		this.rooms.clear();
		this.spawned.clear();
		this.roomLossToDistanceBuffering = 0;
		this.roomLossToValidation = 0;
	}
	
	/**
	 * 
	 * @param builder
	 * @return
	 */
	public ILevelBuilder withRoomBuilder(RoomBuilder builder) {
		this.roomBuilder = builder;
		return this;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	public ILevelBuilder withConfig(LevelConfig config) {
		this.config = config;
		return this;
	}
	
	/**
	 * 
	 * @param startPoint
	 * @return
	 */
	public ILevelBuilder withStartPoint(ICoords startPoint) {
		this.startPoint = startPoint;
		return this;
	}
	
	/**
	 * 
	 * @param room
	 * @return
	 */
	public LevelBuilder withStartRoom(IRoom room) {
		// TODO ensure that room has all the start room properties set
		this.plannedRooms.add(room);
		return this;
	}
	
	public ILevelBuilder withEndRoom(IRoom room) {
		// TODO ensure that room has all the start room properties set
		this.plannedRooms.add(room);
		return this;
	}
	
	public ILevelBuilder withRoom(IRoom room) {
		this.plannedRooms.add(room);
		return this;
	}
	
	public LevelBuilder withField(AxisAlignedBB field) {
		this.setField(field);
		this.setOrigin(new Coords((int)field.minX, (int)field.minY, (int)field.minZ));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.builder.ILevelBuilder#build()
	 */
	@Override
	public ILevel build() {
		/*
		 * local handle to the start room
		 */
		IRoom startRoom = null;
		
		/*
		 *  local handle to the end room
		 */
		IRoom endRoom = null;
		
		/*
		 * return object containing all the rooms that meet build criteria and the locations of the special rooms.
		 */
		ILevel level = new Level();
		
		// TODO ensure that the field exists
		// TODO ensure that start point falls within field
		// TODO check if start room and end room are not null - elsewise generate them.
		// TODO ensure that start room and end room fall within field
		// TODO do property checks - ensure there is a start room, end room, etc and create if necessary

		// process all predefined rooms and categorize
		for (IRoom room : this.plannedRooms) {
			if (room.isStart() && startRoom == null) startRoom = room;
			else if (room.isEnd() && endRoom == null) endRoom = room;
			if (room.isAnchor())
				anchors.add(room);
			else
				spawned.add(room);
		}
		
		// create a start room if one is not provided
		if (startRoom == null) {
			startRoom = getRoomBuilder().buildStartRoom(getRandom(), getStartPoint(), getConfig());
			anchors.add(startRoom);
		}
		
		// create an end room if one is not provided
		if (endRoom == null) {
			endRoom = getRoomBuilder().buildEndRoom(getRandom(), getStartPoint()	, getConfig(), getPlannedRooms());
			anchors.add(endRoom);
		}
				
		// add randomly generated rooms
		this.spawned = spawnRooms();
		
		// sort working array based on distance
		Collections.sort(spawned, new RoomDistanceComparator(startPoint));
		
		// TODO swap with strategy pattern here. ie room = getDistanceBufferingStrat().apply(...)
		/*
		 *  move apart any intersecting rooms (uses anti-grav method). this current method uses anti-grav from the spawn only.
		 *  TODO refactor to use anti-grav against all rooms where force is lessened the greater the dist the rooms are from each other.
		 */
		this.rooms = applyDistanceBuffering();
		logger.debug("After Apply Distance Buffering Rooms.size=" + rooms.size());
		System.out.println("After Apply Distance Buffering Rooms.size=" + rooms.size() + ", room loss=" + getRoomLossToDistanceBuffering());
		
		// select rooms to use ie. filter out rooms that don't meet criteria
		this.rooms = selectValidRooms();
		logger.debug("After select valid rooms Rooms.size=" + this.rooms.size());
		System.out.println("After select valid rooms Rooms.size=" + this.rooms.size() + ", room loss=" + getRoomLossToValidation());
		if (this.rooms == null || rooms.size() < MIN_NUMBER_OF_ROOMS) {
			return EMPTY_LEVEL;
		}
		
		// record minimum dimensions of all the rooms
		Pair<Integer, Integer> minRoomDimensions = calcMinimumRoomDimensions(this.rooms);

		// normalize rooms to positive quadrant
		offsetRoomCoords(this.rooms, minRoomDimensions);
		
		// triangulate valid rooms
		this.edges = triangulate(this.rooms);
		if (edges == null) {
			return EMPTY_LEVEL;
		}
		
		// get the mst
		this.paths = calculatePaths(this.random, edges, this.rooms, config);
		
		// use a BFS from start to end to ensure a path still exists
		logger.debug("StartRoom.id=" + startRoom.getId());
		logger.debug("EndRoom.id=" + endRoom.getId());
		if (!BFS(startRoom.getId(), endRoom.getId(), rooms, paths)) {
			logger.debug("A path doesn't exist from start room to end room on level.");
			return EMPTY_LEVEL;
		}
		
		// calculate room waypoints - the coords that build a hallway (edge) between to rooms (vertice)
		this.waylines = calculateWaylines();
		if (this.waylines == EMPTY_WAYLINES) return EMPTY_LEVEL;
		
		// restore room coords and waylines back to original dimensions
		restoreRoomCoords(this.rooms, minRoomDimensions);
		
		/*
		 * TODO can go in it's own method
		 * build the hallways
		 */
		// initialize hallways
		this.hallways = new ArrayList<>();
		
		// a list to hold waylines from an L-shaped (elbow join) set of waylines
		List<Wayline> processedJoins = new ArrayList<>(10);
		
		// process each wayline
		for (Wayline line : this.waylines) {
			// build a hallway (room) from a wayline
			Hallway hallway = buildHallway(line, rooms);
			
			// add the hallway to the list of generated hallways
			hallways.add(hallway);

			addDoorsToRoom(hallway);
			
			// TODO make this its own method
			// create doors for the rooms based on the hallway doors, but on the opposite side of the room (direction)
//			for (Door d : hallway.getDoors()) {
//				// create a new door instance and flip the direction
//				Door door = new Door(d.getCoords(), d.getRoom(), d.getHallway(), d.getDirection().rotate(Rotate.ROTATE_180));
//				d.getRoom().getDoors().add(door);
//			}
			
			// TODO how to cross-ref L-shaped hallways together from waylines... they both need to be built first ?
			// if an L-shaped ie. multiple connected waylines.
			if (line.getWayline() != null) {				
				// check if second wayline is in process joins list
				if (!processedJoins.contains(line.getWayline())) {
					Hallway hallway2 = buildHallway(line.getWayline(), rooms);
					hallway2.setHallway(hallway);
					hallway.setHallway(hallway2);
					addDoorsToRoom(hallway2);
					hallways.add(hallway2);
					
					// add first wayline to processed joins
					processedJoins.add(line);
				}
			}
		}
		
		// setup the level
		IRoom room = rooms.get(0);
		int minX = room.getMinX();
		int maxX = room.getMaxX();
		int minY = room.getMinY();
		int maxY = room.getMaxY();
		int minZ = room.getMinZ();
		int maxZ = room.getMaxZ();
		
		// record min and max dimension values for level
		for (int i = 1; i < rooms.size(); i++) {
			if (rooms.get(i).getMinX() < minX) minX = rooms.get(i).getMinX();
			if (rooms.get(i).getMaxX() > maxX) maxX = rooms.get(i).getMaxX();
			if (rooms.get(i).getMinY() < minY) minY = rooms.get(i).getMinY();
			if (rooms.get(i).getMaxY() > maxY) maxY = rooms.get(i).getMaxY();
			if (rooms.get(i).getMinZ() < minZ) minZ = rooms.get(i).getMinZ();
			if (rooms.get(i).getMaxZ() > maxZ) maxZ = rooms.get(i).getMaxZ();
		}
		
		// TODO ensure that start and end room still exist
		
		// TODO need a Coords.copy() method in GottschCore
		// TODO need a wrapper for AxisAlignedBB in GottschCore
		
		// set all level properties
		level.setStartPoint(new Coords(getStartPoint()));
		level.setStartRoom(startRoom.copy());
		level.setEndRoom(endRoom.copy());
		level.setField(new AxisAlignedBB(getField().minX, getField().minY, getField().minZ, getField().maxX, getField().maxY, getField().maxZ ));
		level.setRooms(new ArrayList<IRoom>(rooms));
		level.setHallways(new ArrayList<Hallway>(hallways));
		level.setConfig(getConfig().copy());
		
		// TODO refactor into Pair<Int, Int> dimsX, dimsY, dimsZ
		level.setMinX(minX);
		level.setMaxX(maxX);
		level.setMinY(minY);
		level.setMaxY(maxY);
		level.setMinZ(minZ);
		level.setMaxZ(maxZ);
		
		return level;
	}

	/**
	 * 
	 * @param rooms2
	 * @return
	 */
	private Pair<Integer, Integer> calcMinimumRoomDimensions(List<IRoom> rooms) {
		int mx = 0;
		int mz = 0;
		for (int i = 0; i < rooms.size(); i++) {
			if (rooms.get(i).getMinX() < mx) mx = rooms.get(i).getMinX();
			if (rooms.get(i).getMinZ() < mz) mz = rooms.get(i).getMinZ();
		}
		return Pair.of(new Integer(mx),  new Integer(mz));
	}
	
	/**
	 * 
	 * @param rooms2
	 * @param minRoomDimensions
	 */
	private void offsetRoomCoords(List<IRoom> rooms2, Pair<Integer, Integer> minRoomDimensions) {
		// if dimensions are negative, offset all rooms by positive (Math.abs()) amount +1
		if (minRoomDimensions.getLeft() < 0 || minRoomDimensions.getRight() < 0) {
			for (IRoom room : rooms) {
				room.setCoords(room.getCoords().add(Math.abs(minRoomDimensions.getLeft())+1, 0, Math.abs(minRoomDimensions.getRight())+1));
			}
		}
	}
	
	/**
	 * 
	 * @param rooms2
	 * @param minRoomDimensions
	 */
	private void restoreRoomCoords(List<IRoom> rooms, Pair<Integer, Integer> dims) {
		// revert room dimensions and generated waylines back to original values by removing offset.
		if (dims.getLeft() < 0 || dims.getRight() < 0) {
			for (IRoom room : rooms) {
				room.setCoords(room.getCoords().add(dims.getLeft()-1, 0, dims.getRight()-1));
			}
			for (Wayline line : this.waylines) {
				line.getPoint1().setCoords(line.getPoint1().getCoords().add(dims.getLeft()-1, 0, dims.getRight()-1));
				line.getPoint2().setCoords(line.getPoint2().getCoords().add(dims.getLeft()-1, 0, dims.getRight()-1));
			}
		}
	}
	
	/**
	 * 
	 * @param level
	 * @return
	 */
	protected List<IRoom> spawnRooms() {
		List<IRoom> rooms = new ArrayList<>();

		int levelSize = Math.max(
				MIN_NUMBER_OF_ROOMS,
				RandomHelper.randomInt(getRandom(), 
						getConfig().getNumberOfRooms().getMinInt(), 
						getConfig().getNumberOfRooms().getMaxInt())
			);
		
		// generate rooms
		for (int i = 0; i < levelSize; i++) {
			IRoom room = new Room(i);
			room = getRoomBuilder().buildRoom(getRandom(), getStartPoint(), getConfig(), room);
			if (room != RoomBuilder.EMPTY_ROOM) {
				// add to the working list that contains all the rooms sorted on distance (farthest to closest)
				rooms.add(room);
			}
		}
		return rooms;
	}
//
//	/**
//	 * 
//	 * @param rand
//	 * @param roomIn
//	 * @param spawnPoint
//	 * @param config
//	 * @return
//	 */
//	protected IRoom randomizeRoom(IRoom roomIn) {
//		return randomizeRoom(getRandom(), getOrigin(), getField(), roomIn, getConfig());
//	}
	
//	/**
//	 * 
//	 * @param random
//	 * @param origin
//	 * @param field
//	 * @param roomIn
//	 * @param config
//	 * @return
//	 */
//	protected IRoom randomizeRoom(Random random, ICoords origin, AxisAlignedBB field, IRoom roomIn, LevelConfig config) {
//		// randomize dimensions
//		IRoom room = randomizeDimensions(random, roomIn, config);
//
//		// randomize the rooms
//		room = randomizeRoomCoords(random, origin, field, room, config);
//		// calculate distance squared
////		room.setDistance(room.getCenter().getDistanceSq(getStartPoint()));
//		
//		// set the degrees (number of edges)
//		room.setDegrees(RandomHelper.randomInt(random, config.getDegrees().getMinInt(), config.getDegrees().getMaxInt()));
//		// randomize a direction
//		room.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));
//
//		return room;
//	}
	

//	/**
//	 * 
//	 * @param random
//	 * @param roomIn
//	 * @param config
//	 * @return
//	 */
//	protected IRoom randomizeRoomCoords(IRoom roomIn) {
//		return randomizeRoomCoords(getRandom(), getOrigin(), getField(), roomIn, getConfig());
//	}
	
//	/**
//	 * 
//	 * @param random
//	 * @param origin
//	 * @param field
//	 * @param roomIn
//	 * @param config
//	 * @return
//	 */
//	protected IRoom randomizeRoomCoords(Random random, ICoords origin, AxisAlignedBB field, IRoom roomIn, LevelConfig config) {
//		IRoom room = roomIn.copy();//new Room(roomIn);
//		// generate a ranom set of coords
//		ICoords c = randomizeCoords(random, origin, field, config);
//		// center room using the random coords
//		room.setCoords(c.add(-(room.getWidth()/2), 0, -(room.getDepth()/2)));
//		
//		// Y Variance
////		room.getCoords().setY(RandomHelper.randomInt(random, config.getYVariance().getMinInt(), config.getYVariance().getMaxInt()));
//		return room;
//	}

//	/**
//	 * 
//	 * @return
//	 */
//	protected ICoords randomizeCoords() {
//		return randomizeCoords(getRandom(), getOrigin(), getField(), getConfig());
//	}
	
//	/**
//	 * 
//	 * @param random
//	 * @param config
//	 * @return
//	 */
//	protected ICoords randomizeCoords(Random random, ICoords origin, AxisAlignedBB field, LevelConfig config) {
//		int x = RandomHelper.randomInt(random, 0, (int) field.maxX);
//		int y = RandomHelper.randomInt(random, config.getYVariance().getMinInt(), config.getYVariance().getMaxInt());
//		int z = RandomHelper.randomInt(random, 0, (int) field.maxZ);
//		return origin.add(x, y, z);
//	}
	
//	/**
//	 * 
//	 * @param rand
//	 * @param roomIn
//	 * @param config
//	 * @return
//	 */
//	protected IRoom randomizeDimensions(IRoom roomIn) {
//		return randomizeDimensions(getRandom(), roomIn, getConfig());
//	}
	
//	protected IRoom randomizeDimensions(Random random, IRoom roomIn, LevelConfig config) {
//		IRoom room = roomIn.copy();//new Room(roomIn);
//		room.setWidth(Math.max(Room.MIN_WIDTH, RandomHelper.randomInt(random, config.getWidth().getMinInt(), config.getWidth().getMaxInt())));
//		room.setDepth(Math.max(Room.MIN_DEPTH, RandomHelper.randomInt(random, config.getDepth().getMinInt(), config.getDepth().getMaxInt())));
//		room.setHeight(Math.max(Room.MIN_HEIGHT, RandomHelper.randomInt(random, config.getHeight().getMinInt(), config.getHeight().getMaxInt())));		
//		return room;
//	}

	/**
	 * 
	 * @param rand
	 * @param startPoint
	 * @param anchors
	 * @param rooms
	 * @param config
	 * @return
	 */
	protected List<IRoom> applyDistanceBuffering() {
		List<IRoom> bufferedRooms = new ArrayList<>();
		/*
		 * a count of the number times a single room is processed against the list of buffered rooms
		 */
		int processCount = 0;

		// add anchors to buffereds
		bufferedRooms.addAll(this.anchors);
		int i = 0;

		/*
		 * process all the unbuffered rooms that were added for this level
		 */
		rooms:
			for (IRoom room : this.spawned) {
				if (room.isReject()) {
//					logger.info(String.format("Ignoring... room is flagged as rejected."));
					incrementLossToDistanceBuffering(1);
					continue;
				}
				processCount = 0;
				AxisAlignedBB roomBB = room.getXZBoundingBox();

				if (bufferedRooms != null && bufferedRooms.size() > 0) {

					boolean processBufferedRooms = true;
					bufferedRooms:					
						while(processBufferedRooms) {
							// increment the process count
							processCount++;
							if (processCount > 50) {
//								logger.trace("Detected endless loop when positioning room ==> room REJECTED.");
								System.out.println("Detected endless loop when positioning room ==> room REJECTED.");
								room.setReject(true);
								incrementLossToDistanceBuffering(1);
								continue rooms;
							}
							for (IRoom bufferedRoom : bufferedRooms) {
								//Room bufferedRoom = bufferedRooms.get(bufferedRoomIndex);
//																logger.info("\n-------------------\nTesting against processed room: " + bufferedRoom.getId());
								AxisAlignedBB bufferedBB = bufferedRoom.getXZBoundingBox();

								// test if intersect
								int failSafeCount = 0;

								while (roomBB.intersects(bufferedBB)) {
									// TODO need to ensure that an endless loop doesn't occur of a room toggling back and forth

//									logger.info("Room intersects with processed room:" + roomBB + "; " + bufferedBB);
									// testing whether room is anchored
									if (room.isAnchor()) {
//										logger.info("Room is anchored. Remove from level as it can not change position.");
										System.out.println("Room is anchored. Remove from level as it can not change position.");
										room.setReject(true);
										incrementLossToDistanceBuffering(1);
										continue rooms;
									}
									/* determine vector from start point.
									 * this produces an "explosion" vector moving away from the start (epicenter).
									 * ** seems to take more processing cycles as there are still overlaps after one round
									 */
									double angle = room.getCenter().getXZAngle(startPoint);
//									logger.info(String.format("Calculating angle from %s to %s", room.getCenter(), bufferedRoom.getCenter()));
//									double angle = room.getCenter().getXZAngle(bufferedRoom.getCenter());


									/*
									 * this produces an anti-grav vector moving away from the intersected room.
									 * ** seems to work with minimal cycles
									 * NOTE this could process and endless cycle if the room insects two other rooms which just push
									 * back and forth.
									 */
//									 double angle = room.getCenter().getXZAngle(bufferedRoom.getCenter());
//									 logger.info("Angle: " + angle);

									// determine force - relative distance from 0,0,0 to difference in overlap
									Intersect intersect = Intersect.getIntersect(roomBB, bufferedBB);
//									logger.info("intersect:" + intersect);

									/**
									 * Calculate a force value that is equals to the distance from 0 to the amount of intersection between the two rooms being compared.
									 * Add an additional 10% of force  applied to a caridnal directions helps ensure that the amount of adjustment is more than the amount of intersect.  
									 */
									double force = FORCE_SOURCE_COORDS.getDistance(intersect.getX(), 0, intersect.getZ()) * DEFAULT_FORCE_MODIFIER;
//									logger.info("Force:" + force);

									double xForce = Math.sin(angle) * force;
							        double zForce = Math.cos(angle) * force;
							        
//							        logger.info("xForce:" + xForce);
//							        logger.info("zForce:" + zForce);
									// apply force vector to room
									room = room.addXZForce(angle, force);
									// update distance
//									room.setDistance(room.getCenter().getDistanceSq(startPoint));
									roomBB = room.getXZBoundingBox();
									//									logger.info("New Room:" +  room);

									// check again if still intersect
									if (roomBB.intersects(bufferedBB)) {
										//										logger.info("Still intersects! Moving again ...");
										failSafeCount++;
										if (failSafeCount >= 5) {
											// stop processing this room (ie drop altogether)
//											logger.info("Unable to position room... rejecting room.");
											System.out.println("Unable to position room... rejecting room -> " + room.getId());
											room.setReject(true);
											incrementLossToDistanceBuffering(1);
											continue rooms;
										}
									}
									else {
										// no longer intersects; will exit the while loop
										// test against all buffered rooms again to ensure that it doesn't intersect another buffered room now
										//										logger.info("No more intersection... testing against all buffered rooms again.");
										continue bufferedRooms;
									}
								}
								// reset the fail safe
								failSafeCount = 0;
							}
							/*
							 * flag the endless while loop to stop looping.
							 * this is the typical action to take when all the buffered rooms have been processed.
							 */
							processBufferedRooms = false;
						}
				}
				// add to the level list
//				logger.info(i + "] Adding room "+ room.getId());
				bufferedRooms.add(room);
				i++;
			}
//		logger.info("BufferedRooms.size=:" + bufferedRooms.size());
		return bufferedRooms;
	}

	/**
	 * 
	 * @param sourceLevel the originating level to join from. This level owns the shaft that is generated.
	 * @param destLevel the destination level to join to.
	 * @return
	 */
	@Override
	public IShaft join(ILevel sourceLevel, ILevel destLevel) {
		Shaft shaft = EMPTY_SHAFT;
		
		List<IRoom> destRooms = destLevel.getRooms().stream().filter(room -> room.isEnd()).collect(Collectors.toList());
		List<IRoom> sourceRooms = sourceLevel.getRooms().stream().filter(room -> room.isStart()).collect(Collectors.toList());
		logger.debug("destRooms.size=" + destRooms.size());
		logger.debug("sourceRooms.size=" + sourceRooms.size());
		
		// TODO this line should throw and error
		// check if either list is null
		if (destRooms == null || sourceRooms == null || destRooms.size() == 0 || sourceRooms.size() == 0) return shaft;
		
		IRoom destRoom = destRooms.get(0);
		IRoom sourceRoom = sourceRooms.get(0);
		logger.debug("destRoom: " + destRoom);
		logger.debug("sourceRoom: " + sourceRoom);
		
		shaft =  join(sourceRoom, destRoom);
		logger.debug("shaft: " + shaft);
		// add the shaft to the list
		if (shaft != EMPTY_SHAFT) {
			sourceLevel.getShafts().add(shaft);
		}
		
		return shaft;
	}
	
	/**
	 * TODO needs to throw an error if errors
	 * @param sourceRoom
	 * @param destRoom
	 * @return
	 */
	@Override
	public Shaft join(IRoom sourceRoom, IRoom destRoom) {
		Shaft shaft = EMPTY_SHAFT;
		// built the shaft from start room (-1) to end room (+0)
		if (destRoom.getMinY() - sourceRoom.getMaxY() > 1) {
			ICoords center = destRoom.getCenter();
			logger.debug("center of dest room: " + center);

			shaft = (Shaft) new Shaft()
				.setDirection(sourceRoom.getDirection())
				.setDegrees(0)
				.setType(Type.LADDER);

			shaft.setWidth(3)
				.setDepth(3)
				.setHeight(destRoom.getMinY() - sourceRoom.getMaxY()-1);
			shaft.setSource(sourceRoom);
			shaft.setDest(destRoom);
			
			// set the coords - it depends on the direction the room is facing
			switch(shaft.getDirection()) {
			case NORTH:
				shaft.setCoords(new Coords(center.getX()-1, sourceRoom.getMaxY()+1, center.getZ()));
				break;
			case EAST:
				shaft.setCoords(new Coords(center.getX()-2, sourceRoom.getMaxY()+1, center.getZ()-1));
				break;
			case SOUTH:
				shaft.setCoords(new Coords(center.getX()-1, sourceRoom.getMaxY()+1, center.getZ()-2));
				break;
			case WEST:
				shaft.setCoords(new Coords(center.getX(), sourceRoom.getMaxY()+1, center.getZ()-1));
				break;
			default:
			}			
		}	
		logger.debug("shaft: " + shaft);
		return shaft;
	}
	
//	/**
//	 * 
//	 * @param world
//	 * @param rand
//	 * @param startPoint
//	 * @return
//	 */
//	public Level build(World world, Random rand, ICoords startPoint) {
//		return build(world, rand, startPoint, this.config);
//	}
	
//	/**
//	 * Minecraft starts coordinates in the top left, postive growing to the right (east) and down (south).
//	 * Therefor:
//	 * quadrant 1 = bottom right,
//	 * quadrant 2 = bottom left,
//	 * quadrant 3 = top left,
//	 * quadrant 4 = top right
//	 * @param config
//	 * @return
//	 */
//	public Level build(World world, Random rand, ICoords startPoint, LevelConfig config) {
//		/*
//		 * special rooms which are designed as <em>fixed position</em>. ex. ladder rooms, treasure rooms, boss rooms.
//		 * these rooms' positions will typically be pre-determined in a location that meets all criteria.
//		 * these rooms <em>will</em> be included in the resultant level.
//		 */
////		List<Room> anchors = new ArrayList<>();
//
//		/*
//		 * a list of manualy/pre-generated rooms to be used in the level
//		 */
//		List<Room> predefinedRooms = new ArrayList<>();
//		
//		/*
//		 * the start of the level
//		 */
//		Room startRoom = buildStartRoom();
//		if (startRoom == EMPTY_ROOM) {
//			if (logger.isWarnEnabled()) {
//				logger.warn(String.format("Start Room has invalid Minecraft world room conditions: %s", startRoom.toString()));
//			}
//			return EMPTY_LEVEL;
//		}
//		predefinedRooms.add(startRoom);
//		
//		/*
//		 * the end room of the level.
//		 * only one way into the end room - only if boss/treasure room
//		 */
//		Room endRoom = buildEndRoom(predefinedRooms);
//		if (endRoom == EMPTY_ROOM) {
//			return EMPTY_LEVEL;
//		}
//		predefinedRooms.add(endRoom);
//		
//		// add some obstacles to build more randomness to the level
//		// TODO check config for number of obstacles
//		Room obstacle = new Room();
//		obstacle.setAnchor(true);
//		obstacle.setObstacle(true);
//		obstacle = randomizeRoom(obstacle);
////		anchors.add(obstacle);
//
//		/**
//		 * build the level
//		 */
//		return build(world, rand, startPoint, predefinedRooms, config);
//	}

//	/**
//	 * 
//	 * @param world
//	 * @param random
//	 * @param startPoint
//	 * @param plannedRooms
//	 * @return
//	 */
//	public Level build(World world, Random random, ICoords startPoint, List<Room> plannedRooms) {
//		return build(world, random, startPoint, plannedRooms, this.config);
//	}
	
//	/**
//	 * 
//	 * @param rand
//	 * @param startPoint
//	 * @param startRooms
//	 * @param endRooms
//	 * @param config
//	 * @return
//	 */
//	public Level build(World world, Random rand, ICoords startPoint, List<Room> plannedRooms, LevelConfig config) {
//		/*
//		 * special rooms which are designed as <em>fixed position</em>. ex. ladder rooms, treasure rooms, boss rooms.
//		 * these rooms' positions will typically be pre-determined in a location that meets all criteria.
//		 * these rooms <em>will</em> be included in the resultant level.
//		 */
//		List<Room> anchors = new ArrayList<>();
//
//		/*
//		 * rooms that are randomly generated
//		 */
//		List<Room> spawned = null;
//
//		/*
//		 * resultant list of buffered/spaced rooms on a single level.
//		 */
//		List<Room> rooms = null;
//
//		/*
//		 * resultant list of edges from triangulation of rooms.
//		 */
//		List<Edge> edges = null;
//
//		/*
//		 * resultant list of edges from performing minimum spanning tree on edges
//		 */
//		List<Edge> paths = null;
//
//		/*
//		 * resultant list of horizontal and vertical lines representing hallways that connect all the rooms together
//		 * by "squaring off" the paths
//		 */
//		List<Wayline> waylines = null;
//		
//		/*
//		 * resultant list of hallways derived from waylines
//		 */
//		List<Hallway> hallways = null;
//		
//		/*
//		 * return object containing all the rooms that meet build criteria and the locations of the special rooms.
//		 */
//		Level level = new Level();
//		
//		Room startRoom = null;
//		Room endRoom = null;
//		
//		// add randomly generated rooms
//		spawned = spawnRooms();
//		logger.debug("Spawned.size=" + spawned.size());
//		
//		// process all predefined rooms and categorize
//		for (Room room : plannedRooms) {
//			if (room.isStart() && startRoom == null) startRoom = room;
//			else if (room.isEnd() && endRoom == null) endRoom = room;
//			if (room.isAnchor())
//				anchors.add(room);
//			else
//				spawned.add(room);
//		}
//		
//		// sort working array based on distance
//		Collections.sort(spawned, Room.distanceComparator);
//				
//		// move apart any intersecting rooms (uses anti-grav method)
//		rooms = applyDistanceBuffering(rand, startPoint, anchors, spawned, config);
//		logger.debug("After Apply Distance Buffering Rooms.size=" + rooms.size());
//		// select rooms to use ie. filter out rooms that don't meet criteria
//		rooms = selectValidRooms(rooms);
//		logger.debug("After select valid rooms Rooms.size=" + rooms.size());
//		if (rooms == null || rooms.size() < MIN_NUMBER_OF_ROOMS) {
//			return EMPTY_LEVEL;
//		}
//		// TODO record as a value pair, move to own method
//		// record minimum dimensions of all the rooms
//		int mx = 0;
//		int mz = 0;
//		for (int i = 0; i < rooms.size(); i++) {
//			if (rooms.get(i).getMinX() < mx) mx = rooms.get(i).getMinX();
//			if (rooms.get(i).getMinZ() < mz) mz = rooms.get(i).getMinZ();
//		}
////		logger.debug("Min X/Z values=" + mx + ", " + mz);
//		
//		// TODO move own method
//		// if dimensions are negative, offset all rooms by positive (Math.abs()) amount +1
//		if (mx < 0 || mz < 0) {
//			for (Room room : rooms) {
//				room.setCoords(room.getCoords().add(Math.abs(mx)+1, 0, Math.abs(mz)+1));
//			}
//		}
//		
//		/*
//		 * NOTE triangulate can only operate on a positive plane of vertices.
//		 * NOTE triangulate requires at least 3 points (rooms)
//		 * therefor all room must be offset into the positive x/z plane.
//		 */
//		// triangulate valid rooms
//		edges = triangulate(rooms);
//		if (edges == null) {
//			return EMPTY_LEVEL;
//		}
//		
//		// get the mst
//		paths = calculatePaths(rand, edges, rooms, config);
//
//		// TODO a BFS from start to end to ensure a path still exists
//		// path = findPath(start, end);
//		logger.debug("StartRoom.id=" + startRoom.getId());
//		logger.debug("EndRoom.id=" + endRoom.getId());
//		if (!BFS(startRoom.getId(), endRoom.getId(), rooms, paths)) {
//			logger.debug("A path doesn't exist from start room to end room on level.");
//			return EMPTY_LEVEL;
//		}
//		
//		// calculate room waypoints - the coords that build a hallway (edge) between to rooms (vertice)
//		waylines = calculateWaylines(rand, paths, rooms, config);
//		if (waylines == EMPTY_WAYLINES) return EMPTY_LEVEL;
//
////				Collections.sort(rooms, Room.distanceComparator);
//
//		// revert room dimensions and generated waylines back to original values by removing offset.
//		if (mx < 0 || mz < 0) {
//			for (Room room : rooms) {
//				room.setCoords(room.getCoords().add(mx-1, 0, mz-1));
//			}
//			for (Wayline line : waylines) {
//				line.getPoint1().setCoords(line.getPoint1().getCoords().add(mx-1, 0, mz-1));
//				line.getPoint2().setCoords(line.getPoint2().getCoords().add(mx-1, 0, mz-1));
//				// NOTE this might be easier to accomplish if ALL waylines (joints included) were added to the list
//				// BUT still refererncing each other in joint. Then in buildHalls() create a list of ref'ed and check against it so that
//				// double halls aren't built.
////				if (line.getWayline() != null) {
////					line.getWayline().getPoint1().setCoords(line.getWayline().getPoint1().getCoords().add(mx-1, 0, mz-1));
////					line.getWayline().getPoint2().setCoords(line.getWayline().getPoint2().getCoords().add(mx-1, 0, mz-1));					
////				}
//			}
//		}
//		
//		/*
//		 * build the hallways
//		 */
//		// initialize hallways
//		hallways = new ArrayList<>();
//		
//		// a list to hold waylines from an L-shaped (elbow join) set of waylines
//		List<Wayline> processedJoins = new ArrayList<>(10);
//		
//		// process each wayline
//		for (Wayline line : waylines) {
//			// build a hallway (room) from a wayline
//			//Hallway hallway = Hallway.fromWayline(line, level.getRooms());
//			Hallway hallway = buildHallway(line, rooms);
//			
//			// add the hallway to the list of generated hallways
//			hallways.add(hallway);
//
//			addDoorsToRoom(hallway);
//			
//			// TODO make this its own method
//			// create doors for the rooms based on the hallway doors, but on the opposite side of the room (direction)
////			for (Door d : hallway.getDoors()) {
////				// create a new door instance and flip the direction
////				Door door = new Door(d.getCoords(), d.getRoom(), d.getHallway(), d.getDirection().rotate(Rotate.ROTATE_180));
////				d.getRoom().getDoors().add(door);
////			}
//			
//			// TODO how to cross-ref L-shaped hallways together from waylines... they both need to be built first ?
//			// if an L-shaped ie. multiple connected waylines.
//			if (line.getWayline() != null) {				
//				// check if second wayline is in process joins list
//				if (!processedJoins.contains(line.getWayline())) {
//					Hallway hallway2 = buildHallway(line.getWayline(), rooms);
//					hallway2.setHallway(hallway);
//					hallway.setHallway(hallway2);
//					addDoorsToRoom(hallway2);
//					hallways.add(hallway2);
//					
//					// add first wayline to processed joins
//					processedJoins.add(line);
//				}
//			}
//		}
//		
//		// setup the level
//		Room room = rooms.get(0);
//		int minX = room.getMinX();
//		int maxX = room.getMaxX();
//		int minY = room.getMinY();
//		int maxY = room.getMaxY();
//		int minZ = room.getMinZ();
//		int maxZ = room.getMaxZ();
//		
//		// record min and max dimension values for level
//		for (int i = 1; i < rooms.size(); i++) {
//			if (rooms.get(i).getMinX() < minX) minX = rooms.get(i).getMinX();
//			if (rooms.get(i).getMaxX() > maxX) maxX = rooms.get(i).getMaxX();
//			if (rooms.get(i).getMinY() < minY) minY = rooms.get(i).getMinY();
//			if (rooms.get(i).getMaxY() > maxY) maxY = rooms.get(i).getMaxY();
//			if (rooms.get(i).getMinZ() < minZ) minZ = rooms.get(i).getMinZ();
//			if (rooms.get(i).getMaxZ() > maxZ) maxZ = rooms.get(i).getMaxZ();
//		}
//		
//		// update the level
//		level.setStartPoint(startPoint);
//		level.setStartRoom(startRoom);
//		level.setEndRoom(endRoom);
//		level.setRooms(rooms);
////		level.setEdges(edges);
////		level.setPaths(paths);
////		level.setWaylines(waylines);
//		level.setHallways(hallways);
//		level.setMinX(minX);
//		level.setMaxX(maxX);
//		level.setMinY(minY);
//		level.setMaxY(maxY);
//		level.setMinZ(minZ);
//		level.setMaxZ(maxZ);
//		level.setConfig(config);
//
//		return level;
//	}
	
	/**
	 * 
	 * @param hallway
	 */
	private void addDoorsToRoom(Hallway hallway) {
		for (IDoor d : hallway.getDoors()) {
			// create a new door instance and flip the direction
			Door door = new Door(d.getCoords(), d.getRoom(), d.getHallway(), d.getDirection().rotate(Rotate.ROTATE_180));
			d.getRoom().getDoors().add(door);
		}
	}

	/**
	 * 
	 * @param rand
	 * @param edges
	 * @param rooms
	 * @param config
	 * @return
	 */
	protected List<Edge> calculatePaths(Random rand, List<Edge> edges, List<IRoom> rooms, LevelConfig config) {
		/*
		 * holds are the reduced edges generated by the Minimun Spanning Tree
		 */
		List<Edge> paths = new ArrayList<>();
		/**
		 * counts the number of edges are assigned to each node/vertex
		 */
		int[] edgeCount = new int[rooms.size()];

		// reduce all edges to MST
		EdgeWeightedGraph graph = new EdgeWeightedGraph(rooms.size(), edges);
		LazyPrimMST mst = new LazyPrimMST(graph);
		for (Edge e : mst.edges()) {
			if (e.v < rooms.size() && e.w < rooms.size()) {
				IRoom room1 = rooms.get(e.v);
				IRoom room2 = rooms.get(e.w);	
				paths.add(e);
				edgeCount[room1.getId()]++;
				edgeCount[room2.getId()]++;
			}
			else {
				logger.warn(String.format("Ignored Room: array out-of-bounds: v: %d, w: %d", e.v, e.w));
			}
		}
		
//		for (int i = 0; i < edgeCount.length; i++) {
//			logger.info("Room " + i + " has asigned edges: " + edgeCount[i]);
//			
//		}

		// add more edges
		int addtionalEdges = (int) (edges.size() * 0.25); // TODO get the % from config
		for (int i = 0 ; i < addtionalEdges; i++) {
			int pos = rand.nextInt(edges.size());
			Edge e = edges.get(pos);
			// ensure that only non-used edges are selected (and doesn't increment the counter)
			IRoom room1 = rooms.get(e.v);
			IRoom room2 = rooms.get(e.w);
			if (!room1.isEnd() && !room2.isEnd() &&
					edgeCount[room1.getId()] < room1.getDegrees() && edgeCount[room2.getId()] < room2.getDegrees()) {
				paths.add(e);
				edgeCount[room1.getId()]++;
				edgeCount[room2.getId()]++;				
//				logger.info("Adding path from " + room1.getId() + " to " + room2.getId());
			}
//			else {
//				logger.info("Rejected additional path due to exceeded degrees from " + room1.getId() + " to " + room2.getId());
//			}
		}
		return paths;
	}
	
	/**
	 * 
	 * @param wayline
	 * @param rooms
	 * @return
	 */
	public Hallway buildHallway(Wayline wayline, List<IRoom> rooms) {
		int width = 3;
		int depth = 3;
		
		// work with temp way points
		Waypoint startPoint = null;
		Waypoint endPoint = null;
		ICoords startCoords = null;
		boolean isElbowJoint = false;
		
		// HORIZONTAL (WEST <--> EAST)
		if (wayline.getAlignment() == Alignment.HORIZONTAL) {
			// determine which point is the "start point" - having the smallest coords
			if (wayline.getPoint1().getX() < wayline.getPoint2().getX()) {
				startPoint = wayline.getPoint1();
				endPoint = wayline.getPoint2();
			}
			else {
				startPoint = wayline.getPoint2();
				endPoint = wayline.getPoint1();
			}

			// determine if this is a "elbow joint" wayline
			if (!startPoint.isTerminated() || !endPoint.isTerminated()) {
				isElbowJoint = true;
			}
			
			/*
			 * update start/end point depending on isTerminal
			 * this makes the elbow joint 1 block longer so that they line up correctly
			 */
			if (isElbowJoint) {
				if (!startPoint.isTerminated()) {
					startPoint.setCoords(startPoint.getCoords().add(-1, 0, 0));
				}
				
				if (!endPoint.isTerminated()) {
					endPoint.setCoords(endPoint.getCoords().add(1, 0, 0));
				}
			}
			// update the width
			width = Math.abs(startPoint.getX() - endPoint.getX()) + 1;
			
			/*
			 *  this is to maintain the actual hallway (air part) to still be along the wayline,
			 *  since the hallway is 3 wide (2 walls and 1 air)
			 */
			startCoords = startPoint.getCoords();
			startCoords = startCoords.add(0, 0, -1);
			
			// shift if non-terminal (ie an elbow joint)

		}
		// VERTICAL (NORTH <--> SOUTH)
		else {
			// determine which point is the "start point" - having the smallest coords
			if (wayline.getPoint1().getZ() < wayline.getPoint2().getZ()) {
				startPoint = wayline.getPoint1();
				endPoint = wayline.getPoint2();
			}
			else {
				startPoint = wayline.getPoint2();
				endPoint = wayline.getPoint1();
			}
			
			/*
			 * update start/end point depending on isTerminal
			 * this makes the elbow joint 1 block longer so that they line up correctly
			 */		
			if (isElbowJoint) {
				if (!startPoint.isTerminated()) {
					startPoint.setCoords(startPoint.getCoords().add(0, 0, -1));
				}				
				if (!endPoint.isTerminated()) {
					endPoint.setCoords(endPoint.getCoords().add(0, 0, 1));
				}
			}
			// update the depth
			depth = Math.abs(startPoint.getZ( ) - endPoint.getZ()) + 1;
			
			// left-shift by one since horiztonal hallways are 3 depth
			// this is to maintain the actual hallway (air part) to still be along the wayline.
			startCoords = startPoint.getCoords();
			startCoords = startCoords.add(-1, 0, 0);
		}
		
		// get the rooms referenced by the waypoints
		IRoom room1 = rooms.get(startPoint.getId());
		IRoom room2 = rooms.get(endPoint.getId());		

		 // the start/end points y-vlaue isn't set, so update them.
		startPoint.setCoords(startPoint.getCoords().resetY(room1.getCoords().getY()));
		endPoint.setCoords(endPoint.getCoords().resetY(room2.getCoords().getY()));

		// calculate what the dimensions should be
		int height = Math.abs(
				Math.min(room1.getMinY(), room2.getMinY()) - 
				Math.max(room1.getMinY(), room2.getMinY())
				) + 1+ 3; // NOTE why 3? because a doorway is 2 + ceiling block
		
		// create a temp room out of the dimensions
		Hallway hallway = (Hallway) new Hallway().setCoords(
				new Coords(
						startCoords.getX(),
						startPoint.getCoords().getY(),
						startCoords.getZ()))
				.setWidth(width)
				.setDepth(depth)
				.setHeight(height)
				.setType(Type.HALLWAY);
		// update the alignment (Hallway specific property)
		hallway.setAlignment(wayline.getAlignment());
		
		// store the start/end point as doorCoords iff they are terminated.
		if (startPoint.isTerminated()) {
			Direction d = calculateDirection(hallway, startPoint.getCoords(), room1);
			hallway.getDoors().add(new Door(startPoint.getCoords(), room1, hallway, d));
		}
		if (endPoint.isTerminated()) {
			Direction d = calculateDirection(hallway, endPoint.getCoords(), room2);
			hallway.getDoors().add(new Door(endPoint.getCoords(), room2, hallway, d));
		}
		// TODO else { add and set Hallway property. ie this is an elbow join, and this hallway points to another hallway)
		// can't here as it is needs to process the entire wayline first, then produces the hallway
		return hallway;
	}

	/**
	 * Determines which side/direction the door is on.
	 * @param hw
	 * @param doorCoords
	 * @param room
	 */
	public Direction calculateDirection(Hallway hw, ICoords coords, IRoom room) {
		if (hw.getAlignment() == Alignment.HORIZONTAL) {
			// test which side the door is on
			if (coords.getX() == hw.getMinX()) {
				return Direction.WEST;
			}
			if (coords.getX() == hw.getMaxX()) {
				return Direction.EAST;
			}
		}
		else {
			if (coords.getZ() == hw.getMinZ()) {
				return Direction.NORTH;
			}
			if (coords.getZ() == hw.getMaxZ()) {
				return Direction.SOUTH;
			}
		}
		return null;
	}
	
	
	/**
	 * perform a breadth first search against the list of edges to determine if a path exists
	 * from one node to another.
	 * @param start
	 * @param end
	 * @param rooms
	 * @param edges
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean BFS(int start, int end, List<IRoom> rooms, List<Edge> edges) {
		// build an adjacency list
		LinkedList<Integer> adj[];

		adj = new LinkedList[rooms.size()];
		for (IRoom r : rooms) {
			adj[r.getId()] = new LinkedList<>();
		}
		
        for (Edge e : edges) {
//        	if (adj[e.v] == null) adj[e.v] = new LinkedList<>();
        	adj[e.v].add(e.w);
        	// add both directions to ensure all adjacencies are covered
        	adj[e.w].add(e.v);
//        	logger.debug("Adding edge " + e.v + " <-->  " + e.w);        	
        }

		// Mark all the vertices as not visited(By default
		// set as false)
		boolean visited[] = new boolean[rooms.size()];

		// Create a queue for BFS
		LinkedList<Integer> queue = new LinkedList<Integer>();

		// Mark the current node as visited and enqueue it
		visited[start]=true;
		queue.add(start);

		while (queue.size() != 0) {
			// Dequeue a vertex from queue and print it
			int s = queue.poll();
//			logger.debug("polling edge id: " + s);

			// Get all adjacent vertices of the dequeued vertex s
			// If a adjacent has not been visited, then mark it
			// visited and enqueue it
			Iterator<Integer> i = adj[s].listIterator();
			while (i.hasNext()) {
				int n = i.next();
				if (n == end) return true;
				
				if (!visited[n]) {
					visited[n] = true;
					queue.add(n);
				}
			}
		}		
		return false;
	}
	
	/**
	 * 
	 * @param rooms
	 * @return
	 */
	protected List<Edge> triangulate(List<IRoom> rooms) {
		/*
		 * maps all rooms by XZ plane (ie x:z)
		 * this is required for the Delaunay Triangulation library because it only returns edges without any identifying properties, only points
		 */
		Map<String, IRoom> map = new HashMap<>();
		/*
		 * holds all rooms in Vector2D format.
		 * used for the Delaunay Triangulation library to calculate all the edges between rooms.
		 * 
		 */
		Vector<Vector2D> pointSet = new Vector<>();		
		/*
		 * holds all the edges that are produced from triangulation
		 */
		List<Edge> edges = new ArrayList<>();
		/*
		 *  weight/cost array of all rooms
		 */
		double[][] matrix = LevelBuilder.getDistanceMatrix(rooms);
		/**
		 * a flag to indicate that an edge leading to the "end" room is created
		 */
		boolean isEndEdgeMet = false;
		int endEdgeCount = 0;

		// sort rooms by id
		Collections.sort(rooms, Room.idComparator);

		// map all rooms by XZ plane and build all edges.
		for (IRoom room : rooms) {
			ICoords center = room.getCoords();
			// map out the rooms by IDs
			map.put(center.getX() + ":" + center.getZ(), room);
			// convert coords into vector2d for triangulation
			Vector2D v = new Vector2D(center.getX(), center.getZ());
//			logger.debug(String.format("Room.id: %d = Vector2D: %s", room.getId(), v.toString()));
			pointSet.add(v);
		}

		// triangulate the set of points
		DelaunayTriangulator triangulator = null;
		try {
			triangulator = new DelaunayTriangulator(pointSet);
			triangulator.triangulate();
		}
		catch(NotEnoughPointsException e) {
			logger.warn("Not enough points where provided for triangulation. Level generation aborted.");
			return null; // TODO return empty list
		}
		catch(Exception e) {
			if (rooms !=null) logger.debug("rooms.size=" + rooms.size());
			else logger.debug("Rooms is NULL!");
			if (pointSet != null) logger.debug("Pointset.size=" + pointSet.size());
			else logger.debug("Pointset is NULL!");
			
			logger.error("Unable to triangulate: ", e);
		}

		// retrieve all the triangles from triangulation
		List<Triangle2D> triangles = triangulator.getTriangles();

		for(Triangle2D triangle : triangles) {
			// locate the corresponding rooms from the points of the triangles
			IRoom r1 = map.get((int)triangle.a.x + ":" + (int)triangle.a.y);
			IRoom r2 = map.get((int)triangle.b.x + ":" + (int)triangle.b.y);
			IRoom r3 = map.get((int)triangle.c.x + ":" + (int)triangle.c.y);

			// build an edge based on room distance matrix
			// begin Minimum Spanning Tree calculations
			Edge e = new Edge(r1.getId(), r2.getId(), matrix[r1.getId()][r2.getId()]);
			
			// TODO for boss room, not necessarily end room
			// remove any edges that lead to the end room if the end room already has one edge
			// remove (or don't add) any edges that lead to the end room if the end room already has it's maximum edges (degrees)
			if (!r1.isEnd() && !r2.isEnd()) {
//			if (!r1.getType().equals(Type.BOSS) && !r2.getType().equals(Type.BOSS)) {
				edges.add(e);
			}
			else if (r1.isStart() || r2.isStart()) {
				// skip if start joins the end
			}
			else if (!isEndEdgeMet) {
				// add the edge
				edges.add(e);
				// increment the number of edges leading to the end room
				endEdgeCount++;
				// get the end room
				IRoom end = r1.isEnd() ? r1 : r2;
				if (endEdgeCount >= end.getDegrees()) {
					isEndEdgeMet = true;
				}
			}
			
			e = new Edge(r2.getId(), r3.getId(), matrix[r2.getId()][r3.getId()]);
			if (!r2.isEnd() && !r3.isEnd()) {
				edges.add(e);
			}
			else if (r1.isStart() || r2.isStart()) {
				// skip
			}
			else if (!isEndEdgeMet) {
				edges.add(e);
				isEndEdgeMet = true;
			}
			
			e = new Edge(r1.getId(), r3.getId(), matrix[r1.getId()][r3.getId()]);
			if (!r1.isEnd() && !r3.isEnd()) {
				edges.add(e);
			}
			else if (r1.isStart() || r2.isStart()) {
				// skip
			}
			else if (!isEndEdgeMet) {
				edges.add(e);
				isEndEdgeMet = true;
			}
		}
		return edges;
	}

	/**
	 * It is assumed that the rooms list is sorted in some fashion or the caller has a method to map the matrix indices back to a room object
	 * @param rooms
	 * @return
	 */
	protected static double[][] getDistanceMatrix(List<IRoom> rooms) {
		double[][] matrix = new double[rooms.size()][rooms.size()];

		for (int i = 0; i < rooms.size(); i++) {
			IRoom room = rooms.get(i);
			for (int j = 0; j < rooms.size(); j++) {
				IRoom node = rooms.get(j);
				if (room == node) {
					matrix[i][j] = 0.0;
				}
				else {
					if (matrix[i][j] == 0.0) {
						// calculate distance;
						double dist = room.getCenter().getDistance(node.getCenter());
						matrix[i][j] = dist;
						matrix[j][i] = dist;
					}
				}
			}
		}
		return matrix;
	}

	/**
	 * @param rand
	 * @param paths
	 * @param rooms
	 * @param config
	 */
	protected List<Wayline> calculateWaylines() {
		List<Wayline> resolvedWaylines = null;
		
		/*
		 * a list of a the waylines constructed from paths
		 */
		List<Wayline> waylines = new ArrayList<>();		

		for (Edge path : this.paths) {
			// get the rooms
			IRoom room1 = rooms.get(path.v);
			IRoom room2 = rooms.get(path.w);
//			logger.info(String.format("Connecting: [%d] with [%d]", room1.getId(), room2.getId()));
			
			// get the midpoint between room1 and room2
			ICoords midpoint = room1.getCenter().add(room2.getCenter());
			midpoint = new Coords(midpoint.getX()/2, midpoint.getY()/2, midpoint.getZ()/2);
//			midpoint.resetX(midpoint.getX()/2);
//			midpoint.resetY(midpoint.getY()/2);
//			midpoint.resetZ(midpoint.getZ()/2);
			
			/*
			 * a group of maps that relate the room's min/max values with the room
			 * NOTE if it is not necessary to record the ID of the room in the Waypoint, all the maps are unnecessary as well.
			 * UPDATE 8-5-2016 it IS necessary to record the ID of the room as the Y value of the room needs to be inspected
			 * when building the hallway
			 */
			Map<Integer, IRoom> minXMap = new HashMap<>(5);
			minXMap.put(new Integer(room1.getMinX()), room1);
			minXMap.put(new Integer(room2.getMinX()), room2);
			
			Map<Integer, IRoom> maxXMap = new HashMap<>(5);
			maxXMap.put(new Integer(room1.getMaxX()), room1);
			maxXMap.put(new Integer(room2.getMaxX()), room2);
			
			Map<Integer, IRoom> minZMap = new HashMap<>(5);
			minZMap.put(new Integer(room1.getMinZ()), room1);
			minZMap.put(new Integer(room2.getMinZ()), room2);
			
			Map<Integer, IRoom> maxZMap = new HashMap<>(5);
			maxZMap.put(new Integer(room1.getMaxZ()), room1);
			maxZMap.put(new Integer(room2.getMaxZ()), room2);			
			
			// get the min of the max  x -axis
			int innerMaxX = Math.min(room1.getMaxX(),  room2.getMaxX());
			// get the max of the min x-axis
			int innerMinX = Math.max(room1.getMinX(),  room2.getMinX());
			int innerMaxZ = Math.min(room1.getMaxZ(), room2.getMaxZ());
			int innerMinZ = Math.max(room1.getMinZ(), room2.getMinZ());

			/*
			 * a stack to contain all the waylines (and sub-waylines) that need to be checked for intersection
			 */
			Stack<Wayline> stack = new Stack<>();
			
//			logger.info(String.format("minX: %d, maxX: %d, minZ: %d, maxZ: %d", innerMinX, innerMaxX, innerMinZ, innerMaxZ));
			
			/*
			 * -------------------------------------------------------
			 *  test the horizontal "closeness"
			 *  -------------------------------------------------------
			 */
			
			/*
			 * if the rooms overlap each other on a single axis, they are "close" enough
			 */
			// horizontal wayline
			if ((room1.getMaxZ() < room2.getMaxZ() && room1.getMaxZ() > room2.getMinZ()) ||
					(room2.getMaxZ() < room1.getMaxZ() && room2.getMaxZ() > room1.getMinZ()) ||
					(room1.getMinZ() >= room2.getMinZ() && room1.getMaxZ() <= room2.getMaxZ()) ||
					(room2.getMinZ() >= room1.getMinZ() && room2.getMaxZ() <= room1.getMaxZ())) {
				int z = (innerMaxZ + innerMinZ)/2;
				
				// TODO need some sort of check that if rooms are close by 1 block, which creates a horz wayline that runs from
				// one room fine, but ends in a wall of the second room, which theoretically should be fine, as the door generator
				// should continue to remove wall until the air of the room is met ??
				
				Wayline wayline = new Wayline(new Waypoint(minXMap.get(innerMinX).getId(), innerMinX-1, 0, z),
						new Waypoint(maxXMap.get(innerMaxX).getId(), innerMaxX+1, 0, z), Alignment.HORIZONTAL);

//				logger.info(String.format("Horz line from [%d, %d] to [%d, %d[", innerMinX, z, innerMaxX, z));
				if (wayline.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
					logger.trace("Wayline's points are equal !!: " + wayline);
				}
				
				stack.add(wayline);
				// check if EMPTY_WAYLINES is return.
				resolvedWaylines = resolveWaylineRoomIntersections(this.rooms, stack);
				if (resolvedWaylines == EMPTY_WAYLINES) return resolvedWaylines;
				waylines.addAll(resolvedWaylines);
				continue;
			}
			// vertical wayline
			if ((room1.getMaxX() < room2.getMaxX() && room1.getMaxX() > room2.getMinX()) ||
					(room2.getMaxX() < room1.getMaxX() && room2.getMaxX() > room1.getMinX()) ||
					(room1.getMinX() >= room2.getMinX() && room1.getMaxX() <= room2.getMaxX()) ||
					(room2.getMinX() >= room1.getMinX() && room2.getMaxX() <= room1.getMaxX())) {
				int x = (innerMaxX + innerMinX)/2;
				Wayline wayline = new Wayline(new Waypoint(minZMap.get(innerMinZ).getId(), x, 0, innerMinZ-1),
						new Waypoint(maxZMap.get(innerMaxZ).getId(), x, 0, innerMaxZ+1), Alignment.VERTICAL);
//				logger.info(String.format("Vert line from [%d, %d] to [%d, %d[", x, innerMinZ, x, innerMaxZ));

				if (wayline.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
					logger.trace("Wayline's points are equal !!: " + wayline);
				}
				stack.add(wayline);
				waylines.addAll(resolveWaylineRoomIntersections(this.rooms, stack));
				continue;
			}	

			// TODO special case for <1 lenth rooms.... needs to be a 1 block which would just be the door.
			// or indicated something special
			
			/*
			 *  build L-shaped line
			 */
			ICoords r1c = room1.getCenter();
			Wayline wayline = null;
//			if (room2.getMinX() + 1 > room1.getMaxX() - 1) {
			if (room2.getCenter().getX() > room1.getCenter().getX()) {
				// room2 is to the right (positive-x) of room 1
				/*
				 * NOTE 8/31/2017 this comment only  concerning rooms where the Y-value is not the same - which
				 * has not been implemented yet.
				 * 
				 * this part is incorrect or just doesn't contain enough data.  this is a L-shaped wayline and therefor
				 * point2 doesn't connect to a room by default.  however it is assigned the room2's ID.
				 * this will cause incorrect calculations when attempting to generate hallsways as room2's Y value
				 * will be looked at and  not any intermediate intersecting room's Y (the hallway doesn't need to "climb back"
				 * up to room2's Y). needs to be flagged with a value to indicate that it is not terminal and therefor it can be
				 * generated like a corner. as well need a reference to the next piece of the wayline corner so no overlap of blocks
				 * is generated
				 */
				wayline = new Wayline(new Waypoint(room1.getId(), room1.getMaxX()+1, 0, r1c.getZ()), 
						new Waypoint(room2.getId(), room2.getCenter().getX(), 0, r1c.getZ(), false));
			}
			else {
				wayline = new Wayline(new Waypoint(room1.getId(), room1.getMinX()-1, 0, r1c.getZ()), 
						new Waypoint(room2.getId(), room2.getCenter().getX(), 0, r1c.getZ(), false));
			}
			
			if (wayline.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
				logger.warn("Wayline's points are equal !!: " + wayline);
			}
			stack.add(wayline);
			List<Wayline> segmented = resolveWaylineRoomIntersections(this.rooms, stack);
			waylines.addAll(segmented);
			// search the list for the non-terminated wayline
			Optional<Wayline> arm1 = segmented.stream()
				.filter(x -> !x.getPoint1().isTerminated() || !x.getPoint2().isTerminated()).findFirst();
						
			// room2 is down (postivie-z) of room 1
			if (room2.getCenter().getZ() > room1.getCenter().getZ()) {
				wayline = new Wayline(new Waypoint(room1.getId(), room2.getCenter().getX(), 0, r1c.getZ(), false),
						new Waypoint(room2.getId(), room2.getCenter().getX(), 0, room2.getMinZ()-1));
			}
			// room2 is up (negative-z) of room 1
			else {
				wayline = new Wayline(new Waypoint(room1.getId(), room2.getCenter().getX(), 0, r1c.getZ(), false),
						new Waypoint(room2.getId(), room2.getCenter().getX(), 0, room2.getMaxZ()+1));
			}
			
			if (wayline.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
				logger.warn("Wayline's points are equal !!: " + wayline);
			}
			
			stack.add(wayline);
			segmented = resolveWaylineRoomIntersections(this.rooms, stack);
			waylines.addAll(segmented);
			
			// search the list for the non-terminated wayline
			Optional<Wayline> arm2 = segmented.stream()
					.filter(x -> !x.getPoint1().isTerminated() || !x.getPoint2().isTerminated()).findFirst();
			
			// if both parts of L-shaped (elbow join) wayline are found, then set to reference each other
			if (arm1.isPresent() && arm2.isPresent() && arm1.get() != null && arm2.get() != null) {
				arm1.get().setWayline(arm2.get());
				arm2.get().setWayline(arm1.get());
			}
		}

		return waylines;
	}

	/**
	 * @param rooms
	 * @param stack
	 * @return 
	 */
	protected List<Wayline> resolveWaylineRoomIntersections(List<IRoom> rooms, Stack<Wayline> stack) {
		List<Wayline> waylines = new ArrayList<>();
		List<Wayline> result = new ArrayList<>();
		int failSafeLimit = rooms.size() * 3;
		int failSafeCount = 0;
		do {
			failSafeCount++;
			Wayline wayline = stack.pop();
			//=======
			for (IRoom room : rooms) {
//				logger.info(String.format("Checking against room [%d]", room.getId()));
				if (wayline == null) {
					logger.trace("Wayline is null on room:" + room.getId());
					break;
					}
				// construct a BB for line
				AxisAlignedBB bb1 = new AxisAlignedBB(
						wayline.getPoint1().getX(), 0, wayline.getPoint1().getZ(),
						wayline.getPoint2().getX(), 1, wayline.getPoint2().getZ());
				
				/*
				 * TODO NOTE encountered a special use case where a wayline appears to connect start room to end room.
				 * the wayline intersects both rooms and enters an endless loop. this needs to be solved.
				 * The wayline that are generated are 0-length and 1-length.  the zero length should be removed.
				 * Probably means that two rooms intersect on a wall and a wayline is build between them.
				 * In the meantime and for good measure in general, add a failsafe
				 */
				// test intersection with room
				if (wayline.getPoint1().getCoords().getDistance(wayline.getPoint2().getCoords()) > 0 && bb1.intersects(room.getXZBoundingBox())) {
					logger.trace(String.format("Room [%d] intersection with wayline %s", room.getId(), wayline));
//					wayline = resolveWaylineRoomIntersection(room, wayline, waylines);
					waylines = resolveWaylineRoomIntersection(room, wayline);
					if (waylines != null && waylines.size() > 0) {
//						logger.debug("Adding new waylines to stack.");
						stack.addAll(waylines);
						wayline = null;
					}
					break;
				}
//				else {
//					result.add(wayline);
//				}
			}
			if (wayline != null) result.add(wayline);
			
			//===========
			if (failSafeCount >= failSafeLimit) {
				return EMPTY_WAYLINES;
			}
		} while (!stack.isEmpty());
		return result;
	}

	/**
	 * @param room
	 * @param wayline
	 * @return
	 */
	protected List<Wayline> resolveWaylineRoomIntersection(IRoom room, Wayline wayline) {
		List<Wayline> waylines = new ArrayList<>();
		Wayline remainderWayline1 = null;
		Wayline remainderWayline2 = null;

		// determine if horizontal or vertical line
		boolean isHorz = false;
//		if (wayline.getPoint1().getZ() == wayline.getPoint2().getZ()) isHorz = true;
		isHorz = wayline.getAlignment() == Alignment.HORIZONTAL ? true : false;
		
		// create new waylines
		Waypoint p1 = wayline.getPoint1();
		Waypoint p2 = wayline.getPoint2();
		Waypoint terminatedPoint = null;
		if (isHorz) {

			// determine if a point terminates in room
			if (wayline.getPoint1().getX() >= room.getMinX() && wayline.getPoint1().getX() <= room.getMaxX()) {
				terminatedPoint = wayline.getPoint1();
				p1 = wayline.getPoint2();
				p2 = wayline.getPoint1();
			}
			else if (wayline.getPoint2().getX() >= room.getMinX() && wayline.getPoint2().getX() <= room.getMaxX()) {
				terminatedPoint = wayline.getPoint2();
				p1 = wayline.getPoint1();
				p2 = wayline.getPoint2();
			}
			else {
				// no termination - line right through
			}
			
			// left to right
			if (p1.getX() < p2.getX() /*&& wayline.getPoint1() != terminatedPoint*/) {
				remainderWayline1 = new Wayline(p1, new Waypoint(room.getId(), room.getMinX(), p1.getY(), p1.getZ()), Alignment.HORIZONTAL);
				if (terminatedPoint == null) {
					remainderWayline2 = new Wayline(new Waypoint(room.getId(), room.getMaxX()+1, p2.getY(), p2.getZ(), p2.isTerminated()), p2, Alignment.HORIZONTAL);
				}
			}
			// right to left
			else {				
				remainderWayline1 = new Wayline(p1, new Waypoint(room.getId(), room.getMaxX()+1, p1.getY(), p1.getZ()), Alignment.HORIZONTAL);
				if (terminatedPoint == null) {
					remainderWayline2 = new Wayline(new Waypoint(room.getId(), room.getMinX(), p2.getY(), p2.getZ(), p2.isTerminated()), p2, Alignment.HORIZONTAL);
				}
			}
		}
		// vertical
		else {
			
			// determine if a point terminates in room
			if (wayline.getPoint1().getZ() >= room.getMinZ() && wayline.getPoint1().getZ() <= room.getMaxZ()) {
				terminatedPoint = wayline.getPoint1();
				p1 = wayline.getPoint2();
				p2 = wayline.getPoint1();
			}
			else if (wayline.getPoint2().getZ() >= room.getMinZ() && wayline.getPoint2().getZ() <= room.getMaxZ()) {
				terminatedPoint = wayline.getPoint2();
				p1 = wayline.getPoint1();
				p2 = wayline.getPoint2();
			}
			else {
			}
			
			// up
			if (p1.getZ() > p2.getZ()) {
				remainderWayline1 = new Wayline(p1, new Waypoint(room.getId(), p1.getX(), p1.getY(), room.getMaxZ()+1), Alignment.VERTICAL);
				if (terminatedPoint == null)
					remainderWayline2 = new Wayline(new Waypoint(room.getId(), p2.getX(), p2.getY(), room.getMinZ(), p2.isTerminated()), p2, Alignment.VERTICAL);
			}
			// down
			else {
				remainderWayline1 = new Wayline(p1, new Waypoint(room.getId(), p1.getX(), p1.getY(), room.getMinZ()), Alignment.VERTICAL);
				if (terminatedPoint == null)
					remainderWayline2 = new Wayline(new Waypoint(room.getId(), p2.getX(), p2.getY(), room.getMaxZ()+1, p2.isTerminated()), p2, Alignment.VERTICAL);
			}		
		}
			
		// add the new wayline to the list
		if (remainderWayline1 != null) {
			if (remainderWayline1.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
				logger.trace("Remainder Wayline1's points are equal !!: " + remainderWayline1);
			}
			waylines.add(remainderWayline1);
		}
		
		if (remainderWayline2 != null) {
			if (remainderWayline2.getPoint1().getCoords().equals(wayline.getPoint2().getCoords())) {
				logger.trace("Remainder Wayline2's points are equal !!: " + remainderWayline2);
			}
			waylines.add(remainderWayline2);
		}
		return waylines;
	}

	/**
	 * 
	 * @param rooms
	 * @param wayline
	 * @return
	 */
	@Deprecated
	protected List<Wayline> resolveWaylineRoomIntersections(List<IRoom> rooms, Wayline wayline) {
		List<Wayline> waylines = new ArrayList<>();
		
		for (IRoom room : rooms) {
			logger.trace(String.format("Checking against room [%d]", room.getId()));
			if (wayline == null) {
				logger.debug("Wayline is null on room:" + room.getId());
				break;
				}
			// construct a BB for line
			AxisAlignedBB bb1 = new AxisAlignedBB(
					wayline.getPoint1().getX(), 0, wayline.getPoint1().getZ(),
					wayline.getPoint2().getX(), 1, wayline.getPoint2().getZ());
			
			// test intersection with room
			if (bb1.intersects(room.getBoundingBox())) {
				logger.trace(String.format("Room [%d] intersection with wayline %s", room.getId(), wayline));
				wayline = resolveWaylineRoomIntersection(room, wayline, waylines);
			}
		}
		if (wayline != null) waylines.add(wayline);	
		return waylines;
	}
	
	/**
	 * 
	 * @param room
	 * @param wayline
	 * @param waylines
	 * @return
	 */
	@Deprecated
	protected Wayline resolveWaylineRoomIntersection(IRoom room, Wayline wayline, List<Wayline> waylines) {
		Wayline newWayline = null;
		Wayline remainderWayline = null;
		
		// determine if horizontal or vertical line
		boolean isHorz = false;
		if (wayline.getPoint1().getZ() == wayline.getPoint2().getZ()) isHorz = true;

		
		// create new waylines
//		logger.info("WP1: " + wayline.getPoint1());
//		logger.info("WP2: " + wayline.getPoint2());
//		logger.info("Room: " + room);
		Waypoint p1 = wayline.getPoint1();
		Waypoint p2 = wayline.getPoint2();
		Waypoint terminatedPoint = null;
//		Waypoint startPoint = null;
		if (isHorz) {

			// determine if a point terminates in room
			if (wayline.getPoint1().getX() >= room.getMinX() && wayline.getPoint1().getX() <= room.getMaxX()) {
				terminatedPoint = wayline.getPoint1();
//				logger.info("Terminating H point:"  + terminatedPoint);
				p1 = wayline.getPoint2();
				p2 = wayline.getPoint1();
			}
			else if (wayline.getPoint2().getX() >= room.getMinX() && wayline.getPoint2().getX() <= room.getMaxX()) {
				terminatedPoint = wayline.getPoint2();
//				logger.info("Terminating H Else point:"  + terminatedPoint);
				p1 = wayline.getPoint1();
				p2 = wayline.getPoint2();
			}
			else {
//				logger.info("Skipped H termination point altogether somehow!");
				// no termination - line right through
			}
			
//			if (pgood.getX() > pbad.getX()) {
			if (p1.getX() < p2.getX() /*&& wayline.getPoint1() != terminatedPoint*/) {
				newWayline = new Wayline(p1, new Waypoint(room.getId(), room.getMinX(), p1.getY(), p1.getZ()));
//				logger.info("Building wayline (R) from P1 -> Room.MinX");
				if (terminatedPoint == null) {
					remainderWayline = new Wayline(new Waypoint(room.getId(), room.getMaxX(), p2.getY(), p2.getZ()), p2);
				}
			}
			else {				
				newWayline = new Wayline(p1, new Waypoint(room.getId(), room.getMaxX(), p1.getY(), p1.getZ()));
//				logger.info("Building wayline (L) from P1 -> Room.MaxX");
				if (terminatedPoint == null) {
					remainderWayline = new Wayline(new Waypoint(room.getId(), room.getMinX(), p2.getY(), p2.getZ()), p2);
				}
			}
		}
		// vertical
		else {
			
			// determine if a point terminates in room
			if (wayline.getPoint1().getZ() >= room.getMinZ() && wayline.getPoint1().getZ() <= room.getMaxZ()) {
				terminatedPoint = wayline.getPoint1();
//				logger.info("Terminating V point:"  + terminatedPoint);
				p1 = wayline.getPoint2();
				p2 = wayline.getPoint1();
			}
			else if (wayline.getPoint2().getZ() >= room.getMinZ() && wayline.getPoint2().getZ() <= room.getMaxZ()) {
				terminatedPoint = wayline.getPoint2();
//				logger.info("Terminating V Else point:"  + terminatedPoint);
				p1 = wayline.getPoint1();
				p2 = wayline.getPoint2();
			}
			else {
//				logger.info("Skipped V termination point altogether somehow!");
			}
			
			// up
			if (p1.getZ() > p2.getZ()) {
				newWayline = new Wayline(p1, new Waypoint(room.getId(), p1.getX(), p1.getY(), room.getMaxZ()));
				if (terminatedPoint == null)
					remainderWayline = new Wayline(new Waypoint(room.getId(), p2.getX(), p2.getY(), room.getMinZ()), p2);
			}
			// down
			else {
				newWayline = new Wayline(p1, new Waypoint(room.getId(), p1.getX(), p1.getY(), room.getMinZ()));
				if (terminatedPoint == null)
					remainderWayline = new Wayline(new Waypoint(room.getId(), p2.getX(), p2.getY(), room.getMaxZ()), p2);
			}		
		}
			
		// add the new wayline to the list
		waylines.add(newWayline);
		
		// return the remainer wayline
		return remainderWayline;
	}
	
	/**
	 * 
	 * @param m
	 */
	public static void printMatrix(double[][] m){
		try{
			int rows = m.length;
			int columns = m[0].length;
			String str = "|\t";

			for(int i=0;i<rows;i++){
				for(int j=0;j<columns;j++){
					str += ((int)m[i][j]) + "\t";
				}

				System.out.println(str + "|");
				str = "|\t";
			}

		}catch(Exception e){System.out.println("Matrix is empty!!");}
	}

	/**
	 * LevelVisualizer against all build criteria. Rooms that don't meet criteria are removed from the list.
	 * @param rand
	 * @param rooms
	 * @param config
	 * @return
	 */
	protected List<IRoom> selectValidRooms() {
		List<IRoom> met = new ArrayList<>();
		int roomId = 0;

		for (IRoom room : this.rooms) {
			logger.debug("Room coords -> {}", room.getCoords());
			if (room.isObstacle()) {
				continue;
			}

			// NOTE at this point it is assumed any anchors are pre-validated and meet all criteria
			if (room.isAnchor()) {
				room.setId(roomId++);
				met.add(room);
				continue;
			}

			boolean isValid = false;

			// check if the room is inside the level bounding box
			AxisAlignedBB lbb = getField();
			AxisAlignedBB rbb = room.getXZBoundingBox();
			if (rbb.minX > lbb.minX
					|| rbb.maxX < lbb.maxX) {
				isValid = true;
			}
			else {
				logger.debug("Removing room for being outside field bounds -> {}", room);
				System.out.println("Removing room for being outside field bounds -> " +  room);
				incrementLossToValidation(1);
			}
			
			// TODO move to method
			// check if the chunk is loaded
			if (isValid && getConfig().isMinecraftConstraintsOn()) {
				Chunk chunk = getWorld().getChunkFromBlockCoords(room.getCenter().toPos());
				if (chunk.isLoaded()) {
					isValid = true;
				}
				else {
					logger.debug("Removing room for residing in unloaded chunk -> {}", room);
					incrementLossToValidation(1);
				}
			}
			
			// check if room meets all criteria/constraints for generation (Mincraft world tests)
			if (isValid) {
				isValid = meetsRoomConstraints(room);
			}
			else {
				logger.debug("Removing room for failing constraints -> {}", room);
				incrementLossToValidation(1);
			}
			
			if (isValid) {
				// assign a new id to room
				room.setId(roomId++);
				// add room
				met.add(room);			
			}
		}
		return met;
	}

	/**
	 * Ensure the room meets are criteria to be built.
	 * @param world 
	 * @param room
	 * @return
	 */
	protected boolean meetsRoomConstraints(IRoom room) {
		if (room == null || room.isReject()) return false;
		if (!getConfig().isMinecraftConstraintsOn()) return true;
		
		// ensure that the room is above the bottom
		if (room.getCoords().getY() <= config.getYRange().getMinInt()) {
//			if (logger.isDebugEnabled()) {
//				logger.debug(
//					String.format("Room bottom [%d] is below min y constraint [%d]", room.getCoords().getY(), config.getYRange().getMinInt()));
//			}
			return false;
		}

		// ensure the room is below the y max threshold
		if (room.getCoords().getY() + room.getHeight() > config.getYRange().getMaxInt()) {
//			if (logger.isDebugEnabled()) {
//				logger.debug(
//					String.format("Room top [%d] is above max y constraint [%d]", (room.getCoords().getY() + room.getHeight()), config.getYRange().getMaxInt()));
//			}
			return false;
		}

		// get percentage of solid base blocks
		double percentSolid = WorldInfo.getSolidBasePercent(world, room.getCoords(), room.getWidth(), room.getDepth());
//		logger.debug("Percent solid base:" + percentSolid);
		
		// get the depth from the surface to top of the room
		int surfaceRoomDepth = WorldInfo.getDifferenceWithSurface(world, room.getCenter());
//		logger.debug("The surface/room depth =" + surfaceRoomDepth);
		if (surfaceRoomDepth == WorldInfo.INVALID_SURFACE_POS) {
//			logger.debug("Unable to locate the surface position.");
			return false;
		}
		
		// check if the top y valueof the node is above sea level
		if (room.getCoords().getY() + room.getHeight() > config.getSeaLevel()) {
//			logger.trace("Room is above sea level @ " + room.getCenter());
			/*
			 *  if surfaceRoomDepth is greater than a [x] negative amount.
			 *  negative implies the room is higher than the surface, ie the room is exposed.
			 */
			if (surfaceRoomDepth < -3) { // TODO make -3 a constant or a config value
//				logger.debug("Room rejected due to exposure @ " + room.getCenter());
				return false;
			}			
			else if (percentSolid < 50.0f) {
//				logger.debug("Room has less than 50 % base @ " + room.getCenter());
				/*
				 * there is less than 50% solid base
				 */
				return false;
			}
		}
		else {
//			logger.debug("Room is below sea level @ " + room.getCenter());
			if (percentSolid < 20.0f) {
//				logger.debug("Room has less than 20 % base @ " + room.getCenter());
				/*
				 * 0-20% = mostly likely suspended over a chasm/pit/ravine
				 */
				return false;
			}
			else if (percentSolid < 50.0f) {
//				logger.debug("Room has less than 50 % base @ " + room.getCenter());
				/*
				 * 21-40 = overrhanging a chasm/pit/ravine
				 */
				return false;
			}
		}		
		return true;
	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	protected IRoom buildStartRoom() {
//		return buildStartRoom(getStartPoint(), getConfig());
//	}
	
//	/**
//	 * Builds a room at the centered on the startPoint.
//	 * @param world
//	 * @param rand
//	 * @param startPoint
//	 * @param config
//	 * @return
//	 */
//	public IRoom buildStartRoom(ICoords startPoint, LevelConfig config) {
//		/*
//		 * the start of the level
//		 */
//		IRoom startRoom = new Room().setStart(true).setAnchor(true).setType(Type.LADDER);
//		startRoom = randomizeDimensions(getRandom(), startRoom, config);
//		// ensure min dimensions are met for start room
//		startRoom.setWidth(Math.max(7, startRoom.getWidth()));
//		startRoom.setDepth(Math.max(7,  startRoom.getDepth()));
//		// ensure that start room's dimensions are odd in length
//		if (startRoom.getWidth() % 2 == 0) startRoom.setWidth(startRoom.getWidth()+1);
//		if (startRoom.getDepth() % 2 == 0) startRoom.setDepth(startRoom.getDepth()+1);
//		
//		// set the starting room coords to be in the middle of the start point
//		startRoom.setCoords(
//				new Coords(startPoint.getX()-(startRoom.getWidth()/2),
//						startPoint.getY(),
//						startPoint.getZ()-(startRoom.getDepth()/2)));
//		//startRoom.setDistance(startRoom.getCoords().getDistanceSq(startPoint));
////		startRoom.setDistance(0.0);
//		// randomize a direction
//		startRoom.setDirection(Direction.getByCode(RandomHelper.randomInt(2, 5)));
//		// test if the room meets conditions to be placed in the minecraft world
//		if (config.isMinecraftConstraintsOn() && !meetsRoomConstraints(startRoom)) {
//			logger.debug("Start Room failed room constraints @ " + startRoom.getCenter());
//			if (logger.isWarnEnabled()) {
//				logger.warn(String.format("Start Room has invalid Minecraft world room conditions: %s", startRoom.toString()));
//			}
//			return EMPTY_ROOM;
//		}
//		return startRoom;
//	}
//	
//	/**
//	 * 
//	 * @param plannedRooms
//	 * @return
//	 */
//	public Room buildEndRoom(List<IRoom> plannedRooms) {
//		return buildEndRoom(getRandom(), getOrigin(), getField(), plannedRooms, getConfig());
//	}
//	
//	/**
//	 * 
//	 * @param random
//	 * @param origin
//	 * @param field
//	 * @param plannedRooms
//	 * @param config
//	 * @return
//	 */
//	public Room buildEndRoom(Random random, ICoords origin, AxisAlignedBB field, List<IRoom> plannedRooms, LevelConfig config) {
//		/*
//		 * the end room of the level.
//		 */
//
//		/*
//		 * change the distance that the end room can be from startpoint.
//		 * (this chance only affects the end room).
//		 */
//		double factor = 2.0;
//		LevelConfig c2 = new LevelConfig(getConfig());
//		Quantity qx = new Quantity(c2.getXDistance().getMin(), c2.getXDistance().getMax()*factor);
//		Quantity qz = new Quantity(c2.getZDistance().getMin(), c2.getZDistance().getMax()*factor);
//		c2.setXDistance(qx);
//		c2.setZDistance(qz);
//		
//		// build the end room
//		Room endRoom  = buildPlannedRoom(random, origin , field, plannedRooms, c2).setEnd(true).setAnchor(true).setType(Type.LADDER);
//		// ensure min dimensions are met for start room
//		endRoom.setWidth(Math.max(7, endRoom.getWidth()));
//		endRoom.setDepth(Math.max(7,  endRoom.getDepth()));
//		// ensure that the room's dimensions are odd in length
//		if (endRoom.getWidth() % 2 == 0) endRoom.setWidth(endRoom.getWidth()+1);
//		if (endRoom.getDepth() % 2 == 0) endRoom.setDepth(endRoom.getDepth()+1);
//		
//		return endRoom;
//	}
//
//	/**
//	 * 
//	 * @param random
//	 * @param origin
//	 * @param field
//	 * @param plannedRooms
//	 * @param config
//	 * @return
//	 */
//	public IRoom buildPlannedRoom(Random random, ICoords origin, AxisAlignedBB field, List<IRoom> plannedRooms, LevelConfig config) {
//		IRoom plannedRoom = new Room();
//		
//		/* 
//		 * check to make sure planned rooms don't intersect.
//		 * test up to 10 times for a successful position
//		 */
//		boolean checkRooms = true;
//		int endCheckIndex = 0;
//		checkingRooms:
//		do {
//			plannedRoom = randomizeRoom(random, origin, field, plannedRoom, config);
//			logger.debug("New Planned Room:" + plannedRoom);
//			endCheckIndex++;
//			if (endCheckIndex > 10) {
//				logger.warn("Unable to position Planned Room that meets positional criteria.");
//				return EMPTY_ROOM;
//			}
//			for (IRoom room : plannedRooms) {
//				if (room.getXZBoundingBox().intersects(plannedRoom.getXZBoundingBox())) {
//					logger.debug("New Planned room intersects with planned list room.");
//					continue checkingRooms;
//				}
//			}
//			// test if the room meets conditions to be placed in the minecraft world
//			if (!meetsRoomConstraints(plannedRoom)) {
//				break;
//			}			
//			checkRooms = false;			
//		} while (checkRooms);		
//		return plannedRoom;
//	}
//	
//	/**
//	 * 
//	 * @param world
//	 * @param rand
//	 * @param startPoint
//	 * @param config
//	 * @return
//	 */
//	protected IRoom buildPlannedRoom(List<IRoom> plannedRooms, LevelConfig config) {
//		return buildPlannedRoom(getRandom(), getOrigin(), getField(), plannedRooms, config);
//	}
	
//	/**
//	 * 
//	 * @param world
//	 * @param rand
//	 * @param startPoint
//	 * @param predefinedRooms
//	 * @param config
//	 * @return
//	 */
//	public IRoom buildBossRoom(World world, Random rand,
//			ICoords startPoint, List<IRoom> predefinedRooms, LevelConfig config) {
//		final int BOSS_ROOM_MIN_XZ = 10;
//		final int BOSS_ROOM_MIN_Y = 10;
//		
//		IRoom bossRoom = buildEndRoom(predefinedRooms).setType(Type.BOSS).setDegrees(1);	
//		// ensure min dimensions are met for start room
//		bossRoom.setWidth(Math.max(BOSS_ROOM_MIN_XZ, bossRoom.getWidth()));
//		bossRoom.setDepth(Math.max(BOSS_ROOM_MIN_XZ, bossRoom.getDepth()));
//		bossRoom.setHeight(Math.max(Math.min(BOSS_ROOM_MIN_Y, config.getHeight().getMaxInt()),  bossRoom.getHeight()));
//		return bossRoom;
//	}
	
	/**
	 * @param world
	 * @param rand
	 * @param surfaceCoords the coords of the surface position
	 * @param startRoom the level room that the entrance is connected to
	 * @param levelConfig
	 * @return
	 */
	@Deprecated
	protected IRoom buildEntranceRoom(World world, Random rand, ICoords surfaceCoords, Room startRoom,
			LevelConfig levelConfig) {
		
		Room entranceRoom = new Room(startRoom);
		entranceRoom.setAnchor(true).setType(Type.ENTRANCE);
		entranceRoom.setCoords(entranceRoom.getCoords().resetY(surfaceCoords.getY()));

		return null;
	}

	/**
	 * 	
	 * @param i
	 */
	public void incrementLossToDistanceBuffering(int i) {
		this.roomLossToDistanceBuffering += i;		
	}
	
	/**
	 * 
	 * @param i
	 */
	public void incrementLossToValidation(int i) {
		this.roomLossToValidation += i;
	}
	
	/**
	 * @return the config
	 */
	@Override
	public LevelConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	@Override
	public void setConfig(LevelConfig config) {
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LevelBuilder []";
	}

	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	private void setWorld(World world) {
		this.world = world;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	private void setRandom(Random random) {
		this.random = random;
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
	private void setStartPoint(ICoords startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return the plannedRooms
	 */
	public List<IRoom> getPlannedRooms() {
		return plannedRooms;
	}

	/**
	 * @param plannedRooms the plannedRooms to set
	 */
	private void setPlannedRooms(List<IRoom> plannedRooms) {
		this.plannedRooms = plannedRooms;
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
	private void setField(AxisAlignedBB field) {
		this.field = field;
	}

	/**
	 * @return the origin
	 */
	private ICoords getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	private void setOrigin(ICoords origin) {
		this.origin = origin;
	}

	/**
	 * @return the roomBuilder
	 */
	public IRoomBuilder getRoomBuilder() {
		return roomBuilder;
	}

	/**
	 * @param roomBuilder the roomBuilder to set
	 */
	public void setRoomBuilder(IRoomBuilder roomBuilder) {
		this.roomBuilder = roomBuilder;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<IRoom> getSpawned() {
		return spawned;
	}

	/**
	 * 
	 * @param spawned
	 */
	public void setSpawned(List<IRoom> spawned) {
		this.spawned = spawned;
	}
	
	
	public int getRoomLossToDistanceBuffering() {
		return roomLossToDistanceBuffering;
	}

	public void setRoomLossToDistanceBuffering(int roomLossToDistanceBuffering) {
		this.roomLossToDistanceBuffering = roomLossToDistanceBuffering;
	}

	public int getRoomLossToValidation() {
		return roomLossToValidation;
	}

	public void setRoomLossToValidation(int roomLossToValidation) {
		this.roomLossToValidation = roomLossToValidation;
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

	public List<Wayline> getWaylines() {
		return waylines;
	}

	public void setWaylines(List<Wayline> waylines) {
		this.waylines = waylines;
	}
}
