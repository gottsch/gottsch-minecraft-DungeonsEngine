/**
 * 
 */
package com.someguyssoftware.dungeonsengine.config;

import com.someguyssoftware.gottschcore.config.IConfig;

/**
 * @author Mark Gottschling on Oct 4, 2018
 *
 */
public interface IDungeonsEngineConfig extends IConfig {

	public boolean isEnableChests();
	public void setEnableChests(boolean enable);
	
	public boolean isEnableSpawners();
	public void setEnableSpawners(boolean enable);
	
}
