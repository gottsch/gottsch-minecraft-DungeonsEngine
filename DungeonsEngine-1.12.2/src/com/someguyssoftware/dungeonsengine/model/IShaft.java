package com.someguyssoftware.dungeonsengine.model;

public interface IShaft {

	/**
	 * @return the source
	 */
	IRoom getSource();

	/**
	 * @param source the source to set
	 */
	void setSource(IRoom source);

	/**
	 * @return the dest
	 */
	IRoom getDest();

	/**
	 * @param dest the dest to set
	 */
	void setDest(IRoom dest);

}