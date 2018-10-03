/**
 * 
 */
package com.someguyssoftware.dungeonsengine.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.someguyssoftware.dungeonsengine.style.ArchitecturalElement;
import com.someguyssoftware.dungeonsengine.style.IArchitecturalElement;
import com.someguyssoftware.gottschcore.enums.IEnum;

/**
 * @author Mark Gottschling on Aug 19, 2018
 *
 */
public class Elements {
	public static IArchitecturalElement NONE = new ArchitecturalElement("none", -1, -1);
	
	public static IArchitecturalElement AIR = new ArchitecturalElement("air", 0, 0);
	public static IArchitecturalElement SURFACE_AIR = new ArchitecturalElement("surface_air", 0, 0, AIR);
	public static IArchitecturalElement FLOOR_AIR = new ArchitecturalElement("floor_air", 0, 0, AIR);
	public static IArchitecturalElement WALL_AIR = new ArchitecturalElement("wall_air", 0, 0);
	public static IArchitecturalElement CEILING_AIR = new ArchitecturalElement("ceiling_air", 0, 0);

	public static IArchitecturalElement FLOOR = new ArchitecturalElement("floor", 100, 50);
	public static IArchitecturalElement FLOOR_ALT = new ArchitecturalElement("floor_alt", 100, 50);
	public static IArchitecturalElement WALL = new ArchitecturalElement("wall", 100, 50);
	public static IArchitecturalElement WALL_BASE = new ArchitecturalElement("wall_base", 100, 50, WALL);
	public static IArchitecturalElement WALL_CAPITAL = new ArchitecturalElement("wall_capital", 100, 50, WALL);
	public static IArchitecturalElement CEILING = new ArchitecturalElement("ceiling", 100, 50);
	
	public static IArchitecturalElement BASE;
	public static IArchitecturalElement COLUMN;
	public static IArchitecturalElement CAPITAL;
	
	public static IArchitecturalElement TRIM;
	public static IArchitecturalElement CROWN;
	public static IArchitecturalElement CORNICE;
	public static IArchitecturalElement PLINTH;
	
	public static IArchitecturalElement PILASTER;
	public static IArchitecturalElement PILASTER_BASE;
	public static IArchitecturalElement PILASTER_CAPITAL;
	public static IArchitecturalElement PILLAR;
	public static IArchitecturalElement PILLAR_BASE;
	public static IArchitecturalElement PILLAR_CAPITAL;
	
	public static IArchitecturalElement CRENELLATION;
	public static IArchitecturalElement PARAPET;
	public static IArchitecturalElement MERLON;
	
	public static IArchitecturalElement COFFER;
	public static IArchitecturalElement COFFERED_MIDBEAM;
	public static IArchitecturalElement COFFERED_CROSSBEAM;
	public static IArchitecturalElement LADDER;
	public static IArchitecturalElement LADDER_PILLAR;
	public static IArchitecturalElement GUTTER;
	public static IArchitecturalElement GRATE;
	
	public static IArchitecturalElement WINDOW;
	public static IArchitecturalElement SIGN;
	
	// legecy
	public static IArchitecturalElement FACADE;
	public static IArchitecturalElement FACADE_SUPPORT;
	
