/**
 * 
 */
package com.someguyssoftware.dungeonsengine.block;

import com.someguyssoftware.gottschcore.block.ModBlock;

import net.minecraft.block.material.Material;

/**
 * @author Mark Gottschling on Sep 3, 2016
 *
 */
public class NullBlock extends ModBlock {

	/**
	 * 
	 * @param modID
	 */
	public NullBlock(String modID) {		
		super(modID, "null_block", Material.AIR);
	}

}
