package com.nomads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollViewNoTouch extends ScrollView {
	public ScrollViewNoTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}
}
