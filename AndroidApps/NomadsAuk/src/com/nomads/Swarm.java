// Swarm.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.20

package com.nomads;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
import nomads.v210.NGrain;
import java.io.IOException;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class Swarm extends Activity {
	private NomadsApp app;
	private MediaPlayer[] tonesPlayers;
	private MediaPlayer dropletsPlayer, cloudPlayer;
	private NGrain grain, sGrain;
	private AssetManager assetManager;
	final Context context = this;
	ScrollView chatScrollView;
	TextView chatWindow, prompt;
	ImageButton buttonDiscuss, buttonCloud, buttonSettings;
	EditText messageDiscuss, messageCloud;
	AlertDialog.Builder alert;
	private static Handler tonesHandler, dropletsHandler;
	// tonesTimerHandler created below
	
	// time between tones
	private int tonesDelay = 250;
	// minimum amount of time between pitch changes
	private static int tonesTimerDelay = 1000;
	private static boolean tonesPlaybackLocked = false;
	private int tonesSoundFileIndex;
	private int dropletsRange = 5000;
	private int dropletsOffset = 4000;
	private String[] tonesFiles, dropletsFiles, cloudFiles;
	private String currentPrompt, currentChatWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		Log.i("Swarm", "onCreate()");
		
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		app = NomadsApp.getInstance();

		// send reference of Swarm to NomadsApp
		app.setSwarm(this);
		
		// initialize assets
		assetManager = context.getAssets();
		try {
			tonesFiles = assetManager.list("tones");
			dropletsFiles = assetManager.list("droplets");
			cloudFiles = assetManager.list("cloud");
		} catch (IOException e) {
			Log.e("Swarm", "Error attempting to access assets");
			e.printStackTrace();
		}
		
		// initialize handler for timed sound playback
		tonesHandler = new Handler();
		dropletsHandler = new Handler();
		
		// set device volume control to change media volume
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// initialize UI
		setContentView(R.layout.swarm);
		chatScrollView = (ScrollView) findViewById(R.id.chat_ScrollView);
		chatWindow = (TextView) findViewById(R.id.chatWindow);
		chatWindow.setMovementMethod(new ScrollingMovementMethod());
		chatWindow.getParent().requestDisallowInterceptTouchEvent(true);
		buttonDiscuss = (ImageButton) findViewById(R.id.buttonDiscuss);
		buttonDiscuss.setOnClickListener(discussListener);
		buttonCloud = (ImageButton) findViewById(R.id.buttonCloud);
		buttonCloud.setOnClickListener(cloudListener);
		buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(settingsListener);
		messageDiscuss = (EditText) findViewById(R.id.messageDiscuss);
		messageDiscuss.setOnFocusChangeListener(messageListener);
		messageDiscuss.setOnKeyListener(messageKeyListener);
		messageCloud = (EditText) findViewById(R.id.messageCloud);
		messageCloud.setOnFocusChangeListener(messageListener);
		messageCloud.setOnKeyListener(messageKeyListener);
		prompt = (TextView) findViewById(R.id.prompt);
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/papyrus.ttf"); 
		prompt.setTypeface(type);
		
		// register client with server
		register();
	}
	
	@Override
	protected void onResume() {
//		Log.i("Swarm", "onResume()");
		super.onResume();
		
		// request audio focus
		if (app.requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//			Log.d("Swarm", "onResume(): audio focus aquired.");
		}

		// create array of media players
		initializeMediaPlayers();

		// turn off ringer
		app.phoneRingerState(false);
		
		// restore current display
		updateDisplay();
		
		// start surfaceView thread (if not already running)
		if (app.dot != null)
			app.dot.surfaceThreadRestart();
		
		// restore audio settings
		updateAudio();
	}
	
	@Override
	protected void onPause() {
//		Log.i("Swarm", "onPause()");
		super.onPause();

		// stop all audio playback
		stopTones();
		stopDroplets();
		if (cloudPlayer.isPlaying()) {
			cloudPlayer.stop();
		}
		
		// destroy all media player instances
		releaseMediaPlayers();
		
		// release audio focus
		app.releaseAudioFocus();

		// turn ringer back on (?)
		// app.phoneRingerState(true);
		
		// stop surfaceView thread (if already running)
		if (app.dot != null)
			app.dot.surfaceThreadStop();
	}

	@Override
	protected void onStop() {
//		Log.i("Swarm", "onStop()");
		super.onStop();
	}
	
	@Override
	protected void onStart() {
//		Log.i("Swarm", "onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
//		Log.i("Swarm", "onRestart()");
		super.onRestart();
	}

	@Override
	public void onBackPressed() {
		quitAlert();
	}

	// ========================================================
	// Network Methods
	// ========================================================
	
	public void register() {
//		Log.d("Swarm", "register()");

		if (!app.isConnected()) {
			Log.e("Swarm", "Register failed because connectionStatus is false");
			return;
		}

		// Send the register byte to the Nomads server
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sGrain = new NGrain(NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER,
				NDataType.BYTE, 1, registerByte);
		app.sendGrain(sGrain);
	}

	public void parseGrain(NGrain _grain) {
//		Log.i("Swarm", "parseGrain(): grain received");
		grain = _grain;
		
		// update existing data from server on register
		if (grain.appID == NAppIDAuk.SERVER) {
			if (grain.command == NCommandAuk.SEND_PROMPT_ON) {
				currentPrompt = new String(grain.bArray);
//				Log.d("Swarm", "Setting PROMPT Topic: " + currentPrompt);
				app.state().currentPrompt = currentPrompt;
				prompt.setText(currentPrompt);
			}
			if (grain.command == NCommandAuk.SEND_CACHED_DISCUSS_STRING) {
				String msg = new String(grain.bArray);
//				Log.d("Swarm", "Discuss message received: " + msg);
				appendText(msg);
//				Log.d("Discuss", "ChatWindow: " + msg);
			}
		}
		
		// data from the control panel
		if (grain.appID == NAppIDAuk.CONDUCTOR_PANEL) {
//			Log.d("Swarm", "from Conductor Panel: ");
//			Log.i("Swarm", "grain.command == " + grain.command);

			if (grain.command == NCommandAuk.SET_DISCUSS_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "from Control Panel: Discuss OFF");
					app.state().discussToggle = false;
					updateDisplay();
					cancelAllTextInput();
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "from Control Panel: Discuss ON");
					app.state().discussToggle = true;
					updateDisplay();
				}
			}

			else if (grain.command == NCommandAuk.SET_CLOUD_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "from Control Panel: Cloud OFF");
					app.state().cloudToggle = false;
					updateDisplay();
					cancelAllTextInput();
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "from Control Panel: Cloud ON");
					app.state().cloudToggle = true;
					updateDisplay();
				}
			}
			
			else if (grain.command == NCommandAuk.SET_DROPLET_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "Setting Droplets to OFF");
					app.state().dropletsToggle = false;
					stopDroplets();
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "Setting Droplets to ON");
					app.state().dropletsToggle = true;
					startDroplets();
				}
			}
			
			else if (grain.command == NCommandAuk.SET_CLOUD_SOUND_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "Setting CloudTones to OFF");
					app.state().cloudTonesToggle = false;
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "Setting CloudTones to ON");
					app.state().cloudTonesToggle = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "Setting Pointer to OFF");
					app.state().pointerIsVisible = false;
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "Setting Pointer to ON");
					app.state().pointerIsVisible = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_TONE_STATUS) {
				if (grain.bArray[0] == 0) {
//					Log.d("Swarm", "Setting Tones to OFF");
					app.state().tonesToggle = false;
				} else if (grain.bArray[0] == 1) {
//					Log.d("Swarm", "Setting Tones to ON");
					app.state().tonesToggle = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SEND_PROMPT_ON) {
				currentPrompt = new String(grain.bArray);
//				Log.d("Swarm", "Setting PROMPT Topic: " + currentPrompt);
				app.state().currentPrompt = currentPrompt;
				prompt.setText(currentPrompt);
			}
			
			else if (grain.command == NCommandAuk.SEND_PROMPT_OFF) {
				currentPrompt = " ";
//				Log.d("Swarm", "PROMPT is OFF");
				app.state().currentPrompt = currentPrompt;
				prompt.setText(currentPrompt);
			}

			else if (grain.command == NCommandAuk.SET_DROPLET_VOLUME) {
//				Log.i("Swarm", "changing droplets volume for mPlayers (current same as pointer volume)");
				double dropletsVolVal = ( (double) grain.iArray[0]) * 0.7f;
				float dropletsVolume = (float) (Math.pow(dropletsVolVal, 2) / 10000.0);
				
				app.state().dropletsVolume = dropletsVolume;
				
				if (dropletsPlayer != null) {
//						Log.d("Swarm", "setting volume for mPlayer["+i+"] to: " + dropletsVolume);
					dropletsPlayer.setVolume(app.state().dropletsVolume, app.state().dropletsVolume);
				}
			}
			
			else if (grain.command == NCommandAuk.SET_CLOUD_SOUND_VOLUME) {
//				Log.i("Swarm", "changing volume for onePlayer");
				double cloudVolVal = ( (double) grain.iArray[0] ) * 0.7f;
				float cloudVolume = (float) (Math.pow(cloudVolVal, 2) / 10000.0);
				
				app.state().cloudVolume = cloudVolume;
				
				cloudPlayer.setVolume(app.state().cloudVolume, app.state().cloudVolume);
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_TONE_VOLUME) {
//				Log.i("Swarm", "changing pointer volume for mPlayers");
				double pointerVolVal = ( (double) grain.iArray[0] ) * 0.4f;
				float tonesVolume = (float) (Math.pow(pointerVolVal, 2) / 10000.0);
				
				app.state().tonesVolume = tonesVolume;
				
				for (int i = 0; i < tonesPlayers.length; i++) {
					if (tonesPlayers[i] != null) {
//						Log.d("Swarm", "setting volume for mPlayer["+i+"] to: " + pointerVolume);
						tonesPlayers[i].setVolume(app.state().tonesVolume, app.state().tonesVolume);
					}
				}
			}
			
		} else if (grain.appID == NAppIDAuk.OC_DISCUSS) {
			if (grain.command == NCommandAuk.SEND_MESSAGE) {
				String msg = new String(grain.bArray);
//				Log.d("Swarm", "Discuss message received: " + msg);
				appendText(msg);
//				Log.d("Discuss", "ChatWindow: " + msg);
			}
		}
