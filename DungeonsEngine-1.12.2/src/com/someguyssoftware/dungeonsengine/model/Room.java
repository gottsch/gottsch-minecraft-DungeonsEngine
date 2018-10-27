package com.someguyssoftware.dungeonsengine.model;

import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.RandomHelper;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

/**
 * 
 * @author Mark Gottschling on Jul 9, 2016
 *
 */
public class Room implements IRoom {
	
	private int id;
	private String name;
	// TODO remove - instead create concrete classes
	private Type type;
	private ICoords coords;

	private int depth;
	private int width;
	private int height;
	
	private Direction direction;

	// TODO not sure what this is for yet... could be a transient property
	// NOTE this is only used in the comparator to sort once before applyDistanceBuffering
	// TODO remove this from Room and create a new Comparator that takes startPoint in as a constructor param
	// then the compare() method calcs the distance of each room to the start point
//	private double distance;
	
	private boolean anchor;
	private boolean obstacle;
	private boolean reject;
	private boolean start;
	private boolean end;

	// graphing
	private int degrees;
	
	private List<IDoor> doors;
	
	/**
	 * 
	 */
	public Room() {
		setId(RandomHelper.randomInt(-5000, 5000));
		coords = new Coords(0,0,0);
		setType(Type.GENERAL);		
		setDirection(Direction.SOUTH); // South
	}

//	public String printDimensions() {
//		return String.format("Dimensions -> [w: %d, h: %d, d: %d]", getWidth(), getHeight(), getDepth());
//	}
//	
//	public String printCoords() {
//		return String.format("Coords -> [x: %d, y: %d, z: %d]", getCoords().getX(), getCoords().getY(), getCoords().getZ());
//	}
//	
//	public String printCenter() {
//		return String.format("Center -> [x: %d, y: %d, z: %d]", getCenter().getX(), getCenter().getY(), getCenter().getZ());		
//	}
	
	
	/**
	 * 
	 * @param id
	 */
	public Room(int id) {
		this();
		setId(id);
	}
	
	/**
	 * 
	 * @param NAME
	 */
	public Room(String name) {
		this();
		setName(name);
	}
	
//	/**
//	 * initialize
//	 */
//	private void init() {
//		// generate (pseudo) unique id
//		//setId(new Random().nextInt(5000));
//		setId(WorldUtil.randomInt(-5000, 5000));
//		coords = new Coords(0,0,0);		
//	}
	
	/**
	 * 
	 * @param room
	 */
	public Room(IRoom room) {
		if (room != null) {
			setId(room.getId());
			setAnchor(room.isAnchor());
//			setCenter(new Coords(room.getCenter()));
			setCoords(new Coords(room.getCoords()));
			setDepth(room.getDepth());
//			setDistance(room.getDistance());
			setHeight(room.getHeight());
//			setQuad(room.getQuad());
			setWidth(room.getWidth());		
			setStart(room.isStart());
			setEnd(room.isEnd());
			setReject(room.isReject());
			setType(room.getType());
			setDirection(room.getDirection());
			setDegrees(room.getDegrees());
			setName(room.getName());
			setObstacle(room.isObstacle());
			
			// Room and Door ref each other, so make sure to copy without cyclical looping
			for (IDoor door : room.getDoors()) {
				Door d = new Door();
				d.setCoords(door.getCoords());
				d.setRoom(this);
				this.getDoors().add(d);
			}
		}
	}
	
	/**
	 * Copy Constructor
	 * @return
	 */
	@Override
	public IRoom copy() {
		return new Room(this);
	}
	
//	// TODO this is probably wrong needs to -1 ??
//	/* (non-Javadoc)
//	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getBoundingBox()
//	 */
//	@Override
//	public AxisAlignedBB getBoundingBox() {
//		BlockPos bp1 = getCoords().toPos();
//		BlockPos bp2 = getCoords().add(getWidth(), getHeight(), getDepth()).toPos();
//		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
//		return bb;
//	}
	
//	/**
//	 * Creates a bounding box by the XZ dimensions with a height (Y) of 1
//	 * @return
//	 */
//	public AxisAlignedBB getXZBoundingBox() {
//		BlockPos bp1 = new BlockPos(getCoords().getX(), 0, getCoords().getZ());
//		BlockPos bp2 = getCoords().add(getWidth(), 1, getDepth()).toPos();
//		AxisAlignedBB bb = new AxisAlignedBB(bp1, bp2);
//		return bb;
//	}
	
