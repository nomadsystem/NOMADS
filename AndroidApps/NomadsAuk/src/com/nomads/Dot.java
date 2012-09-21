// Dot.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
import nomads.v210.NGrain;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Dot extends SurfaceView implements SurfaceHolder.Callback {
	private static final float RADIUS = 30;
	private static final float RADIUS_MAX = 90;
	private float radCurrent = RADIUS;
	private boolean dropGrow, dropShrink;
	private float[] xy = new float[2];
	private float[] xyNorm = new float[2];
	private int[] xyInt = new int[2];
	private Paint myPaint;

	private NomadsApp app;
	private NGrain sGrain;
	private DotThread dThread;
	private boolean running = false;
	private Bitmap background, scaledBG;

	public Dot(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
		getHolder().addCallback(this);
		dThread = new DotThread(getHolder(), this);

		app = NomadsApp.getInstance();
		
		// send reference of Swarm to NomadsApp
		app.setDot(this);

		myPaint = new Paint();
		// cc (204) for red. blue. alpha; 0 for green
		myPaint.setColor(0xCCCC00CC);
		myPaint.setAntiAlias(true);
	}
	
	public void surfaceThreadRestart () {
		if (!running) {
//			Log.i("Dot", "surfaceThreadStart()");
			running = true;
			dThread = new DotThread(getHolder(), this);
		}
	}
	
	public void surfaceThreadStop () {
//		Log.i("Dot", "surfaceThreadStop()");
		if (running) {
			boolean retry = true;
	        running = false;
	        while (retry) {
	            try {
	            	dThread.join();
	                retry = false;
	            } catch (InterruptedException e) {
	                // we will try it again and again...
	            }
	        }
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
	    scaledBG = Bitmap.createScaledBitmap(background, this.getWidth(), this.getHeight(), true);
	    running = true;
        dThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceThreadStop();
	}
	
	@Override
    public void onDraw(Canvas canvas) {
		float tempRad = getAnimatedRadius();
		
		if (running) {
			// draw the background
			canvas.drawBitmap(scaledBG, 0, 0, null);
			
			if (app.state().pointerIsVisible) {
				canvas.drawCircle(xy[0], xy[1], tempRad, myPaint);
			}
		}
    }
	
	class DotThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private Dot dot;
 
        public DotThread(SurfaceHolder _surfaceHolder, Dot _dot) {
            surfaceHolder = _surfaceHolder;
            dot = _dot;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (running) {
                c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        dot.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                    	surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

	// get dimensions of parent
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    
	    // center the starting position of dot
 		xy[0] = (float) (parentWidth * 0.5);
 		xy[1] = (float) (parentHeight * 0.5);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		
		// View screen size
//		Log.d("Dot", "getWidth(): " + this.getWidth() + "getHeight(): " + this.getHeight());
		
		// clip touch coordinates to view dimensions
			xy[0] = event.getX();
			xy[1] = event.getY();
		xyNorm[0] = xy[0] / this.getWidth();
		xyNorm[1] = xy[1] / this.getHeight();
		
		switch (action) {
			case MotionEvent.ACTION_DOWN:
//				Log.i("Dot", "ACTION_DOWN");
				app.setXY_td(xyNorm);
//				Log.i("Dot", "app.setXY_td(): Y value: " + xyNorm[1]);
				app.setTouchDown(true);
				break;
				
			case MotionEvent.ACTION_MOVE:
//				Log.i("Dot", "ACTION_MOVE");		
				app.setXY(xyNorm);
				
				xyInt[0] = (int) (xyNorm[0] * 1000.0f);
				xyInt[1] = (int) (xyNorm[1] * 1000.0f);
	
				// send position of dot to server
				sGrain = new NGrain(
						NAppIDAuk.OC_POINTER,
						NCommandAuk.SEND_SPRITE_XY,
						NDataType.INT32,
						2,
						xyInt);
    			app.sendGrain(sGrain);
				break;
				
			case MotionEvent.ACTION_UP:
//				Log.i("Dot", "ACTION_UP");
			case MotionEvent.ACTION_CANCEL:
//				Log.i("Dot", "ACTION_CANCEL");
				app.setTouchDown(false);
			default:
					break;
		}
		return (true);
	}
	
	public float getAnimatedRadius () {
		if (dropGrow) {
			if (radCurrent <= RADIUS_MAX) {
				radCurrent += 15;
			} else {
				dropGrow = false;
				dropShrink = true;
			}
		}
		if (dropShrink) {
			if (RADIUS < radCurrent) {
				radCurrent -= 3;
			} else {
				dropShrink = false;
			}
		}
		return radCurrent;
	}
	
	public void animateGrow () {
		dropGrow = true;
	}
}
