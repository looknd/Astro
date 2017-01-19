package com.stackzhang.astro;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.stackzhang.astro.bean.Aspect;
import com.stackzhang.astro.bean.HouseBean;
import com.stackzhang.astro.bean.PlanetBean;
import com.stackzhang.astro.bean.SignBean;
import com.stackzhang.astro.util.JsonTool;
import com.stackzhang.astro.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

/**
 * 2015-04-11 11:03:43
 * 
 * @author stackzhang
 * 
 */
public class DrawView extends View {
	private Paint mPaint = new Paint();
	private Paint symbolPaint = new Paint();

	private List<SignBean> signList = new ArrayList<SignBean>();
	private List<HouseBean> houseList = new ArrayList<HouseBean>();
	private List<PlanetBean> planetList = new ArrayList<PlanetBean>();

	private double deltaAngle = 0;
	private double size;
	private double mainRadius;
	private double planetRadius;
	private double signXRadius;
	private double signRadius;
	private double houseRadius;
	private Point centerPoint = new Point();

	private final static int LineColor = Color.rgb(0xDC, 0xD9, 0xDC);

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);

		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		LayoutParams params = new LayoutParams((int) (metrics.widthPixels * 0.9f), (int) (metrics.widthPixels * 0.9f));
		setLayoutParams(params);

		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true);
		symbolPaint.setAntiAlias(true);
		symbolPaint.setTextSize(28);
		symbolPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/kanxingpan.ttf"));

		// parseSigns();
		initSigns();
	}

	private void initSigns() {
		Type type = new TypeToken<List<SignBean>>() {
		}.getType();
		String planetJson = Util.loadStringFromAssets(getContext(), "files/sign.json");
		List<SignBean> l = JsonTool.parseJson(planetJson, type);
		signList.clear();
		if (l != null) {
			signList.addAll(l);
		}
	}

	public void setHouseAndPlanets(List<HouseBean> houseBeans, List<PlanetBean> planetBeans) {
		houseList.clear();
		planetList.clear();
		houseList.addAll(houseBeans);
		planetList.addAll(planetBeans);

		deltaAngle = 180 - Util.getAscPlanet(planetList).angle;
		size = getLayoutParams().width;
		centerPoint.x = centerPoint.y = (0.5 * size);
		mainRadius = 0.49 * size;
		signRadius = mainRadius * 0.8;
		signXRadius = mainRadius * 0.75;
		houseRadius = mainRadius * 0.58;
		planetRadius = mainRadius * 0.4;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// bg
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.BLACK);
		canvas.drawRect(-1, -1, getWidth() + 2, getHeight() + 2, mPaint);

		mPaint.setStyle(Style.STROKE);
		// drawSigns();
		drawSigns(canvas);

		// drawLines
		mPaint.setColor(Color.WHITE);
		Point pt = getPointByAngle(centerPoint, size / 2, Util.getAscPlanet(planetList).angle);
		canvas.drawLine((float) centerPoint.x, (float) centerPoint.y, (float) pt.x, (float) pt.y, mPaint);
		pt = getPointByAngle(centerPoint, size / 2, Util.getDesPlanet(planetList).angle);
		canvas.drawLine((float) centerPoint.x, (float) centerPoint.y, (float) pt.x, (float) pt.y, mPaint);
		pt = getPointByAngle(centerPoint, size / 2, Util.getMcPlanet(planetList).angle);
		canvas.drawLine((float) centerPoint.x, (float) centerPoint.y, (float) pt.x, (float) pt.y, mPaint);
		pt = getPointByAngle(centerPoint, size / 2, Util.getIcPlanet(planetList).angle);
		canvas.drawLine((float) centerPoint.x, (float) centerPoint.y, (float) pt.x, (float) pt.y, mPaint);

		// drawHouses
		drawHouses(canvas);

		// planets
		drawPlanets(canvas);
	}

	private void drawHouses(Canvas canvas) {
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(30);
		canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y, (float) houseRadius, mPaint);
		for (int i = 0; i < houseList.size(); i++) {
			HouseBean house = houseList.get(i);
			Point pt1 = getPointByAngle(centerPoint, houseRadius, house.angle);
			Point pt2 = getPointByAngle(centerPoint, signXRadius, house.angle);
			if (i % 3 != 0) {
				mPaint.setColor(Color.rgb(0x5E, 0x5E, 0x5E));// dashed
				mPaint.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, (float) 1.0));
				canvas.drawLine((float) pt1.x, (float) pt1.y, (float) centerPoint.x, (float) centerPoint.y, mPaint);
			}

			mPaint.setColor(Color.WHITE);
			mPaint.setPathEffect(null);
			canvas.drawLine((float) pt1.x, (float) pt1.y, (float) pt2.x, (float) pt2.y, mPaint);

			Point pt3 = getPointByAngle(centerPoint, houseRadius, house.angle + 15);
			Point pt4 = getPointByAngle(centerPoint, signXRadius, house.angle + 15);

			drawText(canvas, (1 + i) + "", house.angle + 15, (float) (pt3.x + pt4.x) / 2, (float) (pt3.y + pt4.y) / 2, mPaint);
		}
		mPaint.setTextSize(15);
	}

	private void drawText(Canvas canvas, String str, double angle, float x, float y, Paint paint) {
		// angle = (angle + deltaAngle + 360) %360;
		// float a = 1;
		// float b = 1;
		// if(angle > 90){
		//
		// }else if(angle > 180){
		// // a = -1;
		// }
		// float w = paint.measureText(str);
		// x += w / 2 * Math.cos(angle) * a;
		// y -= (paint.descent() - paint.ascent()) / 2 * Math.sin(angle) * b;
		canvas.drawText(str, x, y, paint);
	}

	private void drawSigns(Canvas canvas) {
		mPaint.setColor(Color.WHITE);
		canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y, (float) signXRadius, mPaint);
		canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y, (float) signRadius, mPaint);
		canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y, (float) mainRadius, mPaint);
		for (SignBean sign : signList) {
			int c = 0;
			try {
				int r = Integer.parseInt(sign.color.substring(0, 2), 16);
				int g = Integer.parseInt(sign.color.substring(2, 4), 16);
				int b = Integer.parseInt(sign.color.substring(4, 6), 16);
				c = Color.rgb(r, g, b);
				mPaint.setColor(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Point pt1 = getPointByAngle(centerPoint, signRadius, sign.angle);
			Point pt2 = getPointByAngle(centerPoint, mainRadius, sign.angle);
			Point centerPt = getPointByAngle(centerPoint, (signRadius + mainRadius) / 2, sign.angle + 15);
			mPaint.setColor(Color.WHITE);
			canvas.drawLine((float) pt1.x, (float) pt1.y, (float) pt2.x, (float) pt2.y, mPaint);

			symbolPaint.setColor(c);
			drawText(canvas, sign.symbol, sign.angle + 15, (float) centerPt.x, (float) centerPt.y, symbolPaint);
			for (int i = 0; i < 6; i++) {
				double angle = sign.angle + i * 5;
				Point p1 = getPointByAngle(centerPoint, signXRadius, angle);
				Point p2 = getPointByAngle(centerPoint, signRadius, angle);
				canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, mPaint);
			}
		}
	}

	private void drawPlanets(Canvas canvas) {
		mPaint.setColor(Color.WHITE);
		// canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y,
		// (float) planetRadius, mPaint);
		// Log.i("LogUtil", "size = " + planetList.size());
		for (PlanetBean planet : planetList) {
			Log.i("LogUtil", planet.chName + " angle =  " + planet.angle);
			mPaint.setColor(LineColor);
			Point pt1 = getPointByAngle(centerPoint, planetRadius, planet.angle);
			Point pt2 = getPointByAngle(centerPoint, planetRadius * 1.1f, planet.angle);
			canvas.drawLine((float) pt1.x, (float) pt1.y, (float) pt2.x, (float) pt2.y, mPaint);
			int c = 0;
			try {
				int r = Integer.parseInt(planet.color.substring(0, 2), 16);
				int g = Integer.parseInt(planet.color.substring(2, 4), 16);
				int b = Integer.parseInt(planet.color.substring(4, 6), 16);
				c = Color.rgb(r, g, b);
				mPaint.setColor(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
			canvas.drawPoint((float) pt1.x, (float) pt1.y, mPaint);
			Point pt3 = getPointByAngle(centerPoint, planetRadius * 1.2f, planet.angle);
			symbolPaint.setColor(c);
			drawText(canvas, planet.symbol, planet.angle, (float) pt3.x, (float) pt3.y, symbolPaint);
			if (planet.aspects != null) {
				for (Aspect aspect : planet.aspects) {
					if (aspect.isDraw) {
						Point pt4 = getPointByAngle(centerPoint, planetRadius, aspect.planet.angle);
						if (aspect.deltaDegree >= 1) {
							float delta = 2 * (aspect.deltaDegree);
							DashPathEffect dashPath = new DashPathEffect(new float[] { delta, delta }, (float) 1.0);
							mPaint.setPathEffect(dashPath);
						}
						mPaint.setColor(aspect.type.getColor());
						canvas.drawLine((float) pt1.x, (float) pt1.y, (float) pt4.x, (float) pt4.y, mPaint);
						mPaint.setPathEffect(null);
					}
				}
			}
		}
	}

	private Point getPointByAngle(Point pt, double r, double angle) {
		angle += deltaAngle;
		double x = pt.x + r * Math.cos(angle * Math.PI / 180);
		double y = pt.y - r * Math.sin(angle * Math.PI / 180);
		return new Point(x, y);
	}
}