	// TODO determine if this can be removed - not a function of Room, but of Direction
    /**
     * Get a Facing by it's horizontal index (0-3). The order is S-W-N-E.
     */
    public static EnumFacing getHorizontal(int direction) {
        return EnumFacing.HORIZONTALS[MathHelper.abs(direction % EnumFacing.HORIZONTALS.length)];
    }

	
//	/**
//	 * Returns a new Room with the force applied at the angle on the XZ plane.
//	 * @param angle
//	 * @param force
//	 * @return
//	 */
//	public IRoom addXZForce(double angle, double force) {
//		double xForce = Math.sin(angle) * force;
//        double zForce = Math.cos(angle) * force;
//        
////        Room room = new Room(this);
//        IRoom room = copy();
//        room.setCoords(room.getCoords().add((int)xForce, 0, (int)zForce));
//        return room;
//	}
	
//	/* (non-Javadoc)
//	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getCenter()
//	 */
//	@Override
//	public ICoords getCenter() {
//		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2) ;
//		int y = this.getCoords().getY()  + ((this.getHeight()-1) / 2);
//		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
//		ICoords coords = new Coords(x, y, z);
//		return coords;
//	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	public ICoords getXZCenter() {
//		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
//		int y = this.getCoords().getY();
//		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
//		ICoords coords = new Coords(x, y, z);
//		return coords;
//	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	public ICoords getTopCenter() {
//		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
//		int y = this.getCoords().getY() + this.getHeight();
//		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
//		ICoords coords = new Coords(x, y, z);
//		return coords;	
//	}
	
//	/**
//	 * 
//	 * @param room
//	 * @return
//	 */
//	public Intersect getIntersect(IRoom room) {
//		return Intersect.getIntersect(this.getBoundingBox(), room.getBoundingBox());
//	}
//	
//	/**
//	 * Returns a new Room with the force applied at the angle on the XZ plane.
//	 * @param angle
//	 * @param force
//	 * @return
//	 */
//	public Room addXZForce(double angle, double force) {
//		double xForce = Math.sin(angle) * force;
//        double zForce = Math.cos(angle) * force;
//        
//        Room room = new Room(this);
//        room.setCoords(room.getCoords().add((int)xForce, 0, (int)zForce));
//        return room;
//	}
	
//	/**
//	 * Comparator to sort by Id
//	 */
//	public static Comparator<Room> idComparator = new Comparator<Room>() {
//		@Override
//		public int compare(Room p1, Room p2) {
//			if (p1.getId() > p2.getId()) {
//				// greater than
//				return 1;
//			}
//			else {
//				// less than
//				return -1;
//			}
//		}
//	};
	
//	/**
//	 * Comparator to sort plans by set weight
//	 */
//	public static Comparator<Room> distanceComparator = new Comparator<Room>() {
//		@Override
//		public int compare(Room p1, Room p2) {
//			if (p1.getDistance() > p2.getDistance()) {
//				// greater than
//				return 1;
//			}
//			else {
//				// less than
//				return -1;
//			}
//		}
//	};
		
