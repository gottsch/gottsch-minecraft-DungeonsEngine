package com.someguyssoftware.dungeonsengine.model;

import com.someguyssoftware.dungeonsengine.enums.Face;

public interface IArchitecturalElement {

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @return the hasHorizontalSupport
	 */
	boolean isHasHorizontalSupport();

	/**
	 * @param hasHorizontalSupport the hasHorizontalSupport to set
	 */
	void setHasHorizontalSupport(boolean hasHorizontalSupport);

	/**
	 * @return the hasVerticalSupport
	 */
	boolean isHasVerticalSupport();

	/**
	 * @param hasVerticalSupport the hasVerticalSupport to set
	 */
	void setHasVerticalSupport(boolean hasVerticalSupport);

	/**
	 * @return the horizontalSupport
	 */
	double getHorizontalSupport();

	/**
	 * @param horizontalSupport the horizontalSupport to set
	 */
	void setHorizontalSupport(double horizontalSupport);

	/**
	 * @return the verticalSupport
	 */
	double getVerticalSupport();

	/**
	 * @param verticalSupport the verticalSupport to set
	 */
	void setVerticalSupport(double verticalSupport);

	/**
	 * @return the face
	 */
	Face getFace();

	/**
	 * @param face the face to set
	 */
	void setFace(Face face);

	/**
	 * @return the base
	 */
	IArchitecturalElement getBase();

	/**
	 * @param base the base to set
	 */
	void setBase(IArchitecturalElement base);

}