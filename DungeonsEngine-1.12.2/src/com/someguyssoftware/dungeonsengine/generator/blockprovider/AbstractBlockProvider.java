/**
 * 
 */
package com.someguyssoftware.dungeonsengine.generator.blockprovider;

import com.someguyssoftware.dungeonsengine.style.StyleSheet;

/**
 * @author Mark Gottschling on Oct 2, 2018
 *
 */
public abstract class AbstractBlockProvider implements IDungeonsBlockProvider {
	protected StyleSheet defaultStyleSheet;
	
	/**
	 * 
	 */
	public AbstractBlockProvider(StyleSheet sheet) {
		setDefaultStyleSheet(sheet);
	}

	@Override
	public StyleSheet getDefaultStyleSheet() {
		return defaultStyleSheet;
	}
	
	public void setDefaultStyleSheet(StyleSheet sheet) {
		this.defaultStyleSheet = sheet;
	}
}
