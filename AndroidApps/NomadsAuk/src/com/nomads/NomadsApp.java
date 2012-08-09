// NomadsApp.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Application;
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
	private NSand sand;
	NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	private boolean connectionStatus = false;
	private boolean appState = true;
	
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

	}
	
	//========================================================
	// Getters / Setters
	//========================================================
	
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
	
	public void setConnectionStatus (boolean _connected)
	{
		connectionStatus = _connected;
	}
	
	public boolean isConnected ()
	{
		return connectionStatus;
	}
	
	public void setAppState(boolean _state)
	{
		Log.i("NomadsApp", "Thread state set to: " + _state);
	    appState = _state;
	}
	
	// checked by NomadsAppThread run loop
	public boolean getAppState()
	{
	    return appState;
	}
	
	
	public void setGrainTarget(GrainTarget _target)
	{
		Log.i("NomadsApp", "setSandTarget()");
		gT = _target;
	}
	
	//========================================================
	
	public NSand getSand()
	{
		return sand;
	}
	
	public void newSand()
	{
		if (sand != null)
		{
			sand.closeConnection();
			sand = null;
		}
		
		// create NSand instance
		sand = new NSand();
	}
	
	private void routeGrain(GrainTarget _target)
	{
		Log.d("Join", "routeGrain(): Current target: " + _target);
		
		if (grain != null)
		{
			if (_target == GrainTarget.JOIN && join != null)
				join.parseGrain(grain);
			
			else if (_target == GrainTarget.SWARM && swarm != null)
				swarm.parseGrain(grain);
			
			else if (_target == GrainTarget.SETTINGS && settings != null)
				settings.parseGrain(grain);
			
			else
				Log.e("Join", "invalid grain target");
		}
		else
			Log.e("Join", "routeGrain(): grain is null");
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

	private synchronized void removeThread()
	{
		if(nThread != null)
		{
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("NomadsApp", "NomadsAppThread stopped.");
//			sand.close();
//			sand.new Close().execute();
//			Log.i("NomadsApp", "sand.close()");
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
					grain = null;
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
	
	public void finishAll()
	{
		removeThread();
		if (settings != null)
			settings.finish();
		if (swarm != null)
			swarm.finish();
		if (join != null)
			join.finish();
	}
}
