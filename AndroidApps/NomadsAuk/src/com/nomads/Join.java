// Join.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Join extends Activity
{
	// setup static instance reference
	public static Join instance;
	public static GrainTarget gT = GrainTarget.JOIN;
	
	NSand sand;
	NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	private boolean connectionStatus = false;
	
	TextView joinStatus;
	Button test;
	
	int[] xy = new int[2];
	
	// TESTING
	Button.OnClickListener testListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			stopThread();
		}
	};
	
	@Override
	 public void onCreate(Bundle savedInstanceState)
	{
		Log.i("Join", "onCreate()");
		
		// set static reference
		instance = this;
		
		// Connect
		tryConnect();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		joinStatus = (TextView)findViewById(R.id.joinStatus);
		test = (Button)findViewById(R.id.test);
		test.setOnClickListener(testListener);
	}
	
	//========================================================
	// Network methods
	//========================================================
	
	public boolean isConnected ()
	{
		return connectionStatus;
	}
	
	void tryConnect ()
	{
		sand = new NSand();
		if (!sand.connect())
		{
			Log.i("Join", "Connect failed");
			connectionStatus = false;
			return;
		}
		startThread();
		
		connectionStatus = true;
		
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sand.sendGrain( NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER, NDataType.UINT8, 1, registerByte );
		
		// Switch to Swarm activity
		Intent intent = new Intent(getApplicationContext(), Swarm.class);
		startActivity(intent);
		finish();
	}
	
	private class NomadsAppThread extends Thread
	{
		Join j;
		boolean active = true;

		public NomadsAppThread(Join _j)
		{
			j = _j;
		}
		
		public void kill()
		{
			active = false;
			Log.i("Join > NomadsAppThread", "active = false");
		}
		
		public void run()
		{			
			while (active)
			{
				try{
					grain = sand.getGrain();
					grain.print(); //prints grain data to console
					handle.post(updateUI);
				} catch (NullPointerException npe) {
					Log.i("Join > NomadsAppThread", "NullPointerException");
				}
			}
		}
		
		final Runnable updateUI = new Runnable()
		{
	    	@Override
	        public void run()
	    	{
				j.routeGrain(gT);
	        }
	    };
	}
	
	public synchronized void startThread()
	{
		if(nThread == null)
		{
			nThread = new NomadsAppThread(this);
			nThread.start();
			Log.i("Join", "Thread started.");
		}
		else
		{
			Log.i("Join", "startThread: thread != null.");
		}
	}

	public synchronized void stopThread()
	{
		if(nThread != null)
		{
			nThread.kill();
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("Join", "NomadsAppThread stopped.");
			sand.close();
			Log.i("Join", "sand.close()");
		}
		else
		{
			Log.i("Join", "stopThread: thread == null.");
		}
	}
	
	private void routeGrain(GrainTarget _target)
	{
		Log.i("Join", "routeGrain(): Current target: " + _target);
		
		if (_target == GrainTarget.JOIN) parseGrain(grain);
		
		else if (_target == GrainTarget.SWARM) Swarm.instance.parseGrain(grain);
		
		else Log.i("Join", "invalid grain target");
	}
	
	// delete?
	private void parseGrain(NGrain _grain)
	{
		if (grain.appID == NAppIDAuk.CONDUCTOR_PANEL){
			Log.i("Join", "addID == NAppIDAuk.CONDUCTOR_PANEL");
		}
	}
	
	public void setGrainTarget(GrainTarget _target)
	{
		Log.i("Join", "setSandTarget()");
		gT = _target;
	}
	
	public NSand getSand()
	{
		return sand;
	}
	
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