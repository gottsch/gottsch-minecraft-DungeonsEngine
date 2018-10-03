/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.strategy;

import java.util.List;

import com.someguyssoftware.dungeonsengine.model.IRoom;

/**
 * @author Mark Gottschling on Aug 30, 2016
 *
 */
public interface IHallwayGenerationStrategy extends IRoomGenerationStrategy {
	public List<IRoom> getHallways();
	public void setHallways(List<IRoom> hallways);
}
