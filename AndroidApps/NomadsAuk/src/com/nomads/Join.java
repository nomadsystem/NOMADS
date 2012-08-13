// Join.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Join extends Activity {
	NomadsApp app;

	public static GrainTarget gT = GrainTarget.JOIN;

	private NSand sand;
	private NGrain grain;

	TextView joinStatus;
	Button connect;
	
	final Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("Join", "onCreate()");
		super.onCreate(savedInstanceState);

		// get reference to NomadsApp singleton
		app = (NomadsApp) this.getApplicationContext();

		// send reference of Join to NomadsApp
		app.setJoin(this);

		// create new sand object in NomadsApp
		app.newSand();

		// get NSand instance from Join
		sand = app.getSand();

		// connect via asynctask--if successful:
		// NomadsApp.connectStatus is set to true; else false
		// register byte is sent to Nomads server from Join
		// startThread() is called in NomadsApp
		// goToSwarm() is called in Join
		sand.new Connect().execute(this, app);

		Log.d("Join", "app.isConnected() = " + app.isConnected());

		// Setup UI
		setContentView(R.layout.join);
		joinStatus = (TextView) findViewById(R.id.joinStatus);
		connect = (Button) findViewById(R.id.connectButton);
		connect.setOnClickListener(connectButtonListener);
	}

	// ========================================================
	// Network methods
	// ========================================================

	public void register() {
		Log.d("NomadsApp",
				"register() -> connectionStatus is: " + app.isConnected());

		if (!app.isConnected()) {
			Log.e("NomadsApp",
					"Register failed because connectionStatus is false");
			return;
		}

		// Send the register byte to the Nomads server
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sand.sendGrain(NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER,
				NDataType.BYTE, 1, registerByte);
	}

	public void parseGrain(NGrain _grain) {
		Log.d("Join", "parseGrain(): grain received");

		if (grain == null) {
			Log.d("Join", "parseGrain(): grain is null");
			return;
		}

		grain = _grain;

		if (grain != null)
			grain = null;
	}

	// ========================================================
	// Refresh connection button
	// ========================================================
	Button.OnClickListener connectButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			sand.new Connect().execute(Join.this, app);
		}
	};

	// ========================================================

	@Override
	public void onResume() {
		super.onResume();
		Log.d("Join", "is resumed");
		gT = GrainTarget.JOIN;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("Join", "is paused");
		// stopThread();
	}

	public void goToSwarm() {
		// Switch to Swarm activity
		Intent intent = new Intent(getApplicationContext(), Swarm.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		quitAlert();
	}
	
	protected void quitAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		// need to create new input field each time

		alert.setTitle("Really quit?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				app.setConnectionStatus(false);
				app.getSand().closeConnection();
				sand = null;
				app.finishAll();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}
}