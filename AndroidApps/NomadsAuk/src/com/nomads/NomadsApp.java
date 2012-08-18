// NomadsApp.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.08

package com.nomads;

//import nomads.v210.NGlobals.GrainTarget;
import nomads.v210.NGrain;
import nomads.v210.NSand;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NomadsApp extends Application {
	private static NomadsApp singleton;
	AudioManager am;
	private Join join;
	private Swarm swarm;
	private Settings settings;
//	private GrainTarget gT;
	private NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
//	final Handler handle = new Handler();
	private boolean connectionStatus = false;
	private boolean touchDown = false;
	private float[] xy, xytd;

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
		xytd = new float [2];
		xy[0] = 0.5f;
		xy[1] = 0.5f;
		xytd[0] = 0.5f;
		xytd[1] = 0.5f;
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
		if (connectionStatus) {
			startThread();
		}
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

//	public void setGrainTarget(GrainTarget _target) {
//		Log.i("NomadsApp", "setGrainTarget(): " + _target);
//		gT = _target;
//	}

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
	
	// set xy position
	public void setXY(float[] _xy) {
		System.arraycopy(_xy, 0, xy, 0, _xy.length);
	}
	
	public float[] getXY() {
		return xy;
	}
	
	// set xy position on touch down
	public void setXY_td(float[] _xytd) {
//		for (int i=0; i<_xytd.length; i++) {
//			xytd[i] = _xytd[i];
//		}
		System.arraycopy(_xytd, 0, xytd, 0, _xytd.length);
		Log.d("NomadsApp", "xytd[1] set to: " + xytd[1]);
	}
	
	public float[] getXY_td() {
		Log.d("NomadsApp", "getXY_td(): Y value returned: " + xytd[1]);
		return xytd;
	}
	
	public void setTouchDown (boolean _td) {
		touchDown = _td;
		if (touchDown) {
			cancelAllTextInput();
			Log.i("NomadsApp", "Touch is down.");
			swarm.pointerStatus(true);
		} else {
			Log.i("NomadsApp", "Touch is up.");
			swarm.pointerStatus(false);
		}
	}
	
	public boolean pointerIsTouching () {
		return touchDown;
	}

	// ========================================================
	// AUDIO
	// ========================================================
	
	// audio focus can only be requested in API 8 (Froyo) or higher
	@TargetApi(8)
	public int requestAudioFocus () {
		// Request audio focus for playback
		int result = am.requestAudioFocus(afChangeListener,
		                                 // Use the music stream.
		                                 AudioManager.STREAM_MUSIC,
		                                 // Request permanent focus.
		                                 AudioManager.AUDIOFOCUS_GAIN);
		return result;
	}
	
	@TargetApi(8)
	public void releaseAudioFocus () {
		// Abandon audio focus
		am.abandonAudioFocus(afChangeListener);
	}
	
	// currently does nothing when other apps request audio focus
	// audio focus is abandoned in the Swarm onPause() method
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
	    public void onAudioFocusChange(int focusChange) {
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	            // Pause playback
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	            // Resume playback 
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//	            am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
//	            am.abandonAudioFocus(afChangeListener);
	            // Stop playback
	        }
	    }
	};
	
	// ========================================================
	// NOMADS methods
	// ========================================================

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

//	private void routeGrain(NGrain _grain) {
////		Log.d("NomadsApp", "routeGrain()");
//		swarm.parseGrain(grain);
//	}

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
				byte tByte = sand.getAppID();
//				Log.d("NomadsApp", "tByte == " + tByte);
				try {
					if (tByte != 0) {
//						Log.e("NomadsApp", "GETTING GRAIN...");
						grain = sand.getGrain(tByte);
//						Log.e("NomadsApp", "GRAIN RECEIVED. handle.post STARTING...");
//						handle.post(updateUI);
						handle.sendMessage(handle.obtainMessage(0, grain));
//						routeGrain(grain);
//						Log.e("NomadsApp", "handle.post DONE");
					} else {
						Log.e("NomadsApp", "tByte == 0");
					}
				} catch (NullPointerException npe) {
					Log.i("NomadsApp -> Thread", "run() -> grain == null; exiting.");		
//					grain = null;
					if (settings != null) {
						settings.finish();
					}
					if (swarm != null) {
						swarm.finish();
					}
					setConnectionStatus(false);
					removeThread();
				}
			}
		}

//		final Runnable updateUI = new Runnable() {
//			@Override
//			public void run() {
//				Log.e("NomadsApp", "grain.appID: " + grain.appID);
//				Log.e("NomadsApp", "grain.command: " + grain.command);
//				swarm.parseGrain(grain);
//			}
//		};
		
		final Handler handle = new Handler () {
			@Override
			public void handleMessage (Message _msg) {
				swarm.parseGrain( (NGrain)_msg.obj );
			}
		};
	}
	
	// ========================================================
	
	public void cancelAllTextInput() {
		if (swarm != null) {
			swarm.cancelAllTextInput();
		}
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
