/**
 * 
 */
package com.someguyssoftware.dungeonsengine.spawner;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on Jan 15, 2017
 *
 */
public class SpawnerPopulator {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private SpawnSheet spawnSheet;
	private Multimap<String, SpawnGroup> map;
	
	/**
	 * 
	 * @param sheet
	 */
	public SpawnerPopulator(SpawnSheet sheet) {
		map = ArrayListMultimap.create();
		this.spawnSheet = sheet;
		// organize style sheet layouts based on category
		loadSpawnSheet(sheet);
		
	}

	/**
	 * 
	 * @param spawnSheet
	 */
	public void loadSpawnSheet(SpawnSheet spawnSheet) {
		// clear the map
		map.clear();
		// for each group remap by category and level(s) into multi map
		for (Entry<String, SpawnGroup> e : spawnSheet.getGroups().entrySet()) {
			SpawnGroup group = e.getValue();
			if (group.getCategory() != null && !group.getCategory().equals("")) {
				map.put(group.getCategory().toLowerCase(), group);
			}
			else {
				map.put("common", group);
			}
			
			// TODO FUTURE for every level between min and max, add entry to the array
		}
	}
	
	/**
	 * 
	 * @param random
	 * @param inventory
	 * @param group
	 */
	public void populate(Random random, TileEntityMobSpawner spawner, SpawnGroup group) {
		String mob = group.getMobs().get(random.nextInt(group.getMobs().size()));		
//		spawner.getSpawnerBaseLogic().setEntityName(mob);
		spawner.getSpawnerBaseLogic().setEntityId(new ResourceLocation(mob));
		logger.debug("Adding mob to spawner:" + mob);		
	}
	
	/**
	 * @return the spawnSheet
	 */
	public SpawnSheet getSpawnSheet() {
		return spawnSheet;
	}

	/**
	 * @param spawnSheet the spawnSheet to set
	 */
	public void setSpawnSheet(SpawnSheet spawnSheet) {
		this.spawnSheet = spawnSheet;
	}
}
