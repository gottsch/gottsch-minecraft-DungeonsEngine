/**
 * 
 */
package com.someguyssoftware.dungeonsengine.style;

/**
 * @author Mark Gottschling on Sep 17, 2018
 *
 */
public class DecoratedRoom {
//
//	private IRoom room;
//	
//	
//	
//	// ui / styling
//	// TODO all these styling dont need to be assigned this until rendering phase.
//	// TODO add RoomDecorations(Room) class.
//	private Layout layout; 
//	private boolean crown;
//	private boolean trim;
//	private boolean pilaster;	
//	private boolean pillar;
//	private boolean gutter;
//	private boolean grate;
//	private boolean coffer;
//	
//	private boolean wallBase;
//	private boolean wallCapital;
//	
//	// TODO these next sets only belong to surface/exterior rooms and probably should be moved to a subclass
//	private boolean cornice;
//	private boolean plinth;	
//	private boolean column;
//	private boolean crenellation;
//	private boolean parapet;
//	private boolean merlon;
//	
//	// TODO this is a styling / decorating property, move to new class
//	private Multimap<IArchitecturalElement, ICoords> floorMap;
//	
//	/**
//	 * 
//	 */
//	public DecoratedRoom(IRoom room) {
//		this.room = room;
//	}
//
//	
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
