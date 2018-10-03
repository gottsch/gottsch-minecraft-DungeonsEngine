/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.builder.DungeonBuilder;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.generator.Arrangement;
import com.someguyssoftware.dungeonsengine.generator.Location;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.rotate.RotatorHelper;
import com.someguyssoftware.dungeonsengine.style.Frame;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IDecoratedRoom;
import com.someguyssoftware.dungeonsengine.style.Layout;
import com.someguyssoftware.dungeonsengine.style.Style;
import com.someguyssoftware.dungeonsengine.style.StyleSheet;
import com.someguyssoftware.dungeonsengine.style.Theme;
import com.someguyssoftware.gottschcore.block.CardinalDirectionFacadeBlock;
import com.someguyssoftware.gottschcore.enums.Direction;
import com.someguyssoftware.gottschcore.enums.Rotate;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

/**
 * @author Mark Gottschling on Aug 27, 2016
 *
 */
public interface IDungeonsBlockProvider {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");

	/**
	 * 
	 * @param random
	 * @param worldCoords
	 * @param room
	 * @param arrangement
	 * @param theme
	 * @param styleSheet
	 * @param config
	 * @return
	 */
	default public IBlockState getBlockState(Random random, ICoords worldCoords, IDecoratedRoom room, Arrangement arrangement,
			Theme theme, StyleSheet styleSheet, LevelConfig config)  {
		IBlockState blockState = null;
		IArchitecturalElement elem = arrangement.getElement();
		
		// apply decay if not air and style dicates
		int decayIndex = -1;
		if (elem != Elements.AIR && elem.getBase() != Elements.SURFACE_AIR) {
			// get the style for the element
			Style style = getStyle(elem, room.getLayout(), theme, styleSheet);
			// get the decayed index
			decayIndex = getDecayIndex(random, config.getDecayMultiplier(), style);
			// get calculated blockstate
			blockState = getBlockState(arrangement, style, decayIndex);

		}
		else {
			blockState = Blocks.AIR.getDefaultState();
		}
		return blockState;
	}
	
