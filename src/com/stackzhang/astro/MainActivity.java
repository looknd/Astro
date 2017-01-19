package com.stackzhang.astro;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.stackzhang.astro.R;
import com.stackzhang.astro.bean.Aspect;
import com.stackzhang.astro.bean.HouseBean;
import com.stackzhang.astro.bean.PlanetBean;
import com.stackzhang.astro.bean.SignBean;
import com.stackzhang.astro.bean.Aspect.AspectType;
import com.stackzhang.astro.util.JsonTool;
import com.stackzhang.astro.util.Util;

public class MainActivity extends Activity {
	private SwissEph sw;

	private LinearLayout container;
	private DrawView drawView;
	private TextView tvInfo;
	private TextView et;

	private Button button;
	private double time;

	private boolean flag = false;

	private List<SignBean> signList;

	private double longitude;
	private double latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		new CopyAssetfiles(".*\\.se1", getApplicationContext()).copy();
		initViews();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!flag) {
					flag = true;
					update();
				} else {
					flag = false;
				}
			}
		});
		init();
		//ationWithTime(1982,8,6,15-8,123.78,41.30)
		SweDate sd = new SweDate(1982, 8, 6, 15-8);
		time = sd.getJulDay();
		
		calc();
	}

	private void initViews() {
		button = (Button) findViewById(R.id.ok);
		container = (LinearLayout) findViewById(R.id.container);
		tvInfo = (TextView) findViewById(R.id.tvOutput);
		et = (TextView) findViewById(R.id.et);
		drawView = new DrawView(getApplicationContext(), null);
		container.addView(drawView);
	}

	private void update() {
		new Thread() {
			@Override
			public void run() { 
				while (flag) {
					time ++;
					calc();
					try {
						sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void calc() {
		//123.78,41.30
		longitude = 123.78;//116	;
		latitude = 41.30;
		int flags = 0;
		double[] cusps = new double[13];
		double[] acsc = new double[10];
		//
		final StringBuffer sb1 = new StringBuffer();
		sb1.append("lng:" + longitude + ", lat:" + latitude + "\n");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));

		sb1.append(formatter.format(SweDate.getDate(time))+","+time);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				et.setText(sb1.toString());
			}
		});

		final List<HouseBean> houseList = new ArrayList<HouseBean>();
		int result = sw.swe_houses(time, flags, latitude, longitude, 'P', cusps, acsc);
		for(double x:cusps){
			System.out.println("->>"+x);
		}
		// 宫位
		for (int i = 1; i < 13; i++) {
			HouseBean bean = new HouseBean(i, "", "", cusps[i]);
			houseList.add(bean);
		}
		// 行星
		int[] planets = { SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MERCURY, SweConst.SE_VENUS, SweConst.SE_MARS, SweConst.SE_JUPITER,
				SweConst.SE_SATURN, SweConst.SE_URANUS, SweConst.SE_NEPTUNE, SweConst.SE_PLUTO, SweConst.SE_JUNO, SweConst.SE_CHIRON ,SweConst.SE_MEAN_NODE };
		// Load
		Type type = new TypeToken<List<PlanetBean>>() {
		}.getType();
		String planetJson = Util.loadStringFromAssets(getApplicationContext(), "files/planet.json");
		final List<PlanetBean> planetList = JsonTool.parseJson(planetJson, type);
		// 过滤
		Util.getAscPlanet(planetList).angle = acsc[0];
		Util.getDesPlanet(planetList).angle = ((acsc[0] + 180 > 360 ? acsc[0] - 180 : acsc[0] + 180));
		Util.getMcPlanet(planetList).angle = acsc[1];
		Util.getIcPlanet(planetList).angle = ((acsc[1] + 180 > 360 ? acsc[1] - 180 : acsc[1] + 180));

		flags = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED;// |
		// boolean retrograde = false;
		double[] xp = new double[6];
		for (int p = 0; p < planets.length; p++) {
			int planet = planets[p];
			StringBuffer serr = new StringBuffer();
			int ret = sw.swe_calc_ut(time, planet, flags, xp, serr);
			if (ret != flags) {
				// TODO:error
			}
			PlanetBean planetBean = Util.getPlanetById(planetList, planet);
			if (planetBean != null) {
				planetBean.angle = xp[0];
//				if(p == SweConst.SE_SUN){
//					Log.i("LogUtil", "SUN = "+xp[0]);
//				}
				System.out.println(planetBean.enName+"("+planetBean.chName+ ") angle = " + planetBean.angle+", retrograde="+xp[3]);
			} else {
				System.err.println("planet = " + planet);
			}
			// retrograde = (xp[3] < 0);
			// s += String.format("%-9s %s %c\n", planetName, xp[0], (retrograde
			// ? 'R' : 'D'));
		}

		// 计算
		final StringBuffer sb = new StringBuffer();
		sb.append("===行星-星座=========\n");
		for (PlanetBean planet : planetList) {
			String[] strs = szZodiac(planet.angle);
			SignBean signBean = signList.get(Integer.parseInt(strs[0]));
			sb.append(planet.chName + "-->" + signBean.chName + strs[1] + "°" + strs[2] + "′\n");
		}

		sb.append("\n===行星-宫位=========\n");
		for (PlanetBean planet : planetList) {
			String[] strs = szZodiac(planet.angle);
			sb.append(planet.chName + "-->第" + (Integer.parseInt(strs[0]) + 1) + "宫\n");
		}

		sb.append("\n===行星-行星相位=========\n");
		for (int i = 0; i < planetList.size() - 1; i++) {
			for (int j = i + 1; j < planetList.size(); j++) {
				double angle = planetList.get(i).angle - planetList.get(j).angle;
				if (angle >= 180) {
					angle = 360 - angle;
				} else if (angle <= -180) {
					angle += 360;
				} else {
					angle = Math.abs(angle);
				}
				AspectType aspectType = AspectType.getType(angle);
				if (aspectType != null) {
					if (planetList.get(i).id == -1 && planetList.get(j).id == -1) {
						continue;
					}
					double d = angle - aspectType.getValue();
					int[] x = calc(d);
					if (planetList.get(i).aspects == null) {
						planetList.get(i).aspects = new ArrayList<Aspect>();
					}
					Aspect aspect = new Aspect(aspectType, planetList.get(j), true);
					aspect.deltaDegree = x[0];
					aspect.deltaMinute = x[1];
					planetList.get(i).aspects.add(aspect);// 更新相位信息
					if (planetList.get(j).aspects == null) {
						planetList.get(j).aspects = new ArrayList<Aspect>();
					}
					aspect = new Aspect(aspectType, planetList.get(i), false);
					aspect.deltaDegree = x[0];
					aspect.deltaMinute = x[1];
					planetList.get(j).aspects.add(aspect);
					sb.append(planetList.get(i).chName + " " + aspectType.getName() + " " + planetList.get(j).chName + "\n");
				}
			}
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				drawView.setHouseAndPlanets(houseList, planetList);
				tvInfo.setText(sb.toString());
			}
		});
	}

	private String[] szZodiac(double d) {
		String as[] = new String[3];
		int i = (int) d / 30;
		int j = (int) d - i * 30;
		int k = Math.round(Math.round(60D * (d - Math.floor(d))));
		if (k == 60) {
			j++;
			k = 0;
		}
		as[0] = i + "";
		as[1] = String.valueOf(j);
		as[2] = String.valueOf(k);
		return as;
	}

	private int[] calc(double d) {
		int i = (int) d / 30;
		int j = (int) d - i * 30;
		int k = Math.round(Math.round(60D * (d - Math.floor(d))));
		if (k == 60) {
			j++;
			k = 0;
		}
		return new int[] { j, k };
	}

	/**
	 * 初始化Swiss星历表
	 */
	private void init() {
		sw = new SwissEph(getApplicationContext().getFilesDir() + File.separator + "/ephe");

		signList = JsonTool.parseJson(Util.loadStringFromAssets(getApplicationContext(), "files/sign.json"), new TypeToken<List<SignBean>>() {
		}.getType());
	}

}
