/**
 * 
 */
package com.someguyssoftware.dungeonsengine.style;

import java.util.EnumSet;

import com.someguyssoftware.dungeonsengine.model.Elements.ElementsEnum;
import com.someguyssoftware.dungeonsengine.model.IRoom;

/**
 * @author Mark Gottschling on Sep 17, 2018
 *
 */
public class DecoratedRoom implements IDecoratedRoom {

	private IRoom room;

	private EnumSet<ElementsEnum> elements;
	
	/**
	 * 
	 * @param room
	 */
	public DecoratedRoom(IRoom room) {
		this.elements = EnumSet.noneOf(ElementsEnum.class);
		this.room = room;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.style.IDecoratedRoom#has(com.someguyssoftware.dungeonsengine.model.Elements.ElementsEnum)
	 */
	@Override
	public boolean has(ElementsEnum element) {
		return elements.contains(element);
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.style.IDecoratedRoom#getRoom()
	 */
	@Override
	public IRoom getRoom() {
		return room;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.dungeonsengine.style.IDecoratedRoom#setRoom(com.someguyssoftware.dungeonsengine.model.IRoom)
	 */
	@Override
	public void setRoom(IRoom room) {
		this.room = room;
	}
	
/*
 * 	TODO maybe have a list that is a collection of elements that are set ie List<IArchitecturalElement> = {CROWN, TRIM, GUTTER}
 * then a single method has(x) { return list.contains(x);} or hasCrown() {return list.contains(CROWN);}
 */

//	private Layout layout; 

//	
//	// TODO this is a styling / decorating property, move to new class
//	private Multimap<IArchitecturalElement, ICoords> floorMap;

//	///////////
//	/**
//	 * @return the layout
//	 */
//	public Layout getLayout() {
//		return layout;
//	}
//
//	/**
//	 * @param layout the layout to set
//	 */
//	public IRoom setLayout(Layout layout) {
//		this.layout = layout;
//		return this;
//	}
//
//	/**
//	 * Lazy-loaded getter.
//	 * @return the floorMap
//	 */
//	public Multimap<DesignElement, ICoords> getFloorMap() {
//		if (floorMap == null) {
//			floorMap = ArrayListMultimap.create();
//		}
//		return floorMap;
//	}
//
//	/**
//	 * @param floorMap the floorMap to set
//	 */
//	public void setFloorMap(Multimap<DesignElement, ICoords> floorMap) {
//		this.floorMap = floorMap;
//	}
//
//	/**
//	 * @return the trim
//	 */
//	public boolean hasTrim() {
//		return trim;
//	}
//
//	/**
//	 * @param trim the trim to set
//	 */
//	public void setHasTrim(boolean trim) {
//		this.trim = trim;
//	}
//
//	/**
//	 * @return the cornice
//	 */
//	public boolean hasCornice() {
//		return cornice;
//	}
//
//	/**
//	 * @param cornice the cornice to set
//	 */
//	public void setHasCornice(boolean cornice) {
//		this.cornice = cornice;
//	}
//
//	/**
//	 * @return the plinth
//	 */
//	public boolean hasPlinth() {
//		return plinth;
//	}
//
//	/**
//	 * @param plinth the plinth to set
//	 */
//	public void setHasPlinth(boolean plinth) {
//		this.plinth = plinth;
//	}
//
//	/**
//	 * @return the pillar
//	 */
//	public boolean hasPillar() {
//		return pillar;
//	}
//
//	/**
//	 * @param pillar the pillar to set
//	 */
//	public void setHasPillar(boolean pillar) {
//		this.pillar = pillar;
//	}
//
//	/**
//	 * @return the column
//	 */
//	public boolean hasColumn() {
//		return column;
//	}
//
//	/**
//	 * @param column the column to set
//	 */
//	public void setHasColumn(boolean column) {
//		this.column = column;
//	}
//
//	/**
//	 * @return the crown
//	 */
//	public boolean hasCrown() {
//		return crown;
//	}
//
//	/**
//	 * @param crown the crown to set
//	 */
//	public void setHasCrown(boolean crown) {
//		this.crown = crown;
//	}
//
//	/**
//	 * @return the crenellation
//	 */
//	public boolean hasCrenellation() {
//		return crenellation;
//	}
//
//	/**
//	 * @param crenellation the crenellation to set
//	 */
//	public void setHasCrenellation(boolean crenellation) {
//		this.crenellation = crenellation;
//	}
//
//	/**
//	 * @return the parapet
//	 */
//	public boolean hasParapet() {
//		return parapet;
//	}
//
//	/**
//	 * @param parapet the parapet to set
//	 */
//	public void setHasParapet(boolean parapet) {
//		this.parapet = parapet;
//	}
//
//	/**
//	 * @return the merlon
//	 */
//	public boolean hasMerlon() {
//		return merlon;
//	}
//
//	/**
//	 * @param merlon the merlon to set
//	 */
//	public void setHasMerlon(boolean merlon) {
//		this.merlon = merlon;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean hasPilaster() {
//		return pilaster;
//	}
//	
//	/**
//	 * 
//	 * @param pilaster
//	 */
//	public void setHasPilaster(boolean pilaster) {
//		this.pilaster = pilaster;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean hasGutter() {
//		return gutter;		
//	}
//	
//	/**
//	 * 
//	 * @param gutter
//	 */
//	public void setHasGutter(boolean gutter) {
//		this.gutter = gutter;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean hasGrate() {
//		return grate;
//	}
//	
//	/**
//	 * 
//	 * @param grate
//	 */
//	public void setHasGrate(boolean grate) {
//		this.grate = grate;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean hasCoffer() {
//		return this.coffer;
//	}
//	
//	/**
//	 * 
//	 * @param coffer
//	 */
//	public void setHasCoffer(boolean coffer) {
//		this.coffer = coffer;
//	}
//	
//	/**
//	 * 
//	 * @param coffer
//	 */
//	public void setCoffer(boolean coffer) {
//		setHasCoffer(coffer);
//	}
//	
//	/**
//	 * 
//	 * @param base
//	 * @return
//	 */
//	public boolean hasWallBase() {
//		return this.wallBase;
//	}
//	
//	/**
//	 * 
//	 * @param base
//	 */
//	public void setHasWallBase(boolean base) {
//		this.wallBase = base;
//	}
//	
//	/**
//	 * 
//	 * @param base
//	 */
//	public void setWallBase(boolean base) {
//		setHasWallBase(base);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean hasWallCapital() {
//		return this.wallCapital;
//	}
//	
//	/**
//	 * 
//	 * @param capital
//	 */
//	public void setHasWallCapital(boolean capital) {
//		this.wallCapital = capital;
//	}
//	
//	/**
//	 * 
//	 * @param capital
//	 */
//	public void setWallCapital(boolean capital) {
//		setHasWallCapital(capital);
//	}
}