	/**
	 * 
	 */
	public Elements() {	
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static IArchitecturalElement getElement(String name) {
		return ElementsEnum.valueOf(name.toUpperCase()).getElement();
	}
	
	/**
	 * Wrapper for all the ArchitecturalElements to enable fast lookups.
	 * @author Mark Gottschling on Aug 19, 2018
	 *
	 */
	public enum ElementsEnum implements IEnum {
		NONE(-1, Elements.NONE.getName(), Elements.NONE),
		AIR(0, Elements.AIR.getName(), Elements.AIR),
		FLOOR(1, Elements.FLOOR.getName(), Elements.FLOOR),
		 FLOOR_ALT(1, Elements.FLOOR_ALT.getName(), Elements.FLOOR_ALT),
		WALL(2, Elements.WALL.getName(), Elements.WALL),
		WALL_BASE(3, Elements.WALL_BASE.getName(), Elements.WALL_BASE),
		WALL_CAPITAL(4, Elements.WALL_CAPITAL.getName(), Elements.WALL_CAPITAL),
		CEILING(3, Elements.CEILING.getName(), Elements.CEILING),
		BASE(4, Elements.BASE.getName(), Elements.BASE),
		COLUMN(5, Elements.COLUMN.getName(), Elements.COLUMN),
		CAPITAL(6, Elements.CAPITAL.getName(), Elements.CAPITAL),
		TRIM(7, Elements.TRIM.getName(), Elements.TRIM),
		CROWN(8, Elements.CROWN.getName(), Elements.CROWN),
		CORNICE(9, Elements.CORNICE.getName(), Elements.CORNICE),
		PLINTH(10, Elements.PLINTH.getName(), Elements.PLINTH),
		PILASTER(11, Elements.PILASTER.getName(), Elements.PILASTER),
		PILASTER_BASE(11, Elements.PILASTER_BASE.getName(), Elements.PILASTER_BASE),
		PILASTER_CAPITAL(11, Elements.PILASTER_CAPITAL.getName(), Elements.PILASTER_CAPITAL),
		PILLAR(12, Elements.PILLAR.getName(), Elements.PILLAR),
		PILLAR_BASE(12, Elements.PILLAR_BASE.getName(), Elements.PILLAR_BASE),
		PILLAR_CAPITAL(12, Elements.PILLAR_CAPITAL.getName(), Elements.PILLAR_CAPITAL),		
		COFFERED_CROSSBEAM(13, Elements.COFFERED_CROSSBEAM.getName(), Elements.COFFERED_CROSSBEAM),
		COFFERED_MIDBEAM(13, Elements.COFFERED_MIDBEAM.getName(), Elements.COFFERED_MIDBEAM),
		LADDER(25, Elements.LADDER.getName(), Elements.LADDER),
		LADDER_PILLAR(26, Elements.LADDER_PILLAR.getName(), Elements.LADDER_PILLAR),
		GUTTER(26, Elements.GUTTER.getName(), Elements.GUTTER),
		GRATE(27, Elements.GRATE.getName(), Elements.GRATE),
		WINDOW(27, Elements.WINDOW.getName(), Elements.WINDOW),
		SIGN(28, Elements.SIGN.getName(), Elements.SIGN),
		
		CRENELLATION(50, Elements.CRENELLATION.getName(), Elements.CRENELLATION),
		PARAPET(51, Elements.PARAPET.getName(), Elements.PARAPET),
		MERLON(52, Elements.MERLON.getName(), Elements.MERLON),
		
		FACADE(100, Elements.FACADE.getName(), Elements.FACADE),
		FACADE_SUPPORT(101, Elements.FACADE_SUPPORT.getName(), Elements.FACADE_SUPPORT);
		
		private Integer code;
		private String value;
		private IArchitecturalElement element;
		private static final Map<Integer, IEnum> codes = new HashMap<Integer, IEnum>();
		private static final Map<String, IEnum> values = new HashMap<String, IEnum>();
		
		// setup reverse lookup
		static {
			for (ElementsEnum x : EnumSet.allOf(ElementsEnum.class)) {
				codes.put(x.getCode(), x);
				values.put(x.getValue(), x);
			}
		}
		
		/**
		 * 
		 * @param code
		 * @param element
		 */
		ElementsEnum(int code, String value, IArchitecturalElement element) {
			this.code = code;
			this.value = value;
			this.element = element;
		}
		
		/**
		 * 
		 * @return
		 */
		public IArchitecturalElement getElement() {
			return this.element;
		}
		
		@Override
		public String getName() {
			return name();
		}
		
		@Override
		public Integer getCode() {
			return code;
		}

		@Override
		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public void setValue(String value) {
			this.value = value;
		}
		
		/**
		 * 
		 * @param code
		 * @return
		 */
		public static ElementsEnum getByCode(Integer code) {
			return (ElementsEnum) codes.get(code);
		}
		/**
		 * 
		 * @param value
		 * @return
		 */
		public static ElementsEnum getByValue(String value) {
			return (ElementsEnum) values.get(value);
		}

		/**
		 * 
		 */
		@Override
		public Map<Integer, IEnum> getCodes() {
			return codes;
		}

		/**
		 * 
		 * @return
		 */
		@Override
		public Map<String, IEnum> getValues() {
			return values;
		}
	}
}
