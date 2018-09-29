package com.someguyssoftware.dungeonsengine.builder;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.ILevel;

public interface ILevelBuilder {

	/**
	 * 
	 * @return
	 */
	ILevel build();

	LevelConfig getConfig();
	void setConfig(LevelConfig config);

	IRoomBuilder getRoomBuilder();
	void setRoomBuilder(IRoomBuilder builder);

}