// handle received cloud messages?
//		else if (grain.appID == NAppIDAuk.OC_CLOUD) {
//			if (grain.command == NCommandAuk.SEND_MESSAGE) {
//				String text = new String(grain.bArray);
//				Log.d("Swarm", "Cloud message received: " + text);
//			}
//		}

	}

	// ========================================================
	// Listeners
	// ========================================================

	Button.OnClickListener discussListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (app.state().discussToggle) {
				app.state().discussMessageToggle = true;
				// hide cloud message field if visible
				if (app.state().cloudMessageToggle == true)
					app.state().cloudMessageToggle = false;
				updateDisplay();
				messageDiscuss.setText(null);
				setMessageFocus(true, messageDiscuss);
			}
		}
	};

	Button.OnClickListener cloudListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (app.state().cloudToggle) {				
				app.state().cloudMessageToggle = true;
				// hide discuss message field if visible
				if (app.state().discussMessageToggle == true)
					app.state().discussMessageToggle = false;
				updateDisplay();
				messageCloud.setText(null);
				setMessageFocus(true, messageCloud);
			}
		}
	};

	Button.OnClickListener settingsListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), Settings.class);
			startActivity(intent);
		}
	};
	
	Button.OnClickListener cancelListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			cancelAllTextInput();
		}
	};
	
	EditText.OnFocusChangeListener messageListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == messageDiscuss) {
                if (hasFocus) {
                    //open keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageDiscuss,
                            InputMethodManager.SHOW_FORCED);
                } else {
                	//close keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    		messageDiscuss.getWindowToken(), 0);
                }
            } else if (v == messageCloud) {
            	if (hasFocus) {
                    //open keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageCloud,
                            InputMethodManager.SHOW_FORCED);
                } else {
                	//close keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    		messageDiscuss.getWindowToken(), 0);
                }
            }
        }
    };
    
    EditText.OnKeyListener messageKeyListener = new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
        	if (v == messageDiscuss) {
	            if (event.getAction() == KeyEvent.ACTION_DOWN) {
	                switch (keyCode) {
	                    case KeyEvent.KEYCODE_DPAD_CENTER:
	                    case KeyEvent.KEYCODE_ENTER:
	                    	String value = messageDiscuss.getText().toString();
	            			Log.i("Swarm->Discuss", "value = " + value);
	            			byte[] discussMsg = value.getBytes();
	            			// eventually use this:
	            			// char[] discussMsg = value.toCharArray();
	            			sGrain = new NGrain(NAppIDAuk.OC_DISCUSS,
	            					NCommandAuk.SEND_MESSAGE,
	            					NDataType.CHAR,
	            					discussMsg.length,
	            					discussMsg);
	            			app.sendGrain(sGrain);
	            			cancelAllTextInput();
	                        return true;
	                    default:
	                        break;
	                }
	            } 
        	} else if (v == messageCloud) {
	        	if (event.getAction() == KeyEvent.ACTION_DOWN) {
	                switch (keyCode) {
	                    case KeyEvent.KEYCODE_DPAD_CENTER:
	                    case KeyEvent.KEYCODE_ENTER:
	            			String value = messageDiscuss.getText().toString();
	            			Log.i("Swarm", "Cloud sent: " + value);
	            			byte[] cloudMsg = value.getBytes();
	            			sGrain = new NGrain(
	            					NAppIDAuk.OC_CLOUD,
	            					NCommandAuk.SEND_MESSAGE,
	            					NDataType.CHAR,
	            					cloudMsg.length,
	            					cloudMsg);
	            			app.sendGrain(sGrain);
	            			if ( app.state().cloudTonesToggle && !cloudPlayer.isPlaying() )
	            				playRandomCloud();
	            			cancelAllTextInput();
	                        return true;
	                    default:
	                        break;
	                }
	        	}
        	}
        	
            return false;
        }
    };

	// ========================================================
	// Alerts
	// ========================================================

	protected void quitAlert() {
		alert = new AlertDialog.Builder(context);

		alert.setTitle("Really quit?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				app.setConnectionStatus(false);
				app.getSand().closeConnection();
				app.closeSand();
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

	// ========================================================
	// Audio Methods
	// ========================================================

// create a more generalized playback class?
//	public void playMediaPlayer (MediaPlayer _mp) {
//		
//	}

	public void playRandomCloud () {
//		Log.i("Swarm", "playSingleRandomSoundFromBank() started");
		try {
			// if one Player is current playing, return
			if (cloudPlayer.isPlaying())
				return;
			
			cloudPlayer.reset();
			Random rand = new Random();
			int soundFileIndex = (int) (rand.nextFloat() * (float)cloudFiles.length);
					
			AssetFileDescriptor afd = context.getAssets().openFd("cloud/" + cloudFiles[soundFileIndex]);
			cloudPlayer.setDataSource(
					afd.getFileDescriptor(),
					afd.getStartOffset(),
					afd.getLength());
			afd.close();
			cloudPlayer.prepare();
			// currentPlayer.setLooping(true);
			// currentPlayer.seekTo(0);
//			Log.i("Swarm", "playing onePlayer...");
			cloudPlayer.start();
// This is how to detect when playback is finished:			
//			cloudPlayer.setOnCompletionListener(new OnCompletionListener() {
//						@Override
//						public void onCompletion(MediaPlayer mp) {
//							if (cloudPlayer == mp) {
//								Log.i("Swarm", "cloudPlayer finished.");
//							}
//						}
//					});

		} catch (IllegalArgumentException e) {
			Log.e("Swarm", "IllegalArgumentException: " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			Log.e("Swarm", "IllegalStateException: " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e("Swarm", "IOException: " + e.getMessage(), e);
		} catch (Exception e) {
			Log.e("Swarm", "Exception: " + e.getMessage(), e);
		}
	}
	
	public void playTones () {
//		Log.i("Swarm", "playSoundFromBankXY() started");

		for (int i = 0; i < tonesPlayers.length; i++) {
			if (!tonesPlayers[i].isPlaying() && tonesPlayers[i] != null) {
				try {
					// only allow notes to change after 
					if (!tonesPlaybackLocked) {
						startTonesTimer();
						tonesSoundFileIndex = (int) (app.getXY_td()[1] * (float)tonesFiles.length);
						tonesPlaybackLocked = true;
					}

					// allows a previously played mPlayer to play again
					tonesPlayers[i].reset();
					
//					Log.i("Swarm", "normalized Y value is: " + app.getXY_td()[1]);
//					Log.i("Swarm", "soundFileIndex is: " + tonesSoundFileIndex);
//					Log.i("Swarm", "soundFile is: " + tonesFiles[tonesSoundFileIndex]);
							
					AssetFileDescriptor afd = context.getAssets().openFd("tones/" + tonesFiles[tonesSoundFileIndex]);
					tonesPlayers[i].setDataSource(
							afd.getFileDescriptor(),
							afd.getStartOffset(),
							afd.getLength());
					afd.close();
					tonesPlayers[i].prepare();
//					Log.i("Swarm", "playing MediaPlayer instance #: " + i);
					tonesPlayers[i].start();

				} catch (IllegalArgumentException e) {
					Log.e("Swarm", "IllegalArgumentException: " + e.getMessage(), e);
				} catch (IllegalStateException e) {
					Log.e("Swarm", "IllegalStateException: " + e.getMessage(), e);
				} catch (IOException e) {
					Log.e("Swarm", "IOException: " + e.getMessage(), e);
				} catch (Exception e) {
					Log.e("Swarm", "Exception: " + e.getMessage(), e);
				}

				break;
			}
		}
	}
	
	public void playDroplets () {
//		Log.i("Swarm", "playSoundFromBankXYDynamic() started");

		if (!dropletsPlayer.isPlaying() && dropletsPlayer != null) {
			try {
				// in case mPlayer is still somehow playing, stop it

				// allows a previously played mPlayer to play again
				dropletsPlayer.reset();
				
				int soundFileIndex = (int) (app.getXY()[1] * (float)dropletsFiles.length);
				
//				Log.i("Swarm", "normalized Y value is: " + app.getXY()[1]);
//				Log.i("Swarm", "soundFileIndex is: " + soundFileIndex);
//				Log.i("Swarm", "soundFile is: " + dropletsFiles[soundFileIndex]);
						
				AssetFileDescriptor afd = context.getAssets().openFd("droplets/" + dropletsFiles[soundFileIndex]);
				dropletsPlayer.setDataSource(
						afd.getFileDescriptor(),
						afd.getStartOffset(),
						afd.getLength());
				afd.close();
				dropletsPlayer.prepare();
				dropletsPlayer.start();

			} catch (IllegalArgumentException e) {
				Log.e("Swarm", "IllegalArgumentException: " + e.getMessage(), e);
			} catch (IllegalStateException e) {
				Log.e("Swarm", "IllegalStateException: " + e.getMessage(), e);
			} catch (IOException e) {
				Log.e("Swarm", "IOException: " + e.getMessage(), e);
			} catch (Exception e) {
				Log.e("Swarm", "Exception: " + e.getMessage(), e);
			}
		}
	}

	Runnable tonesRunnable = new Runnable() {
		@Override
		public void run() {			
			if ( app.state().tonesToggle ) {
				// play a sound from the tonesFiles array
				playTones();
				tonesHandler.postDelayed(tonesRunnable, tonesDelay);
			}
		}
	};
	
	Runnable dropletsRunnable = new Runnable() {
		@Override
		public void run() {
			if ( app.state().dropletsToggle ) {
				// play a sound from the dropletsFiles array
				playDroplets();
				if (app.dot != null)
					app.dot.animateGrow();
				dropletsHandler.postDelayed(dropletsRunnable, getPlayInterval(dropletsRange, dropletsOffset));
			}
		}
	};
	
	private static void startTonesTimer () {
		tonesTimerRunnable.run();
	}
	
	private static void stopTonesTimer () {
		tonesTimerHandler.removeCallbacks(tonesTimerRunnable);
	}
	
	static Runnable tonesTimerRunnable = new Runnable () {
		@Override
		public void run () {
			tonesTimerHandler.sendMessageDelayed (tonesTimerHandler.obtainMessage(0), tonesTimerDelay);
		}
	};
	
	private static Handler tonesTimerHandler = new Handler () {
		@Override
		public void handleMessage(Message msg) {  
			tonesPlaybackLocked = false;
	    	stopTonesTimer ();
		}
	};

	private void startTones() {
		tonesRunnable.run();
	}

	private void stopTones() {
		tonesHandler.removeCallbacks(tonesRunnable);
	}
	
	private void startDroplets() {
		dropletsRunnable.run();
	}

	private void stopDroplets() {
		dropletsHandler.removeCallbacks(dropletsRunnable);
	}

	private void initializeMediaPlayers() {
		// initialize MediaPlayer array, boolean array size
		tonesPlayers = new MediaPlayer[4];
		
		// Create individual media players - needed?
		for (int i = 0; i < tonesPlayers.length; i++) {
			tonesPlayers[i] = new MediaPlayer();
		}
		cloudPlayer = new MediaPlayer();
		dropletsPlayer = new MediaPlayer();
	}

	void releaseMediaPlayers() {
		// release the media players
		for (int i = 0; i < tonesPlayers.length; i++) {
			tonesPlayers[i].release();
		}
		cloudPlayer.release();
		dropletsPlayer.release();
	}
	
	private int getPlayInterval(int _range, int _offset) {
		int pI = (int) (app.getXY()[0] * _range + _offset);
		return pI;
	}
	
	void updateAudio() {
		for (int i = 0; i < tonesPlayers.length; i++) {
			if (tonesPlayers[i] != null) {
//				Log.d("Swarm", "setting volume for mPlayer["+i+"] to: " + dropletsVolume);
				tonesPlayers[i].setVolume(app.state().tonesVolume, app.state().tonesVolume);
			}
		}
		dropletsPlayer.setVolume(app.state().dropletsVolume, app.state().dropletsVolume);
		cloudPlayer.setVolume(app.state().cloudVolume, app.state().cloudVolume);
		
		// start auto droplets if necessary
		if (app.state().dropletsToggle)
			startDroplets();
	}

	// ========================================================
	// Display Methods
	// ========================================================
	
	private void appendText(String _text) {
		if (chatWindow != null) {
			if (currentChatWindow == null) {
				currentChatWindow = (_text + "\n");		// if new chat window, set...
			} else {
				currentChatWindow += (_text + "\n");	// otherwise, append
			}				
			// save current chatWindow text in case of device rotation ( see onResume() below )
			app.state().currentChatWindow = currentChatWindow;
			// get saved chat window from NomadsApp.appState
			chatWindow.setText( app.state().currentChatWindow );
			scrollText();
		}
	}
	
	public void cancelAllTextInput() {
		if (app.state().discussMessageToggle = true) app.state().discussMessageToggle = false;
		if (app.state().cloudMessageToggle = true) app.state().cloudMessageToggle = false;
		updateDisplay();
	}
	
	// called from NomadsApp
	public void pointerStatus (boolean _down) {
		if (_down) {
			startTones();
		} else {
			stopTones();
		}
	}
	
	public void setMessageFocus (boolean isFocused, EditText _target) {
		_target.setCursorVisible(isFocused);
		_target.setFocusable(isFocused);
		_target.setFocusableInTouchMode(isFocused);

	    if (isFocused) {
	    	_target.requestFocus();
	    }
	}
	
	// scroll the textview to view latest messages
	private void scrollText () {
//		Log.d("Swarm", "scrollText()");
		
	    chatScrollView.post(new Runnable() {
	        public void run() {
	            chatScrollView.fullScroll(View.FOCUS_DOWN);
	        }
	    });

	}
	
	int convertVisibility (boolean _b) {
		int vis = 0;
		if (_b)
			vis = View.VISIBLE;
		else
			vis = View.GONE;
		
		return vis;
	}
	
	void updateDisplay() {
		buttonDiscuss.setVisibility( convertVisibility(app.state().discussToggle) );
		buttonCloud.setVisibility( convertVisibility(app.state().cloudToggle) );
		messageDiscuss.setVisibility( convertVisibility(app.state().discussMessageToggle) );
		messageCloud.setVisibility( convertVisibility(app.state().cloudMessageToggle) );
		
		// restore prompt message if device is rotated
		prompt.setText( app.state().currentPrompt );
		
		// restore chat window and scroll to the newest message if device is rotated
		chatWindow.setText( app.state().currentChatWindow );
	}
}
