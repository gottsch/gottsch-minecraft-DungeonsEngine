package com.someguyssoftware.dungeonsengine.model;

import java.util.List;

import com.someguyssoftware.dungeonsengine.config.DungeonConfig;
import com.someguyssoftware.dungeonsengine.style.Theme;

/**
 * 
 * @author Mark Gottschling on Sep 28, 2018
 *
 */
public interface IDungeon {

	/**
	 * @return the levels
	 */
	List<ILevel> getLevels();

	/**
	 * @param levels the levels to set
	 */
	void setLevels(List<ILevel> levels);

	/**
	 * @return the entrance
	 */
	IRoom getEntrance();

	/**
	 * @param entrance the entrance to set
	 */
	void setEntrance(IRoom entrance);

	/**
	 * @return the shafts
	 */
	List<Shaft> getShafts();

	/**
	 * 
	 * @param shafts
	 */
	void setShafts(List<Shaft> shafts);
	
	/**
	 * @return the config
	 */
	DungeonConfig getConfig();

	/**
	 * @param config the config to set
	 */
	void setConfig(DungeonConfig config);

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	// TEMP
	Theme getTheme();

	void setTheme(Theme theme);
}