	/**
	 * 
	 * @param arrangement
	 * @param style
	 * @param decayIndex
	 * @return
	 */
	default public IBlockState getBlockState(Arrangement arrangement, Style style, int decayIndex) {
		String block = "";
		IBlockState blockState = Blocks.AIR.getDefaultState();
		
		if (style == Style.NO_STYLE) return blockState;
		// ensure the decayIndex is right size for selected style
		decayIndex = (decayIndex < style.getDecayBlocks().size()) ? decayIndex : style.getDecayBlocks().size()-1;

		// get the block according to style and decay index
		block = decayIndex > -1 ? style.getDecayBlocks().get(decayIndex) : style.getBlock();

		if (block == null || block == "") return blockState;
		
		// check for special case "null"
		if (block.equals(DungeonBuilder.NULL_BLOCK_NAME)) {
			return DungeonBuilder.NULL_BLOCK;
		}
		
		// get the block based on meta
		String[] blockAndMeta = block.split("@");
//		logger.debug("Style:" + style.getName());
//		logger.debug("Block:" + block);
//		logger.debug("blockAndMeta[0]:" + blockAndMeta[0]);
		int meta = 0;
		// TODO could add additional checks here to ensure the string only contains 1 @ or that the value after @ is numeric
		if (blockAndMeta.length > 1) meta = Integer.valueOf(blockAndMeta[1]);
		try {
			blockState = Block.getBlockFromName(blockAndMeta[0]).getStateFromMeta(meta);
		}
		catch(Exception e) {
			blockState = null;
		}
		
		if (blockState == null) {
			logger.warn(String.format("Unable to retrieve blockState; returning NULL_BLOCK:\n" +
					"Arrangement: %s\n" +
					"Style: %s\n" + 
					"Block: %s\n" +
					"blockAndMeta[0]: %s\n" +
					"Meta: %d", arrangement, style.getName(), block, blockAndMeta[0], meta));
			return DungeonBuilder.NULL_BLOCK;
		}

		// rotate block to the direction of Arrangement if rotatable type
		IArchitecturalElement elem = arrangement.getElement();
		// TODO FUTURE add property to Elements enum, ROTATABLE
		if (elem == Elements.CROWN || elem == Elements.TRIM ||
				elem == Elements.CORNICE || elem == Elements.PLINTH  ||
				elem == Elements.COLUMN || elem == Elements.CAPITAL || elem == Elements.BASE ||
				elem == Elements.PILASTER ||
				elem == Elements.LADDER ||
				elem == Elements.GUTTER) {
			
			 /*
			  *  NOTE gutter didn't get rotated 180 because it is not CardinalDirectionFacade
			  *   but just CardinalDirection.  Nor should it be rotated because it should face into the wall). 
			  */			
			
			/*
			 * Since RelativeDirection and CardinalDirection are built is in opposite direction of Minecraft vanilla blocks,
			 * they have to be rotated 180 degrees from what the direction originally is.
			 */
			Direction d = null;
			if (blockState.getBlock() instanceof CardinalDirectionFacadeBlock) {
				d = arrangement.getDirection().rotate(Rotate.ROTATE_180);
			}
			else {
				d = arrangement.getDirection();
			}
			// rotate the block to face the correct direction in the room
			blockState = RotatorHelper.rotateBlock(blockState, d);
		}
		
		return blockState;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public Arrangement getArrangement(ICoords coords, IDecoratedRoom room, Layout layout){
		// determine the design element of the block @ xyz represents: floor, wall, ceiling, crown, trim, cornice, base, pillar door, air, etc					
		IArchitecturalElement element = getArchitecturalElement(coords, room, layout);
		Location location = room.getLocation(coords);
		Direction direction = getDirection(coords, room, element, location);

		return new Arrangement(element, location, direction);
	}
	
	/**
	 * @param element
	 * @param location
	 * @return
	 */
	default public Direction getDirection(ICoords coords, IDecoratedRoom room, IArchitecturalElement element,Location location) {
		Direction direction = Direction.NORTH;
		
		// if ladder rotate to same direction as room
		if (element == Elements.LADDER) {
			switch(room.getRoom().getDirection()) {
			case NORTH:
				direction = Direction.SOUTH;
				break;
			case EAST:
				direction = Direction.WEST;
				break;
			case SOUTH:
				direction = Direction.NORTH;
				break;
			case WEST:
				direction = Direction.EAST;
				break;
			default:
				direction = room.getRoom().getDirection();
			}
		}
		// TEMP exterior elements need to face in the opposite direction as default interior elements
		else if (
//				element.getFace() == Face.EXTERIOR || //<-- probably only need this one
				element == Elements.PLINTH  ||
				element == Elements.COLUMN  ||
				element == Elements.CORNICE ||
				element == Elements.COLUMN ||
				element == Elements.CAPITAL ||
				element == Elements.BASE	) {
			if (coords.getX() == room.getRoom().getMinX()) direction = Direction.EAST;
			else if (coords.getX() == room.getRoom().getMaxX()) direction = Direction.WEST;
			else if (coords.getZ() == room.getRoom().getMinZ()) direction = Direction.SOUTH;
			else direction = Direction.NORTH;
		}
		else {
			/*
			 * using stairs as an example, the back (the tallest side) is the north side and faces north
			 * therefore if stairs are to be used as trim or cornice, then the direction needs to point in
			 * the same direction of the side. ie NORTH_SIDE = NORTH
			 */
			if (location == Location.SOUTH_SIDE) direction = Direction.SOUTH;
			else if (location == Location.WEST_SIDE) direction = Direction.WEST;
			else if (location == Location.EAST_SIDE) direction = Direction.EAST;
		}
		
		return direction;
	 }
	

	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public IArchitecturalElement getArchitecturalElement(ICoords coords, IDecoratedRoom room, Layout layout) {

		// check for floor
		if (isFloorElement(coords, room, layout)) {
			if (room.has(Elements.ElementsEnum.GUTTER) && isGutterElement(coords, room, layout)) {
				return Elements.GUTTER;
			}
			// check for grate
			if (room.has(Elements.ElementsEnum.GRATE) && isGrateElement(coords, room, layout)) {
				return Elements.GRATE;
			}
			return Elements.FLOOR;
		}
		
		// check for wall
		if (isWallElement(coords, room, layout)) {
			if (isFacadeSupport(coords, room, layout)) return Elements.FACADE_SUPPORT;
			if (room.has(Elements.ElementsEnum.WALL_BASE) && isWallBase(coords, room, layout)) return Elements.WALL_BASE;
			if (room.has(Elements.ElementsEnum.WALL_CAPITAL) && isWallCapital(coords, room, layout)) return Elements.WALL_CAPITAL;
			return Elements.WALL;
		}
		
		/*
		 *  Optional elements
		 */
		// check for pilaster. if a room has a pilaster and pillar, then pilaster is used
		if (room.has(Elements.ElementsEnum.PILASTER) && isPilasterElement(coords, room, layout)) {
			// determine if base, shaft or capital
			if (isBaseElement(coords, room, layout)) return Elements.PILASTER_BASE;
			else if (isCapitalElement(coords, room, layout)) return Elements.PILASTER_CAPITAL;
			else return Elements.PILASTER;
		}
		
		// pillar
		if (room.has(Elements.ElementsEnum.PILLAR) /*&& !room.hasPilaster()*/ && isPillarElement(coords, room, layout)) {
			if (isBaseElement(coords, room, layout)) return Elements.PILLAR_BASE;
			else if (isCapitalElement(coords, room, layout)) return Elements.PILLAR_CAPITAL;
			else return Elements.PILLAR;
		}
			
		// check for crown molding
		if (room.has(Elements.ElementsEnum.CROWN) && isCrownElement(coords, room, layout)) return Elements.CROWN;
		// check for trim
		if (room.has(Elements.ElementsEnum.TRIM)) {
//			logger.debug("Room has Trim.");
			// &&
			if (isTrimElement(coords, room, layout)) return Elements.TRIM;
//			logger.debug("Not a Trim element.");
		}
				
		/* 
		 * End of Optional elements
		 */
		
		// check for coffered ceiling
		if (room.has(Elements.ElementsEnum.COFFERED_CROSSBEAM, Elements.ElementsEnum.COFFERED_MIDBEAM) && isCofferedCrossbeamElement(coords, room, layout)) return Elements.COFFERED_CROSSBEAM;
		if (room.has(Elements.ElementsEnum.COFFERED_CROSSBEAM, Elements.ElementsEnum.COFFERED_MIDBEAM) && isCofferedMidbeamElement(coords, room, layout)) return Elements.COFFERED_MIDBEAM;
		
		// check for ceiling
		if(isCeilingElement(coords, room, layout)) return Elements.CEILING;
		
		// check if air is on a surface, like the floor, ceiling, walls
		if(isSurfaceAirElement(coords, room, layout)) {
			int x = coords.getX();
			int z = coords.getZ();
			
			if (coords.getY() == room.getRoom().getMinY() + 1) return Elements.FLOOR_AIR;
			// check for wall air before ceiling, so the corner edges are recorded as wall as opposed to ceiling
			if (
					((x == room.getMinX() + 1 || x == room.getMaxX()-1) && z > room.getMinZ() && z < room.getMaxZ()) ||
					((z == room.getMinZ()+1 || z == room.getMaxZ()-1) && x > room.getMinX() && x < room.getMaxX())
				) return Elements.WALL_AIR;
			if (coords.getY() == room.getMaxY() - 1) return Elements.CEILING_AIR;
		}		
		return Elements.AIR;
	}
	
	/**
	 * Return same value as crossbeam.
	 * TODO future - determine if midbeam properly.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isCofferedMidbeamElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		return isCofferedCrossbeamElement(coords, room, layout);
	}

	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isCofferedCrossbeamElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMaxY() - 1) return true;
		return false;
	}

	/**
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isSurfaceAirElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() > room.getRoom().getMinY() + 1 && coords.getY() < room.getRoom().getMaxY() - 1 &&
			coords.getX() > room.getRoom().getMinX() + 1 && coords.getX() < room.getRoom().getMaxX() -1 &&
			coords.getZ() > room.getRoom().getMinZ() + 1 && coords.getZ() < room.getRoom().getMaxZ() -1) return false;
		return true;
	 }

	/**
	 * 
	 * @param coords
	 * @param room
	 * @return
	 */
	default public Location getLocation(ICoords coords, IDecoratedRoom room) {
		return room.getLocation(coords);
	}
	
	/**
	 * 
	 * @return
	 */
	public StyleSheet getDefaultStyleSheet();
	
	/**
	 * 
	 * @param elem
	 * @param layout
	 * @param theme
	 * @param styleSheet
	 * @return
	 */
	default public Style getStyle(IArchitecturalElement elem, Layout layout, Theme theme, StyleSheet styleSheet) {
		Style style = Style.NO_STYLE;

		// ensure that a layout is used
		if (layout == null) {
			layout = getDefaultStyleSheet().getLayouts().get(Layout.DEFAULT_NAME);
		}
		
		/*
		 *  any of the following elements should return without a style
		 *  NOTE FACADE_SUPPORT should not be included in this list
		 *  as it is a listed design element and returns a WALL!
		 */
		if (elem == null || elem == Elements.NONE ||
				elem == Elements.AIR || elem.getBase() == Elements.SURFACE_AIR ||
				elem == Elements.FACADE/* || elem == Elements.FACADE_SUPPORT*/)
			return Style.NO_STYLE;

		// TODO this will be part of the style sheet
		// get the frame from the layout
		Frame frame = layout.getFrames().get(Elements.ElementsEnum.getByValue(elem.getName()).name());
		// recursively check if layout has a ref and check if it contains the frame (up to 5 nested refs)
		if (frame == null) {
			List<String> layoutRefs = new ArrayList<>(5);
			while (frame == null && layout != null && layout.getRef() != null && layout.getRef().length() > 0) {
				if (layoutRefs.contains(layout.getRef())) {
					logger.warn(String.format("Stylesheet layout circular dependency: %s. Using defaults.", layout.getRef()));
					break;
				}
				else if (layoutRefs.size() == 5) {
					logger.warn("Too many Stylesheet layout references (5 max allowed. Using defaults.");
					break;
				}
				// update the layout refs
				layoutRefs.add(layout.getRef());
				// get the new referred layout
				layout = styleSheet.getLayouts().get(layout.getRef());
				if (layout != null) {
					frame = layout.getFrames().get(Elements.ElementsEnum.getByValue(elem.getName()).name());
				}
			}
		}
		
		// if frame is still null then use the default
		if (frame == null) {
//			logger.debug("Getting element from default stylesheet:" + elem.getName());
			frame = getDefaultStyleSheet().getLayouts().get(Layout.DEFAULT_NAME).getFrames().get(Elements.ElementsEnum.getByValue(elem.getName()).name());
		}
		
		// TODO throw custom error UNKNOWN IArchitecturalElement
		if (frame == null) {
			return Style.NO_STYLE;
		}
		
		// get the style by the alias or style NAME
		if (theme != null && frame.getAlias() != null && frame.getAlias().length() > 0) {
//			logger.debug(String.format("theme: %s\nAlias:%s", theme.getName(), frame.getAlias()));
			// TODO need to catch error here is style == null or theme.getAliases == null or the alias from the theme doesn't exist
			style = styleSheet.getStyles().get(theme.getAliases().get(frame.getAlias()).getStyle());
			if (style == null) {
				logger.warn(String.format("Unable to locate style based on theme [%s] and frame [%s]'s alias [%s]", theme.getName(), Elements.ElementsEnum.getByValue(elem.getName()).name(), frame.getAlias()));
			}
		}
		else if (frame.getStyle() != null && frame.getStyle().length() > 0) {
//			logger.debug("frame.getStyle:" + frame.getStyle());
			style = styleSheet.getStyles().get(frame.getStyle());
//			logger.debug("style=" + style);
			// get the style from the default stylesheet
			if (style == null) {
				style = getDefaultStyleSheet().getStyles().get(frame.getStyle());
			}
		}

		if (style == null) return Style.NO_STYLE;
		return style;
	}
	
	/**
	 * @param decayMultiplier
	 * @param style
	 * @return
	 */
	default public int getDecayIndex(Random random, int decayMultiplier, Style style) {
		if (decayMultiplier <= 0) return -1;
		
		int decayIndex = -1;
		for (int i = 0; i < decayMultiplier; i++) {
			if (random.nextDouble() * 100 < style.getDecay()) {
				decayIndex++;
			}
		}
		return decayIndex;
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
	default public boolean isFloorElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMinY()) return true;
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
	default public boolean isWallElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getX() == room.getRoom().getMinX() || coords.getZ() == room.getRoom().getMinZ() ||
				coords.getX() == room.getRoom().getMaxX() || coords.getZ() == room.getRoom().getMaxZ()) return true;
		return false;
	}
	
	/**
	 * Assumes isWallElement() has already been met.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isWallCapital(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMaxY()-1) return true;
		return false;
	}

	/**
	 * Assumes isWallElement() has already been met.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isWallBase(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMinY()+1) return true;
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
	default public boolean isCeilingElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getMaxY()) return true;
		return false;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isCrownElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		
		if (coords.getY() == room.getMaxY()-1 &&
				room.getHeight() > 4 && 
				(
					coords.getX() == room.getMinX()+1 ||
					coords.getX() == room.getMaxX()-1 ||
					coords.getZ() == room.getMinZ()+1 ||
					coords.getZ() == room.getMaxZ()-1)
				) return true;
		return false;
	}
	
	/**
	 * Checks for support block for crown and cornice.
	 * It is assumed that the element at x,y,z has already been determined to be a wall. This method simply checks
	 * the vertical position to determine if this element is the 1 less than the ceiling. No additional checks are made to ensure
	 * that is position is indeed a wall.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isFacadeSupport(ICoords coords, IDecoratedRoom room, Layout layout) {
		// check if y is 1 less than top (ceiling)
		if (coords.getY() == room.getMaxY() - 1) return true;
		return false;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isTrimElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (/*layout.getFrames().get(Elements.TRIM.name()) != null &&*/
				/*
				 * room has to be at least 7x7 and 4 high to give enough space to walk around
				 */
				room.getWidth() >=7 &&
				room.getDepth() >=7 &&
				room.getHeight() > 4 &&
				coords.getY() == room.getMinY()+1 &&				
				(
					coords.getX() == room.getMinX()+1 ||
					coords.getX() == room.getMaxX()-1 ||
					coords.getZ() == room.getMinZ()+1 ||
					coords.getZ() == room.getMaxZ()-1)
				) return true;
		return false;
	}
	
	/**
	 * Default isLadder method is a ladder that in going down from the floor as it is assumed dungeons go down.
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isLadderElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();		
		ICoords center = room.getCenter();
		Direction direction = room.getDirection();
		
		// short-circuit if above floor
		if (y > room.getMinY()) return false;
		
		// want the ladder on the opposite side the room is facing ie if room faces north, want the ladder on the south side of pillar (so that the ladder still faces north)
		switch(direction) {		
		case NORTH:
			if (x == center.getX() && z == (center.getZ()+1) ) return true;
			break;
		case EAST:
			if (x == (center.getX()-1) && z == center.getZ() ) return true;
			break;
		case SOUTH:
			if (x == center.getX() && z == (center.getZ()-1) ) return true;
			break;
		case WEST:
			if (x == (center.getX()+1) && z == center.getZ() ) return true;
			break;
		default:			
		}
		return false;
	}

	//  pillar
	default public boolean isPillarElement(ICoords coords, IDecoratedRoom decoratedRoom, Layout layout) {
		int x = coords.getX();
		int y = coords.getY();
		int z = coords.getZ();
		IRoom room = decoratedRoom.getRoom();
		
		if (!decoratedRoom.has(Elements.ElementsEnum.PILLAR) || Math.min(room.getWidth(), room.getDepth()) < 7 || y == room.getMaxY()) return false;

		// get the x,z indexes
		int xIndex = x - room.getCoords().getX();
		int zIndex = z - room.getCoords().getZ();
		int offset = 1;
		int remainder = 0;
		
		// if the room also has pilasters, then the offset is increased so there is still space between pillar and pilaster
		if (decoratedRoom.has(Elements.ElementsEnum.PILASTER)) {
			offset+=2;
			remainder = 1;
		}
		
		if (((xIndex > offset && xIndex < room.getWidth() - offset) && Math.abs(xIndex % 3) == remainder
				&& zIndex > offset && zIndex < room.getDepth() - offset && Math.abs(zIndex % 3) == remainder)) return true;
		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isPilasterElement(ICoords coords, IDecoratedRoom decoratedRoom, Layout layout) {
		int x = coords.getX();
		int z = coords.getZ();
		IRoom room = decoratedRoom.getRoom();
		// if on the floor level (min y)
		if ((room.getWidth() <= 5 || room.getDepth() <= 5) || coords.getY() == room.getMinY() || coords.getY() == room.getMaxY()) return false;
		
		// get the x,z indexes
		int xIndex = x - room.getCoords().getX();
		int zIndex = z - room.getCoords().getZ();

		// even x-z axis index (only in the corners
		if (room.getWidth() % 2 == 0 || room.getDepth() % 2 == 0) {
			if ((x == room.getMinX()+1 || x == room.getMaxX()-1) && (z == room.getMinZ()+1 || z == room.getMaxZ()-1)) return true;
			return false;
		}

		// odd x-z axis index
		if (((x == room.getMinX()+1 || x == room.getMaxX()-1) && Math.abs(zIndex % 2) == 1 && zIndex > 0 && zIndex < room.getDepth() - 1) ||
				((z == room.getMinZ()+1 || z == room.getMaxZ()-1) && Math.abs(xIndex % 2) == 1 && xIndex > 0 && xIndex < room.getWidth() - 1)) return true;

		return false;
	}
	
	/**
	 * 
	 * @param coords
	 * @param room
	 * @param layout
	 * @return
	 */
	default public boolean isGutterElement(ICoords coords, IDecoratedRoom decoratedRoom, Layout layout) {
		int x = coords.getX();
		int z = coords.getZ();
		IRoom room = decoratedRoom.getRoom();
		// if on the floor level (min y)
		if (coords.getY() == room.getMinY() &&
				// if on the inner edge of the wall and not the corners (gutter block isn't setup yet for corners)
				(((x == room.getMinX()+1 || x == room.getMaxX()-1) && z > room.getMinZ()+1 && z < room.getMaxZ()-1) ||
				((z == room.getMinZ()+1 || z == room.getMaxZ()-1) && x > room.getMinX()+1 && x < room.getMaxX()-1))
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
	default public boolean isGrateElement(ICoords coords, IDecoratedRoom decoratedRoom, Layout layout) {
		int x = coords.getX();
		int z = coords.getZ();
		IRoom room = decoratedRoom.getRoom();
		
		// if on the floor level (min y)
		if (coords.getY() != room.getMinY()) return false;
		
		if (decoratedRoom.has(Elements.ElementsEnum.GUTTER)) {
			// populate the four corners with a grate
			if ((x == room.getMinX()+1 || x == room.getMaxX()-1) && (z == room.getMinZ()+1 || z == room.getMaxZ()-1)) return true;
		}
		else {
			ICoords center = room.getCenter();
			// populate the center with a grate
			if (coords.getX() == center.getX() && coords.getZ() == center.getZ()) return true;
		}
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
	default public boolean isBaseElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMinY() + 1) return true;
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
	default public boolean isCapitalElement(ICoords coords, IDecoratedRoom room, Layout layout) {
		if (coords.getY() == room.getRoom().getMaxY() - 1) return true;
		return false;
	}
}
