// NomadsApp.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

import nomads.v210.NGlobals.GrainTarget;
import nomads.v210.NGrain;
import nomads.v210.NSand;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

public class NomadsApp extends Application {
	private static NomadsApp singleton;
	AudioManager am;
	private Join join;
	private Swarm swarm;
	private Settings settings;
	private GrainTarget gT;
	private NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	private boolean connectionStatus = false;
	private float[] xy;

	public NomadsApp getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		Log.i("NomadsApp", "onCreate()");

		super.onCreate();

		singleton = this;

		am = (AudioManager) getBaseContext().getSystemService(
				Context.AUDIO_SERVICE);
		
		// initialize xy coordinates in case of sound before any touches
		xy = new float[2];
		xy[0] = 0.5f;
		xy[1] = 0.5f;
	}

	// ========================================================
	// Getters / Setters
	// ========================================================

	public void setJoin(Join _j) {
		join = _j;
	}

	public void setSwarm(Swarm _sw) {
		swarm = _sw;
	}

	public void setSettings(Settings _set) {
		settings = _set;
	}

	public void setConnectionStatus(boolean _connected) {
		connectionStatus = _connected;
	}

	public boolean isConnected() {
		return connectionStatus;
	}

//	public void setAppState(boolean _state) {
//		Log.i("NomadsApp", "Thread state set to: " + _state);
//		appState = _state;
//	}
//
//	// checked by NomadsAppThread run loop
//	public boolean getAppState() {
//		return appState;
//	}

	public void setGrainTarget(GrainTarget _target) {
		Log.i("NomadsApp", "setGrainTarget(): " + _target);
		gT = _target;
	}

	public void phoneRingerState(boolean on) {
		if (on) {
			// set device ringer to normal mode
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		} else {
			// set device ringer to silent mode
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}

		Log.d("NomadsApp", "ringer state was set to: " + on);
	}
	
	public void setXY(float[] _xy) {
		xy = _xy;
	}
	
	public float[] getXY() {
		return xy;
	}

	// ========================================================
	
	public void cancelAllTextInput() {
		if (swarm != null) {
			swarm.cancelAllTextInput();
		}
	}

	public NSand getSand() {
		return sand;
	}

	public void newSand() {
		if (sand != null) {
			sand.closeConnection();
			sand = null;
		}

		// create NSand instance
		sand = new NSand();
	}

	private void routeGrain(GrainTarget _target) {
		Log.d("NomadsApp", "routeGrain() to target: " + _target);

		if (grain != null) {
			if (_target == GrainTarget.JOIN && join != null)
				join.parseGrain(grain);

			else if (_target == GrainTarget.SWARM && swarm != null)
				swarm.parseGrain(grain);

			else if (_target == GrainTarget.SETTINGS && settings != null)
				settings.parseGrain(grain);

			else
				Log.e("NomadsApp", "invalid grain target");
		} else
			Log.e("NomadsApp", "routeGrain(): grain is null");
	}

	// ========================================================
	// Thread Helpers
	// ========================================================

	public synchronized void startThread() {
		if (nThread == null) {
			nThread = new NomadsAppThread();
			nThread.start();
			Log.i("NomadsApp", "Thread started.");
		} else {
			Log.i("NomadsApp", "startThread: thread != null.");
		}
	}

	private synchronized void removeThread() {
		if (nThread != null) {
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("NomadsApp", "NomadsAppThread stopped.");
			// sand.close();
			// sand.new Close().execute();
			// Log.i("NomadsApp", "sand.close()");
		} else {
			Log.i("NomadsApp", "stopThread: thread == null.");
		}
	}

	// ========================================================
	// Connection Thread
	// ========================================================

	private class NomadsAppThread extends Thread {
		public NomadsAppThread() {
			System.out.println("appState: " + isConnected());
		}

		public void run() {
			while (isConnected()) {
				// Log.i( "NomadsApp->Thread", "getThreadState() = " +
				// getAppState() );
				try {
					grain = sand.getGrain();
					grain.print(); // prints grain data to console
					handle.post(updateUI);
				} catch (NullPointerException npe) {
					Log.i("NomadsApp -> Thread",
							"run() -> grain == null; exiting.");
					setConnectionStatus(false);
//					grain = null;
					if (settings != null) {
						settings.finish();
					}
					if (swarm != null) {
						swarm.finish();
					}
					removeThread();
				}
			}
		}

		final Runnable updateUI = new Runnable() {
			@Override
			public void run() {
				routeGrain(gT);
			}
		};
	}

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

	public void finishAll() {
		removeThread();

		// return phone ringer to normal
		phoneRingerState(true);

		if (settings != null) {
			settings.finish();
			Log.i("NomadsApp", "Settings Activity finished.");
		}
		
		if (swarm != null) {
			swarm.finish();
			Log.i("NomadsApp", "Swarm Activity finished.");
		}
		
		if (join != null) {
			join.finish();
			Log.i("NomadsApp", "Join Activity finished.");
		}
	}
}
