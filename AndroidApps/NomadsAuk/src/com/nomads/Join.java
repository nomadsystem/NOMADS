// Join.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Activity;
import android.content.Intent;
//import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Join extends Activity
{
	NomadsApp app;
	
	public static GrainTarget gT = GrainTarget.JOIN;
	
//	private NSand sand;
	private NGrain grain;
	
	TextView joinStatus;
	Button connect;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i("Join", "onCreate()");
		super.onCreate(savedInstanceState);
		
		app = (NomadsApp)this.getApplicationContext();
		
		// send reference of Join to NomadsApp
		app.setJoin(this);
				
		setContentView(R.layout.join);
		joinStatus = (TextView)findViewById(R.id.joinStatus);
		connect = (Button)findViewById(R.id.connectButton);
		connect.setOnClickListener(connectButtonListener);
		
		// get NSand instance from Join
//		sand = app.getSand();
		
		if( app.isConnected() )
		{
			// Switch to Swarm activity
			Intent intent = new Intent(getApplicationContext(), Swarm.class);
			startActivity(intent);
		}
		else
		{
			Log.i("Join", "onCreate() -> NomadsApp is not connected");
		}
	}
	
	//========================================================
	// Network methods
	//========================================================
	
	public void parseGrain(NGrain _grain)
	{
		grain = _grain;
		
		if (grain.appID == NAppIDAuk.CONDUCTOR_PANEL){
			Log.i("Join", "addID == NAppIDAuk.CONDUCTOR_PANEL");
		}
		
		if (grain != null)
			grain = null;
	}
	
	//========================================================
	// Refresh connection button
	//========================================================
	Button.OnClickListener connectButtonListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			app.tryConnect();
			
			if( app.isConnected() )
			{
				// Switch to Swarm activity
				Intent intent = new Intent(getApplicationContext(), Swarm.class);
				startActivity(intent);
			}
		}
	};
	
	//========================================================
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.i("Join", "is resumed");
		gT = GrainTarget.JOIN;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Log.i("Join", "is paused");
//		stopThread();
	}
}