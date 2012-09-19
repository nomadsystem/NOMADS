package com.nomads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollViewNoTouch extends ScrollView {
	public ScrollViewNoTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// pass all touches
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}
	
	// intercept touches from children
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}
