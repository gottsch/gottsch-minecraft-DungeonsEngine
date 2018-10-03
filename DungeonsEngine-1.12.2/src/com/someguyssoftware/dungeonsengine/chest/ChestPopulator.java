/**
 * 
 */
package com.someguyssoftware.dungeonsengine.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.someguyssoftware.gottschcore.inventory.InventoryUtil;
import com.someguyssoftware.gottschcore.item.util.ItemUtil;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.random.IRandomProbabilityItem;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.random.RandomProbabilityCollection;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Sep 12, 2016
 *
 */
public class ChestPopulator {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	
	private static final String POTION_ITEM_NAME = "minecraft:potion";
	private static final String SPLASH_POTION_ITEM_NAME = "minecraft:splash_potion";
	private static final String LINGERING_POTION_ITEM_NAME = "minecraft:lingering_potion";
	
	private ChestSheet chestSheet;
	private Multimap<String, ChestContainer> map;
	
	/**
	 * 
	 * @param sheet
	 */
	public ChestPopulator(ChestSheet sheet) {
		map = ArrayListMultimap.create();
		this.chestSheet = sheet;
		
		// organize style sheet layouts based on category
		loadChestSheet(chestSheet);
	}
	
	/**
	 * 
	 * @param chestSheet
	 */
	public void loadChestSheet(ChestSheet chestSheet) {
		// clear the map
		map.clear();
		// for each layout remap by category into multi map
		for (Entry<String, ChestContainer> e : chestSheet.getContainers().entrySet()) {
			ChestContainer container = e.getValue();
			if (container.getCategory() != null && !container.getCategory().equals("")) {
				map.put(container.getCategory().toLowerCase(), container);
			}
			else {
				map.put("common", container);
			}
		}
	}
	
	/**
	 * 
	 * @param world
	 * @param chestCoords
	 * @return
	 */
	public TileEntityChest getChestTileEntity(World world, ICoords chestCoords) {
		TileEntity te = world.getTileEntity(chestCoords.toPos());
		if (te == null) {
			logger.warn("Unable to locate Chest TileEntity @: " + chestCoords.toShortString());
			return null;
		}
		
		if (!(te instanceof TileEntityChest)) {
			logger.warn(String.format("TileEntity is not an instance of TileEntityChest @ %s", chestCoords.toShortString()));
		}
		
		return (TileEntityChest) te;
	}
	
	/**
	 * 
	 * @param inventory
	 * @param sheet
	 */
	// TODO change container to containerName and remove sheet
	public void populate(Random random, IInventory inventory, ChestContainer container) {
		List<RandomItem> allItems = new ArrayList<>(container.getRandomItems());
		
		for (RandomGroup randomGroup : container.getRandomGroups()) {
			// get the group
			ChestItemGroup group = getChestSheet().getGroups().get(randomGroup.getRef());
			
			// TODO need a Double VERSION of this
			RandomProbabilityCollection<IRandomProbabilityItem> col = new RandomProbabilityCollection<>();
			for (RandomItem ri : group.getItems()) {
//				logger.debug(String.format("Processing item %s from group %s", ri.getRef(), group.getName()));
				col.add((int) ri.getWeight(), ri);
			}
			
			// determine the number of items to add from the group
			int numberOfItems = RandomHelper.randomInt(
					(int) (randomGroup.getQuantity().getMin() * randomGroup.getItemsFactor()),
					(int) (randomGroup.getQuantity().getMax() * randomGroup.getItemsFactor()));
			
			// NOTE this part is really moot now
			// add the number of random items to the list
			for (int i = 0; i < numberOfItems; i++) {
				RandomItem ri = (RandomItem) col.next();
//				logger.debug("Adding item to collection:" + ri.getRef());
				allItems.add(ri);
			}

			// check if the inventory has slots available
			List<Integer> slots = InventoryUtil.getAvailableSlots(inventory);
			if (slots == null || slots.isEmpty()) {
				logger.warn("Slots is null or empty.");
				return;
			}
//			logger.debug("Slots available:" + slots.size());
			
			// TODO move the processing of the items into here because the randomGroup is required for the chance factor
			// process each random item listed
			for (RandomItem randomItem : allItems) {
							
				/*
				 *  TODO add the chanceFactor to method toItemStack().
				 *  Needs to check with doubles, not ints
				 */
				// get the minecraft itemstack
				ItemStack itemStack = toItemStack(random, randomItem, randomGroup.getChanceFactor(), getChestSheet());			
				if (itemStack == null) {
//					logger.debug("Item not added: " + randomItem.getRef());
					continue;
				}			
//				logger.debug("Attempting to add item to chest:" + itemStack.getDisplayName());
				
				// add itemstack to chest
				if (itemStack != null) {
					InventoryUtil.addItemToInventory(inventory, itemStack, random, slots);
//					logger.debug("Added item to chest:" + itemStack.getDisplayName() + ". Slots left:" + slots.size());
					if (slots == null || slots.size() == 0) {
						break;
					}
				}
			}	
			
			// clear the all items
			allItems.clear();
		}
		
	}
	
