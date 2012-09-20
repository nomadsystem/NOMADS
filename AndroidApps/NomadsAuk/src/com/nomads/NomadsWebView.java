package com.nomads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class NomadsWebView extends Activity {
	WebView webpage;
	Button backButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webpage);
		backButton = (Button) findViewById (R.id.webBackButton);
		backButton.setOnClickListener(backListener);
		webpage = (WebView) findViewById (R.id.nomadsWebView);
		webpage.loadUrl("http://nomads.music.virginia.edu/moreinfo.html");
	}
	
	 Button.OnClickListener backListener = new Button.OnClickListener(){
		 @Override
		 public void onClick(View v) {
			 finish();
		 }
	 };
}
