/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import java.util.LinkedList;
import java.util.List;

import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.style.Theme;

/**
 * @author Mark Gottschling on Jul 27, 2016
 *
 */
public class Dungeon implements IDungeon {
	private String name;
	private IRoom entrance;
	private List<ILevel> levels;
	// TODO shafts needs to be indexed by levelIndex
	private List<Shaft> shafts;
	private Integer minX, maxX;
	private Integer minY, maxY;
	private Integer minZ, maxZ;
	private Theme theme;
	
	private DungeonConfig config;
	
	/**
	 * 
	 */
	public Dungeon() {
		levels = new LinkedList<>();
		this.config = new DungeonConfig();
	}

	/**
	 * 
	 * @param config
	 */
	public Dungeon(DungeonConfig config) {
		levels = new LinkedList<>();
		this.config = config;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#getLevels()
	 */
	@Override
	public List<ILevel> getLevels() {
		return levels;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#setLevels(java.util.List)
	 */
	@Override
	public void setLevels(List<ILevel> levels) {
		this.levels = levels;
	}

	/**
	 * @return the minX
	 */
	@Override
	public Integer getMinX() {
		return minX;
	}

	/**
	 * @param minX the minX to set
	 */
	@Override
	public void setMinX(Integer minX) {
		this.minX = minX;
	}

	/**
	 * @return the maxX
	 */
	@Override
	public Integer getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maxX to set
	 */
	@Override
	public void setMaxX(Integer maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the minY
	 */
	@Override
	public Integer getMinY() {
		return minY;
	}

	/**
	 * @param minY the minY to set
	 */
	@Override
	public void setMinY(Integer minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxY
	 */
	@Override
	public Integer getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY the maxY to set
	 */
	@Override
	public void setMaxY(Integer maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the minZ
	 */
	@Override
	public Integer getMinZ() {
		return minZ;
	}

	/**
	 * @param minZ the minZ to set
	 */
	@Override
	public void setMinZ(Integer minZ) {
		this.minZ = minZ;
	}

	/**
	 * @return the maxZ
	 */
	@Override
	public Integer getMaxZ() {
		return maxZ;
	}

	/**
	 * @param maxZ the maxZ to set
	 */
	@Override
	public void setMaxZ(Integer maxZ) {
		this.maxZ = maxZ;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#getEntrance()
	 */
	@Override
	public IRoom getEntrance() {
		return entrance;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#setEntrance(com.someguyssoftware.dungeonsengine.model.IRoom)
	 */
	@Override
	public void setEntrance(IRoom entrance) {
		this.entrance = entrance;
	}

	/**
	 * @return the theme
	 */
	@Override
	public Theme getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	@Override
	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#getShafts()
	 */
	@Override
	public List<Shaft> getShafts() {
		return shafts;
	}

	/**
	 * @param shafts the shafts to set
	 */
	@Override
	public void setShafts(List<Shaft> shafts) {
		this.shafts = shafts;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#getConfig()
	 */
	@Override
	public DungeonConfig getConfig() {
		return config;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#setConfig(com.someguyssoftware.dungeonsengine.config.DungeonConfig)
	 */
	@Override
	public void setConfig(DungeonConfig config) {
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.model.IDungeon#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