	/**
	 * Converts to a vanilla Minecraft ItemStack with any enchantments.
	 * The resultant itemStack does NOT limit the stack size based on the item's stack limit.
	 * @param random
	 * @return
	 */
	public static ItemStack toItemStack(Random random, RandomItem randomItem, double chanceFactor, ChestSheet sheet) {
		ChestItem chestItem = sheet.getItems().get(randomItem.getRef());
		
		if (chestItem == null) {
			logger.warn("Unable to locate chest item in sheet: " + randomItem.getRef());
			return null;
		}
//		logger.debug("Chest Item:" + chestItem.getName());
		
		Item item = null;
		ItemStack stack = null;
		
		try {		
			// calculate the probablility that the item will generate
			boolean checkProbability = true;
			if (randomItem.getChance() < 100.0) {
				double r = random.nextDouble() * 100.0;
//				logger.debug("item random probability:" + r);
				// determine if selected item meet probability criteria
				double chance = randomItem.getChance() * chanceFactor;
//				logger.debug("item random chance:" + chance);
				if (r > (chance)) {
					checkProbability = false;
				}
			}
	
			if (checkProbability) {				
				// check if poition, then build potion and return
				if (chestItem.getName().equalsIgnoreCase(POTION_ITEM_NAME)
						|| chestItem.getName().equalsIgnoreCase(SPLASH_POTION_ITEM_NAME)
						|| chestItem.getName().equalsIgnoreCase(LINGERING_POTION_ITEM_NAME)) {
					stack = toPotion(chestItem);
					return stack;
				}
				
				// create the item
				item = toItem(chestItem.getName());
				if (item == null) {
					logger.warn("Unable to convert ChestItem to minecraft item: ", chestItem.getName());
					return null;
				}
//				logger.debug("Converted random item to (unlocalized NAME): " + item.getUnlocalizedName() + ":" + item);
				
				// calculate the number items to generate
				int size = RandomHelper.randomInt(random, randomItem.getQuantity().getMinInt(), randomItem.getQuantity().getMaxInt());
				// get the damage value. If greater than 0, create an item stack can call the proper method. (for things like dyes (lapis) and potions that have
				// multiple items per)
				if (chestItem.getDamage() > 0) {
					// create an itemStack
					stack = new ItemStack(item, size, chestItem.getDamage());
				}
				else {
					stack = new ItemStack(item, size);
				}
//				logger.debug("Converted Item to Stack:" + stack.getDisplayName());
				
				int enchants = 0;
				if (randomItem.getEnchants().getQuantity().getMaxInt() > 0) {
					enchants = RandomHelper.randomInt(random,
							randomItem.getEnchants().getQuantity().getMinInt(),
							randomItem.getEnchants().getQuantity().getMaxInt());
	//				logger.debug("Adding enchantments");
					// TODO update addEnchantments with new enchantments and for shield
					for (int i = 0; i < enchants; i++) {	
						stack = ItemUtil.addEnchantment(stack);
					}
				}	
				// add specified enchantments
				if (randomItem.getEnchants().getEnchantments() != null &&
						randomItem.getEnchants().getEnchantments().size() > 0) {
					for (ChestItemEnchantment e : randomItem.getEnchants().getEnchantments()) {
						// TODO create method to add enchantment based on NAME to the ItemUtil
					}
				}
			}		
		}
		catch(Exception e) {
			logger.error("Error generating item from RandomItem:", e);
		}
		return stack;	
	}
	
	/**
	 * 
	 * @param chestItem
	 * @return
	 */
	public static ItemStack toPotion(ChestItem chestItem) {
		try {
			Item item = ItemUtil.getItemFromName(chestItem.getName());
			PotionType type = PotionType.getPotionTypeForName(chestItem.getType());
			ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(item), type);
			return stack;
		}
		catch(Exception e) {
			logger.error("toItem error:", e);
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static Item toItem(String itemName) {
		try {
			Item item = ItemUtil.getItemFromName(itemName);			
			return item;
		}
		catch(Exception e) {
			logger.error("toItem error:", e);
			return null;
		}
	}

	/**
	 * @return the chestSheet
	 */
	public ChestSheet getChestSheet() {
		return chestSheet;
	}

	/**
	 * @param chestSheet the chestSheet to set
	 */
	public void setChestSheet(ChestSheet chestSheet) {
		this.chestSheet = chestSheet;
	}

	/**
	 * @return the map
	 */
	public Multimap<String, ChestContainer> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Multimap<String, ChestContainer> map) {
		this.map = map;
	}
}
