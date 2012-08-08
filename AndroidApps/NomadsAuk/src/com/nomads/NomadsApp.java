package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

public class NomadsApp extends Application
{
	private static NomadsApp singleton;
	private Join join = null;
	private Swarm swarm = null;
	private Settings settings = null;
	public static GrainTarget gT = GrainTarget.JOIN;
	NSand sand;
	NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	private boolean connectionStatus = false;
	private boolean active = true;
	
	public NomadsApp getInstance ()
	{
		return singleton;
	}

	@Override
	public void onCreate() {
		Log.i("NomadsApp", "onCreate()");
		
		super.onCreate();
		
		singleton = this;
		
		//Create, initialize, load the sound manager
        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();

		// create NSand instance
		sand = new NSand();
	}
	
	//========================================================
	// Getters / Setters
	//========================================================
	
	public void setConnectionStatus (boolean _connected)
	{
		connectionStatus = _connected;
	}
	
	public boolean isConnected ()
	{
		return connectionStatus;
	}
	
	public void setJoin(Join _j)
	{
		join = _j;
	}
	
	public void setSwarm(Swarm _sw)
	{
		swarm = _sw;
	}
	
	public void setSettings(Settings _set)
	{
		settings = _set;
	}
	
	public void setAppState(boolean state)
	{
		Log.i("NomadsApp", "Thread state set to: " + state);
	    active = state;
	}
	
	public boolean getAppState()
	{
	    return active;
	}
	
	
	public void setGrainTarget(GrainTarget _target)
	{
		Log.i("NomadsApp", "setSandTarget()");
		gT = _target;
	}
	
	public NSand getSand()
	{
		return sand;
	}
	
	//========================================================
	
	private void routeGrain(GrainTarget _target)
	{
		Log.i("Join", "routeGrain(): Current target: " + _target);
		
		if (_target == GrainTarget.JOIN && join != null)
			join.parseGrain(grain);
		
		else if (_target == GrainTarget.SWARM && swarm != null)
			swarm.parseGrain(grain);
		
		else if (_target == GrainTarget.SETTINGS && settings != null)
			settings.parseGrain(grain);
		
		else
			Log.i("Join", "invalid grain target");
	}
	
	//========================================================
	// Thread Helpers
	//========================================================
	
	public synchronized void startThread()
	{
		if(nThread == null)
		{
			nThread = new NomadsAppThread();
			nThread.start();
			Log.i("NomadsApp", "Thread started.");
		}
		else
		{
			Log.i("NomadsApp", "startThread: thread != null.");
		}
	}

	public synchronized void stopThread()
	{
		if(nThread != null)
		{
			setAppState(false);
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("NomadsApp", "NomadsAppThread stopped.");
			sand.new Close().execute();
			Log.i("NomadsApp", "sand.close()");
		}
		else
		{
			Log.i("NomadsApp", "stopThread: thread == null.");
		}
	}
	
	//========================================================
	// Connection Thread
	//========================================================
	
	private class NomadsAppThread extends Thread
	{
		public NomadsAppThread()
		{
			System.out.println("appState: " + getAppState());
		}
		
		public void run()
		{			
			while ( getAppState() )
			{
//				Log.i( "NomadsApp->Thread", "getThreadState() = " + getAppState() );
				try{
					grain = sand.getGrain();
					grain.print(); //prints grain data to console
					handle.post(updateUI);
				} catch (NullPointerException npe) {
					Log.i("NomadsApp -> Thread", "run() -> socket == null; exiting.");
					handle.post(exitThread);
				}
			}
		}
		
		final Runnable updateUI = new Runnable()
		{
	    	@Override
	        public void run()
	    	{
	    		routeGrain(gT);
	        }
	    };
	    
	    final Runnable exitThread = new Runnable()
		{
	    	@Override
	        public void run()
	    	{
				stopThread();
				
				// Switch to Join activity
				Intent intent = new Intent(getApplicationContext(), Join.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
	        }
	    };
	}
	
	//========================================================
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
