// Settings.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NGrain;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
//import android.net.Uri;
import android.os.Bundle;
//import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends Activity {
	NomadsApp app;

	TextView connectionStatus, nomadsLink;
	Button linkButton, quitButton;
	String connectedMessage;
	final Context context = this;
	AlertDialog.Builder alert;
	private NGrain grain;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Settings", "onCreate()");
		super.onCreate(savedInstanceState);

//		app = (NomadsApp) getApplicationContext();
		app = NomadsApp.getInstance();

		// send reference of Swarm to NomadsApp
		app.setSettings(this);

		setContentView(R.layout.settings);
		connectionStatus = (TextView) findViewById(R.id.connectionStatus);
		linkButton = (Button)findViewById(R.id.linkButton);
		linkButton.setOnClickListener(linkListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("Settings", "is resumed");
		
		// inform user of connection status
		setConnectedMessage(app.isConnected());
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("Settings", "is paused");
		// stopThread();
	}
	
	// ========================================================

	public void setConnectedMessage(boolean _connected) {
		if (_connected) {
			connectionStatus.setText("Connected to the Nomads server");
		} else {
			connectionStatus.setText("Not connected to the Nomads server.");
		}
	}

	// ========================================================
	// Button Listeners
	// ========================================================

	 Button.OnClickListener linkListener = new Button.OnClickListener(){
		 @Override
		 public void onClick(View v) {
			 // use this to open web page in another browser (can also use text link in Strings.xml)
			 // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nomads.music.virginia.edu"));
			 Intent intent = new Intent(getApplicationContext(), NomadsWebView.class);
			 startActivity(intent);
		 }
	 };

	// ========================================================
	// Network
	// ========================================================

	public void parseGrain(NGrain _grain) {
		Log.d("Settings", "parseGrain(): grain received");

		if (grain == null) {
			Log.d("Settings", "parseGrain(): grain is null");
			return;
		}
		grain = _grain;

		// String msg = new String(grain.bArray);
		// Log.i("Swarm", msg);
		//
		if (grain != null)
			grain = null;
	}
}