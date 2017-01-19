package com.stackzhang.astro.bean;

import android.graphics.Color;

/**
 * 相位 - 5种
 * @author stackzhang
 *
 */
public class Aspect {
	public AspectType type;
	public PlanetBean planet;
	public boolean isDraw;
	public int deltaDegree;
	public int deltaMinute;

	public Aspect(AspectType type, PlanetBean planet, boolean isDraw){
		this.type = type;
		this.planet = planet;
		this.isDraw = isDraw;
	}
	public enum AspectType {
		Conjunction(0, 5, "合", Color.rgb(0xFE, 0xF9, 0x37)), 
		Sextile(60, 5, "六合", Color.rgb(0x23, 0xFF, 0xFE)),
		Square(90, 5, "刑", Color.rgb(0xF2, 0x38, 0x37)), 
		Trine(120, 5, "拱", Color.rgb(0x2B, 0xC8, 0x2D)), 
		Opposition(180, 5, "冲", Color.rgb(0x20, 0xAC, 0xFC)), ;
		private int value;
		private float delta;
		private String name;
		private int color;

		private AspectType(int value, float delta, String name, int color) {
			this.value = value;
			this.delta = delta;
			this.name = name;
			this.color = color;
		}

		public static AspectType getType(double angle) {
			for (AspectType type : AspectType.values()) {
				if (type.value - type.delta <= angle && angle <= type.value + type.delta) {
					return type;
				}
			}
			return null;
		}

		public int getValue(){
			return value;
		}
		public int getColor() {
			return color;
		}

		public String getName() {
			return name;
		}

	}
}
