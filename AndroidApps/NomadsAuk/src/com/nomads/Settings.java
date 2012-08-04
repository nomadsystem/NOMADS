package com.nomads;

//import nomads.v210.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Settings extends Activity {
	Activity currentTarget;
//	Join join;
//	NSand sand;
//	private NGrain grain;
//	private NomadsAppThread nThread;
//	final Handler handle = new Handler();
	
	TextView settings, connectionStatus;
//	Button buttonConnect, buttonDisconnect;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		// set NSand target to this
		currentTarget = this;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		settings = (TextView)findViewById(R.id.settings);
		connectionStatus = (TextView)findViewById(R.id.connectionStatus);
	}
	
	//========================================================
	// Buttons
	//========================================================
	
//	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){
//		@Override
//		public void onClick(View v) {
//
//		}
//	};
	
	//========================================================
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Join", "is resumed");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Join", "is paused");
//		stopThread();
	}
}