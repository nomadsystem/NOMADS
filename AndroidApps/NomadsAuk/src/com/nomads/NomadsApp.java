package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NomadsApp extends Application
{
	private static NomadsApp singleton;
	private Join join;
	private Swarm swarm;
	private Settings settings;
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
		
		tryConnect();
	}
	
	//========================================================
	// Network methods
	//========================================================
	
	public boolean isConnected ()
	{
		return connectionStatus;
	}
	
	public void tryConnect ()
	{
		if (!sand.connect())
		{
			Log.i("NomadsApp", "Connect failed");
			connectionStatus = false;
			return;
		}
		startThread();
		
		connectionStatus = true;
		
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sand.sendGrain( NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER, NDataType.UINT8, 1, registerByte );
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
	    // note: you can also store this in SharedPreferences
	}
	
	public boolean getAppState()
	{
	    return active;
	    // note: you can also load this from SharedPreferences
	}
	
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
			sand.close();
			Log.i("NomadsApp", "sand.close()");
		}
		else
		{
			Log.i("NomadsApp", "stopThread: thread == null.");
		}
	}
	
//	public synchronized void threadRunLoop(boolean _running)
//	{
//		nThread.runLoop(_running);
//	}
	
	private void routeGrain(GrainTarget _target)
	{
		Log.i("Join", "routeGrain(): Current target: " + _target);
		
		if (_target == GrainTarget.JOIN) join.parseGrain(grain);
		
		else if (_target == GrainTarget.SWARM) swarm.parseGrain(grain);
		
		else if (_target == GrainTarget.SETTINGS) settings.parseGrain(grain);
		
		else Log.i("Join", "invalid grain target");
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
	// Connection Thread
	//========================================================
	private class NomadsAppThread extends Thread
	{
//		NomadsApp a;

//		public NomadsAppThread(NomadsApp _a)
		public NomadsAppThread()
		{
//			a = _a;
			System.out.println("appState: " + getAppState());
		}
		
		public void run()
		{			
			while ( getAppState() )
			{
				Log.i("NomadsApp->Thread", "getThreadState() = " + getAppState());
				try{
					grain = sand.getGrain();
					grain.print(); //prints grain data to console
					handle.post(updateUI);
				} catch (NullPointerException npe) {
					Log.i("NomadsApp > NomadsAppThread", "NullPointerException");
				}
			}
		}
		
		final Runnable updateUI = new Runnable()
		{
	    	@Override
	        public void run()
	    	{
//				a.routeGrain(gT);
	    		routeGrain(gT);
	        }
	    };
	}
	
	//========================================================
	// Refresh connection button
	//========================================================
	Button.OnClickListener connectButtonListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			tryConnect();
		}
	};
	
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
