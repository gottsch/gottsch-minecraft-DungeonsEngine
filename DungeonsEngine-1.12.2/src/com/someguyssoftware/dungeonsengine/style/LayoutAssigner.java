/**
 * 
 */
package com.someguyssoftware.dungeonsengine.style;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.someguyssoftware.dungeonsengine.generator.DungeonGenerator;
import com.someguyssoftware.dungeonsengine.model.Elements;
import com.someguyssoftware.dungeonsengine.model.IHallway;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.dungeonsengine.model.Room.Type;

/**
 * @author Mark Gottschling on Aug 31, 2016
 *
 */
public class LayoutAssigner {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private StyleSheet styleSheet;
	private Multimap<String, Layout> map;

	/**
	 * 
	 * @param styleSheet
	 */
	public LayoutAssigner(StyleSheet styleSheet) {
		map = ArrayListMultimap.create();
		this.styleSheet = styleSheet;
		
		// organize style sheet layouts based on category
		loadStyleSheet(styleSheet);
	}
	
	/**
	 * 
	 * @param styleSheet
	 */
	public void loadStyleSheet(StyleSheet styleSheet) {
		// clear the map
		map.clear();
		// for each layout remap by category into multi map
		for (Entry<String, Layout> e : styleSheet.getLayouts().entrySet()) {
			Layout layout = e.getValue();
			if (layout.getCategory() != null && !layout.getCategory().equals("")) {
				map.put(layout.getCategory().toLowerCase(), layout);
			}
			else {
				map.put(Layout.Type.ROOM.getName().toLowerCase(), layout);
			}
		}
	}
	
	/**
	 * 
	 * @param random
	 * @param room
	 * @param sheet
	 * @param layoutName
	 * @return
	 */
	public Layout assign(Random random, IDecoratedRoom room, String layoutName) {
		Layout layout = getStyleSheet().getLayouts().get(layoutName);
		
		if (layout == null) {
			logger.debug("Getting default layout from default stylesheet.");
			layout = DungeonGenerator.getDefaultStyleSheet().getLayouts().get(Layout.DEFAULT_NAME);
		}
		
		// assign the room
		assignLayoutToRoom(random, layout, room);
		
		return layout;
	}
	
	/**
	 * TODO change all Layout.Type to Room.Type and get rid of Layout.Type
	 * @param styleSheet
	 * @param room
	 * @return
	 */
	public Layout assign(Random random, IDecoratedRoom room) {
		List<Layout> layouts = null;
		Layout layout = null;

		// determine the category of layouts to grab based on the room
		if (room.getType() == Type.GENERAL) {
			layouts = (List<Layout>) map.get(Layout.Type.ROOM.getName().toLowerCase());
		}
		else if (room.isStart() && room.getType() != Room.Type.ENTRANCE) {
			layouts = (List<Layout>) map.get(Layout.Type.START.getName().toLowerCase());
		}
		else if (room.isEnd() && room.getType() != Room.Type.BOSS) {
			layouts = (List<Layout>) map.get(Layout.Type.END.getName().toLowerCase());
		}
		else if (room.getType() == Room.Type.HALLWAY || room instanceof IHallway) {
			layouts = (List<Layout>) map.get(Layout.Type.HALLWAY.getName().toLowerCase());
			layout = layouts.get(random.nextInt(layouts.size()));
//			logger.debug("Assigning Hallway layout:" + layout.getName());
			room.setLayout(layout);
			return layout;
		}
		else if (room.getType() == Type.ENTRANCE) {
			layouts = (List<Layout>) map.get(Layout.Type.ENTRANCE.getName().toLowerCase());
		}
		else if (room.getType() == Type.TREASURE) {
			layouts = (List<Layout>) map.get(Layout.Type.TREASURE.getName().toLowerCase());
		}
		
		else if (room.getType() == Type.BOSS) {
			layouts = (List<Layout>) map.get(Layout.Type.BOSS.getName().toLowerCase());
		}
		else {
			layouts = (List<Layout>) map.get(Layout.Type.ROOM.getName().toLowerCase());
		}
		
		// select a random layout from the category or use default layout if none found
		if (layouts != null && layouts.size() > 0) {
			layout = layouts.get(random.nextInt(layouts.size()));
		}
		else {
			layout = DungeonGenerator.getDefaultStyleSheet().getLayouts().get(Layout.DEFAULT_NAME);
		}
		
		// assign the room
		assignLayoutToRoom(random, layout, room);
		
		return layout;
	}
	
