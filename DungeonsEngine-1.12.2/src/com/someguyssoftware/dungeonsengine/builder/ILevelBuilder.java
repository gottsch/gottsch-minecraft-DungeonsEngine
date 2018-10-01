package com.someguyssoftware.dungeonsengine.builder;

import java.util.List;

import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.ILevel;
import com.someguyssoftware.dungeonsengine.model.IRoom;
import com.someguyssoftware.dungeonsengine.model.IShaft;
import com.someguyssoftware.dungeonsengine.model.Shaft;

public interface ILevelBuilder {
	public static final Shaft EMPTY_SHAFT = new Shaft(); // TODO should this go here or in IRoomBuilder
	
	/**
	 * 
	 * @return
	 */
	ILevel build();

	LevelConfig getConfig();
	void setConfig(LevelConfig config);

	IRoomBuilder getRoomBuilder();
	void setRoomBuilder(IRoomBuilder builder);

	List<IRoom> getPlannedRooms();

	ILevelBuilder withRoom(IRoom r);

	void reset();

	IShaft join(IRoom sourceRoom, IRoom destRoom);
	IShaft join(ILevel sourceLevel, ILevel destLevel);
}