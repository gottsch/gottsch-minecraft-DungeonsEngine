/**
 * 
 */
package com.someguyssoftware.dungeonsengine.printer;

import com.someguyssoftware.gottschcore.Quantity;

/**
 * @author Mark Gottschling on Aug 27, 2017
 * @version 2.0
 */
public interface IPrettyPrinter {

	/*
	 * @since 2.0
	 */
	public String print(Object o);
	
	/**
	 * 
	 * @param q
	 * @return
	 */
	default public String quantityToString(Quantity q) {
		return q.getMin() + " <--> " + q.getMax();
	}
}