	/**
	 * @return the NAME
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param NAME the NAME to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getCoords()
	 */
	@Override
	public ICoords getCoords() {
		return coords;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#setCoords(com.someguyssoftware.gottschcore.positional.ICoords)
	 */
	@Override
	public IRoom setCoords(ICoords coords) {
		this.coords = coords;
		return this;
	}

	/**
	 * @return the center
	 */
//	public ICoords getCenter() {
//		return center;
//	}

	/**
	 * @param center the center to set
	 */
//	public void setCenter(ICoords center) {
//		this.center = center;
//	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getDepth()
	 */
	@Override
	public int getDepth() {
		return depth;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#setDepth(int)
	 */
	@Override
	public IRoom setDepth(int depth) {
		this.depth = depth;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getWidth()
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#setWidth(int)
	 */
	@Override
	public IRoom setWidth(int width) {
		this.width = width;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getHeight()
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#setHeight(int)
	 */
	@Override
	public Room setHeight(int height) {
		this.height = height;
		return this;
	}

	/**
	 * @return the anchor
	 */
	@Override
	public boolean isAnchor() {
		return anchor;
	}

	/**
	 * @param anchor the anchor to set
	 */
	@Override
	public Room setAnchor(boolean anchor) {
		this.anchor = anchor;
		return this;
	}

//	/**
//	 * @return the distance
//	 */
//	public double getDistance() {
//		return distance;
//	}
//
//	/**
//	 * @param distance the distance to set
//	 */
//	public IRoom setDistance(double distance) {
//		this.distance = distance;
//		return this;
//	}

	/**
	 * @return the id
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the reject
	 */
	@Override
	public boolean isReject() {
		return reject;
	}

	/**
	 * @param reject the reject to set
	 */
	@Override
	public void setReject(boolean reject) {
		this.reject = reject;
	}
	
	// TODO move to own file or part of IRoom
	public enum Type {
		GENERAL("general"),
		LADDER("ladder"),
		ENTRANCE("entrance"),
		EXIT("exit"),
		TREASURE("treasure"),
		BOSS("boss"),
		HALLWAY("hallway");

		private String name;
		
		/**
		 * @param arg0
		 * @param arg1
		 */
		Type(String name) {
			this.name = name;
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
	}

	/**
	 * @return the start
	 */
	@Override
	public boolean isStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	@Override
	public Room setStart(boolean start) {
		this.start = start;
		return this;
	}

	/**
	 * @return the end
	 */
	@Override
	public boolean isEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	@Override
	public Room setEnd(boolean end) {
		this.end = end;
		return this;
	}

	/**
	 * @return the type
	 */
	@Override
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	@Override
	public Room setType(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * @return the degrees
	 */
	@Override
	public int getDegrees() {
		return degrees;
	}

	/**
	 * @param degrees the degrees to set
	 */
	@Override
	public Room setDegrees(int degrees) {
		this.degrees = degrees;
		return this;
	}

	/**
	 * @return the obstacle
	 */
	@Override
	public boolean isObstacle() {
		return obstacle;
	}

	/**
	 * @param obstacle the obstacle to set
	 */
	@Override
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getDirection()
	 */
	@Override
	public Direction getDirection() {
		return direction;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#setDirection(com.someguyssoftware.gottschcore.enums.Direction)
	 */
	@Override
	public Room setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public List<IDoor> getDoors() {
		if (doors == null) doors = new ArrayList<>(5);
		return doors;
	}

	/**
	 * 
	 * @param doors
	 */
	@Override
	public void setDoors(List<IDoor> doors) {
		this.doors = doors;
	}

//	/* (non-Javadoc)
//	 * @see com.someguyssoftware.dungeonsengine.model.IRoom#getBottomCenter()
//	 */
//	@Override
//	public ICoords getBottomCenter() {
//		int x = this.getCoords().getX()  + ((this.getWidth()-1) / 2);
//		int y = this.getCoords().getY();
//		int z = this.getCoords().getZ()  + ((this.getDepth()-1) / 2);
//		ICoords coords = new Coords(x, y, z);
//		return coords;	
//	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + ", type=" + type + ", coords=" + coords + ", depth=" + depth + ", width=" + width + ", height=" + height + ", direction=" + direction
				+ ", anchor=" + anchor + ", obstacle=" + obstacle + ", reject=" + reject + ", start=" + start + ", end=" + end + ", degrees=" + degrees + ", doors=" + doors
				+ "]";
	}
}
