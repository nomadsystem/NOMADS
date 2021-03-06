// Settings.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NGrain;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends Activity {
	NomadsApp app;

	TextView connectionStatus, nomadsLink;
	Button linkButton, backButton;
	String connectedMessage;
	final Context context = this;
	AlertDialog.Builder alert;
	private NGrain grain;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		Log.i("Settings", "onCreate()");
		super.onCreate(savedInstanceState);

		app = NomadsApp.getInstance();

		// send reference of Swarm to NomadsApp
		app.setSettings(this);

		setContentView(R.layout.settings);
		connectionStatus = (TextView) findViewById(R.id.connectionStatus);
		linkButton = (Button)findViewById(R.id.linkButton);
		linkButton.setOnClickListener(linkListener);
		backButton = (Button)findViewById(R.id.settingsBackButton);
		backButton.setOnClickListener(backListener);
	}

	@Override
	public void onResume() {
		super.onResume();
//		Log.i("Settings", "is resumed");
		
		// inform user of connection status
		setConnectedMessage(app.isConnected());
	}

	@Override
	public void onPause() {
		super.onPause();
//		Log.i("Settings", "is paused");
	}
	
	// ========================================================

	public void setConnectedMessage(boolean _connected) {
		if (_connected) {
			connectionStatus.setText("Connected to the NOMADS!");
		} else {
			connectionStatus.setText("Not connected to the NOMADS.");
		}
	}

	// ========================================================
	// Button Listeners
	// ========================================================

	 Button.OnClickListener linkListener = new Button.OnClickListener(){
		 @Override
		 public void onClick(View v) {
			 // use this to open web page in another browser (can also use text link in Strings.xml)
			 Intent intent = new Intent(getApplicationContext(), NomadsWebView.class);
			 startActivity(intent);
		 }
	 };
	 
	 Button.OnClickListener backListener = new Button.OnClickListener(){
		 @Override
		 public void onClick(View v) {
			 finish();
		 }
	 };

	// ========================================================
	// Network
	// ========================================================

	public void parseGrain(NGrain _grain) {
//		Log.d("Settings", "parseGrain(): grain received");

		if (grain == null) {
//			Log.d("Settings", "parseGrain(): grain is null");
			return;
		}
		grain = _grain;

		if (grain != null)
			grain = null;
	}
}