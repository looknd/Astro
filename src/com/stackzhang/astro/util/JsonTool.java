package com.stackzhang.astro.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class JsonTool {
	public static <T> T parseJson(String info, Class<T> clazz) {
		Gson gson = new Gson();
		try {
			T t = gson.fromJson(info, clazz);
			return t;
		} catch (Exception e) {
			// LogUtil.w("json parse Class error：" + clazz);
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parseJson(String info, Type type) {
		Gson gson = new Gson();
		try {
			T t = gson.fromJson(info, type);
			return t;
		} catch (Exception e) {
//			LogUtil.w("json parse type error：" + type);
			e.printStackTrace();
		}
		return null;
	}
}