	/**
	 * @param random
	 * @param layout
	 * @param room
	 */
	private void assignLayoutToRoom(Random random, Layout layout, IDecoratedRoom room) {
		// if useAll is set to true, then if the frame exists, activate the element in the room
		if (layout.isUseAll()) {
			logger.debug("Using ALL Frames!");
			// just turn on items that have values
			if (getStyleSheet().hasFrame(layout, Elements.CROWN)) {
				room.include(Elements.ElementsEnum.CROWN);
			}
			if (getStyleSheet().hasFrame(layout, Elements.TRIM)) {
				room.include(Elements.ElementsEnum.TRIM);
			}
			if (getStyleSheet().hasFrame(layout, Elements.PILLAR)) {
				room.include(Elements.ElementsEnum.PILLAR);
			}
			if (getStyleSheet().hasFrame(layout, Elements.PILASTER) &&
					room.getWidth() >= 5 && room.getDepth() >= 5) {
				room.include(Elements.ElementsEnum.PILASTER);
			}
			
			// gutter
			if (getStyleSheet().hasFrame(layout, Elements.GUTTER)) {
				room.include(Elements.ElementsEnum.GUTTER);
			}
			// grate
			if (getStyleSheet().hasFrame(layout, Elements.GRATE)) {
				room.include(Elements.ElementsEnum.GRATE);
			}
			
			// coffered
			if (getStyleSheet().hasFrame(layout, Elements.COFFERED_CROSSBEAM)) {
				room.include(Elements.ElementsEnum.COFFERED_CROSSBEAM);
			}
			if (getStyleSheet().hasFrame(layout, Elements.COFFERED_MIDBEAM)) {
				room.include(Elements.ElementsEnum.COFFERED_MIDBEAM);
			}		
			
			
			// wall base
			if (getStyleSheet().hasFrame(layout, Elements.WALL_BASE)) {
				room.include(Elements.ElementsEnum.WALL_BASE);
			}
			
			// wall capital
			if (getStyleSheet().hasFrame(layout, Elements.WALL_CAPITAL)) {
				room.include(Elements.ElementsEnum.WALL_CAPITAL);
			}			
			
			if (room.getType() == Type.ENTRANCE) {
				if (getStyleSheet().hasFrame(layout, Elements.COLUMN)) {
					room.include(Elements.ElementsEnum.COLUMN);
				}
				if (getStyleSheet().hasFrame(layout, Elements.CORNICE)) {
					room.include(Elements.ElementsEnum.CORNICE);
				}
				if (getStyleSheet().hasFrame(layout, Elements.CRENELLATION) &&
						room.getWidth() >= 7 && room.getDepth() >= 7) {
					room.include(Elements.ElementsEnum.CRENELLATION);
					room.include(Elements.ElementsEnum.MERLON);
					room.include(Elements.ElementsEnum.PARAPET);
				}
				else {
					if (getStyleSheet().hasFrame(layout, Elements.MERLON)) {
						room.include(Elements.ElementsEnum.MERLON);
					}
					else if(getStyleSheet().hasFrame(layout, Elements.PARAPET)) {
						room.include(Elements.ElementsEnum.PARAPET);
					}
				}
				if (getStyleSheet().hasFrame(layout, Elements.PLINTH)) {
					room.include(Elements.ElementsEnum.PLINTH);
				}
			}
		}
		else {
			if (getStyleSheet().hasFrame(layout, Elements.CROWN) && random.nextInt(100) < 25) {
				room.include(Elements.ElementsEnum.CROWN);
			}

			if (getStyleSheet().hasFrame(layout, Elements.TRIM) && random.nextInt(100) < 20) {
				room.include(Elements.ElementsEnum.TRIM);
			}
			if (getStyleSheet().hasFrame(layout, Elements.PILLAR) && random.nextInt(100) < 25) {
				room.include(Elements.ElementsEnum.PILLAR);
			}
			if (getStyleSheet().hasFrame(layout, Elements.PILASTER) && 
					room.getWidth() >= 5 && room.getDepth() >= 5 && random.nextInt(100) < 25) {
				room.include(Elements.ElementsEnum.PILASTER);
			}

			// gutter
			if (getStyleSheet().hasFrame(layout, Elements.GUTTER) && random.nextInt(100) < 25) {
				room.include(Elements.ElementsEnum.GUTTER);
			}
			// grate
			if (getStyleSheet().hasFrame(layout, Elements.GRATE) && (random.nextInt(100) < 15)) {
				room.include(Elements.ElementsEnum.GRATE);
			}
			
			// coffer
			if (getStyleSheet().hasFrame(layout, Elements.COFFERED_CROSSBEAM)
					&& random.nextInt(100) < 20) {
				room.include(Elements.ElementsEnum.COFFERED_CROSSBEAM);
			}
			if (getStyleSheet().hasFrame(layout, Elements.COFFERED_MIDBEAM)
					&& random.nextInt(100) < 20) {
				room.include(Elements.ElementsEnum.COFFERED_MIDBEAM);
			}		
			
			// wall base
			if (getStyleSheet().hasFrame(layout, Elements.WALL_BASE) && (random.nextInt(100) <25)) {
				room.include(Elements.ElementsEnum.WALL_BASE);
			}
			
			// wall capital
			if (getStyleSheet().hasFrame(layout, Elements.WALL_CAPITAL) && (random.nextInt(100) < 25)) {
				room.include(Elements.ElementsEnum.WALL_CAPITAL);
			}
			
			// entrance rooms
			if (room.getType() == Type.ENTRANCE) {
				if (room.getWidth() >=7
						&& getStyleSheet().hasFrame(layout, Elements.PILASTER) && random.nextInt(100) < 25) {
					room.include(Elements.ElementsEnum.PILASTER);
				}
				else {
					room.exclude(Elements.ElementsEnum.PILASTER);
				}
				room.exclude(Elements.ElementsEnum.PILLAR);
				
				if (getStyleSheet().hasFrame(layout, Elements.COLUMN) && random.nextInt(100) < 50) {
					room.include(Elements.ElementsEnum.COLUMN);
				}
				if (getStyleSheet().hasFrame(layout, Elements.CORNICE) && random.nextInt(100) < 60) {
					room.include(Elements.ElementsEnum.CORNICE);
				}
				if (getStyleSheet().hasFrame(layout, Elements.CRENELLATION) &&
						room.getWidth() >= 7 && room.getDepth() >= 7 && random.nextInt(100) < 50) {
					room.include(Elements.ElementsEnum.CRENELLATION);
					room.include(Elements.ElementsEnum.MERLON);
					room.include(Elements.ElementsEnum.PARAPET);
				}
				else {
					if (getStyleSheet().hasFrame(layout, Elements.MERLON) && random.nextInt(100) < 60) {
						room.include(Elements.ElementsEnum.MERLON);
					}
					else if(getStyleSheet().hasFrame(layout, Elements.PARAPET) && random.nextInt(100) < 70) {
						room.include(Elements.ElementsEnum.PARAPET);
					}
				}
				if (getStyleSheet().hasFrame(layout, Elements.PLINTH) && random.nextInt(100) < 50) {
					room.include(Elements.ElementsEnum.PLINTH);
				}
			}
		}
		
		// set the room with the selected layout
		room.setLayout(layout);
	}

	/**
	 * @return the styleSheet
	 */
	public StyleSheet getStyleSheet() {
		return styleSheet;
	}

	/**
	 * @param styleSheet the styleSheet to set
	 */
	public void setStyleSheet(StyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}
}
