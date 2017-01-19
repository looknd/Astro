package com.stackzhang.astro.bean;

import java.util.List;

/**
 * 行星 /assets/files/planet.json
 * @author stackzhang
 *
 */
public class PlanetBean {
	public int id;
	public String symbol;
	public String enName;
	public String enSimpleName;
	public String chName;
	public String color;
	public double angle;
	public List<Aspect> aspects;// 相位
	
	public PlanetBean(int id, String symbol, String enName, String enSimpleName, String chName, String color) {
		this.id = id;
		this.symbol = symbol;
		this.enName = enName;
		this.enSimpleName = enSimpleName;
		this.chName = chName;
		this.color = color;
	}

}
