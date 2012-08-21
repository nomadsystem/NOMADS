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
	private Dot dot;
	private Settings settings;
//	private GrainTarget gT;
	private static NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
	private AppState appState = new AppState();
	
	private boolean connectionStatus = false;
	private boolean touchDown = false;
	private float[] xy, xytd;

	public static NomadsApp getInstance() {
		return singleton;
	}
	
	public class AppState {
		public boolean pointerIsVisible = false;
		public boolean discussToggle = false;
		public boolean discussMessageToggle = false;
		public boolean cloudToggle = false;
		public boolean cloudMessageToggle = false;
		public boolean dropletsToggle = false;
		public float dropletsVolume = 0.0f;
		public boolean tonesToggle = false;
		public float tonesVolume = 0.0f;
		public boolean cloudTonesToggle = false;
		public float cloudVolume = 0.0f;
		public String currentPrompt, currentChatWindow;
	}
	
	public AppState state () {
		return appState;
	}

	@Override
	public void onCreate() {
		Log.i("NomadsApp", "onCreate()");

		super.onCreate();

		singleton = this;

		am = (AudioManager) getBaseContext().getSystemService(
				Context.AUDIO_SERVICE);
		
		// set media volume to max
		am.setStreamVolume(AudioManager.STREAM_MUSIC,
				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
			    AudioManager.FLAG_SHOW_UI);

		
		// initialize xy coordinates in case of sound before any touches
		xy = new float[2];
		xytd = new float [2];
		xy[0] = 0.5f;
		xy[1] = 0.5f;
		xytd[0] = 0.5f;
		xytd[1] = 0.5f;
		
//		currentPrompt = currentChatWindow = null;
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
	
	public void setDot(Dot _dot) {
		dot = _dot;
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

	private static class NomadsAppThread extends Thread {
		public NomadsAppThread() {
			System.out.println("appState: " + singleton.isConnected());
		}

		public void run() {
			while (singleton.isConnected()) {
				// Log.i( "NomadsApp->Thread", "getThreadState() = " +
				// getAppState() );
				byte tByte = singleton.getSand().getAppID();
//				Log.d("NomadsApp", "tByte == " + tByte);
				try {
					if (tByte != 0) {
//						Log.e("NomadsApp", "GETTING GRAIN...");
						singleton.grain = sand.getGrain(tByte);
//						Log.e("NomadsApp", "GRAIN RECEIVED. handle.post STARTING...");
						handle.sendMessage(handle.obtainMessage(1, singleton.grain));
//						Log.e("NomadsApp", "handle: Message Sent.");
					} else {
						Log.e("NomadsApp", "tByte == 0");
					}
				} catch (NullPointerException npe) {		
//					grain = null;
					handle.sendMessage(handle.obtainMessage(0));
				}
			}
		}
		
		static Handler handle = new Handler () {
			@Override
			public void handleMessage (Message _msg) {
				if (_msg.what == 1) {
					singleton.swarm.parseGrain( (NGrain)_msg.obj );
				}
				else if (_msg.what == 0) {
					Log.i("NomadsApp -> Thread", "run() -> grain == null; exiting.");
					if (singleton.settings != null) {
						singleton.settings.finish();
					}
					if (singleton.swarm != null) {
						singleton.swarm.finish();
					}
					singleton.setConnectionStatus(false);
					singleton.removeThread();
				}
			}
		};
	}
	
	// ========================================================
	
	public void dropAnimation () {
		if (dot != null) {
			dot.animateGrow();
		}
	}
	
//	public void setPointerVisibility (boolean _v) {
//		dot.setPointerVisibility (_v);
//	}
	
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
