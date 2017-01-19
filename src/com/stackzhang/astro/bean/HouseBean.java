package com.stackzhang.astro.bean;

/**
 * 宫位[1-12]， 每个相位角度为30°
 * @author stackzhang
 *
 */
public class HouseBean {
	public int index;//1-12
	public String symbol;
	public String color;
	public double angle;
	public HouseBean(int index, String symbol, String color, double angle) {
		this.index = index;
		this.symbol = symbol;
		this.color = color;
		this.angle = angle;
	}
	
}
