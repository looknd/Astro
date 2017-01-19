package com.stackzhang.astro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.stackzhang.astro.bean.PlanetBean;

import android.content.Context;

public class Util {
	public static String loadStringFromAssets(Context context, String filename) {
		InputStream io = null;
		try {
			io = context.getAssets().open(filename);
			byte[] bytes = IOUtil.getBytes(io);
			return Util.convertToString(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (io != null) {
				try {
					io.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					io = null;
				}
			}
		}
		return "";
	}

	public static String convertToString(byte[] bytes) {
		try {
			if (bytes == null)
				return null;
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String(bytes);
	}

	public static PlanetBean getPlanetById(List<PlanetBean> list, int planet) {
		for (PlanetBean bean : list) {
			if (bean.id == planet)
				return bean;
		}
		return null;
	}
	public static PlanetBean getPlanetByName(List<PlanetBean> list, String name) {
		for (PlanetBean bean : list) {
			if (name.equalsIgnoreCase(bean.enName))
				return bean;
		}
		return null;
	}
	public static PlanetBean getAscPlanet(List<PlanetBean> list) {
		return getPlanetByName(list, "Asc");
	}
	public static PlanetBean getDesPlanet(List<PlanetBean> list) {
		return getPlanetByName(list, "Des");
	}
	public static PlanetBean getMcPlanet(List<PlanetBean> list) {
		return getPlanetByName(list, "MC");
	}
	public static PlanetBean getIcPlanet(List<PlanetBean> list) {
		return getPlanetByName(list, "IC");
	}

}
