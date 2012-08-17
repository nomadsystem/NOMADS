package com.nomads;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class NomadsWebView extends Activity {
	WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		webview = new WebView(this);
		setContentView(webview);
		webview.loadUrl("http://nomads.music.virginia.edu");
	}
}
