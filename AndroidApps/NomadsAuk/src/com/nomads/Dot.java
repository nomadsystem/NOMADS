// Dot.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
import nomads.v210.NSand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class Dot extends View {
	private static final float RADIUS = 20;
	private float[] xy = new float[2];
	private float[] xyNorm = new float[2];
	private int[] xyInt = new int[2];
	private Paint myPaint;
	// private Paint backgroundPaint;

	private NomadsApp app;
	private NSand sand;

	// private NGrain grain;

	public Dot(Context context, AttributeSet attrs) {
		super(context, attrs);

		app = (NomadsApp) context.getApplicationContext();

		// get NSand instance from Join
		sand = app.getSand();

		// starting position of dot
		xy[0] = (float) (getWidth() * 0.5);
		xy[1] = (float) (getHeight() * 0.5);

		// backgroundPaint = new Paint();
		// backgroundPaint.setColor(Color.BLUE);

		myPaint = new Paint();
		myPaint.setColor(Color.WHITE);
		myPaint.setAntiAlias(true);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			app.cancelAllTextInput();
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// clip touch coordinates to view dimensions
			if (event.getX() >= 0.0 && event.getX() <= this.getWidth())
				xy[0] = event.getX();
			if (event.getY() >= 0.0 && event.getY() <= this.getHeight())
				xy[1] = event.getY();
			xyNorm[0] = xy[0] / this.getWidth();
			xyNorm[1] = xy[1] / this.getHeight();
			
			// View screen size
//			Log.d("Dot", "getWidth(): " + this.getWidth() + "getHeight(): " + this.getHeight());
			
			app.setXY(xyNorm);
			
			xyInt[0] = (int) (xyNorm[0] * 1000.0f);
			xyInt[1] = (int) (xyNorm[1] * 1000.0f);

			// send position of dot to server
			sand.sendGrain(
					NAppIDAuk.OC_POINTER,
					NCommandAuk.SEND_SPRITE_XY,
					NDataType.INT32,
					2,
					xyInt);
			break;
		}
		return (true);
	}

	public void draw(Canvas c) {
		// draw the dot
		// int width = canvas.getWidth();
		// int height = canvas.getHeight();
		// canvas.drawRect(0, 0, width, height, backgroundPaint);
		c.drawCircle(xy[0], xy[1], RADIUS, myPaint);

		// need to invalidate in custom view class only
		invalidate();
	}
}
