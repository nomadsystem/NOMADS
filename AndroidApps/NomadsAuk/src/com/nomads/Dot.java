// Dot.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
//import nomads.v210.NGrain;
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
	private Paint myPaint;
//	private Paint backgroundPaint;

	private NomadsApp app;
	private NSand sand;
//	private NGrain grain;

	public Dot(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		app = (NomadsApp)context.getApplicationContext();
		
		// get NSand instance from Join
		sand = app.getSand();
		
		// starting position of dot; should be a normalized float
		xy[0] = 30;
		xy[1] = 30;

//		backgroundPaint = new Paint();
//		backgroundPaint.setColor(Color.BLUE);

		myPaint = new Paint();
		myPaint.setColor(Color.WHITE);
		myPaint.setAntiAlias(true);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				xy[0] = event.getX();
				xy[1] = event.getY();
				
				// send position of dot to server
				sand.sendGrain(
						NAppIDAuk.OC_POINTER,
						NCommandAuk.SEND_SPRITE_XY,
						NDataType.FLOAT32,
						2,
						xy);
				break;
		}
		return (true);
	}

	public void draw(Canvas c) {	
		// draw the dot
//		int width = canvas.getWidth();
//		int height = canvas.getHeight();
//		canvas.drawRect(0, 0, width, height, backgroundPaint);
		c.drawCircle(xy[0], xy[1], RADIUS, myPaint);
		
		// need to invalidate in custom view class only
		invalidate();
	}
}
