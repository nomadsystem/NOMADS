// Swarm.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import java.io.IOException;
import java.util.Random;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
//import nomads.v210.NGlobals.GrainTarget;
import nomads.v210.NGrain;
import nomads.v210.NSand;
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
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Swarm extends Activity {
	private NomadsApp app;
	private MediaPlayer[] mPlayer;
	private MediaPlayer onePlayer;
	private NSand sand;
	private NGrain grain;
	private AssetManager assetManager;
	final Context context = this;
	TextView chatWindow, prompt;
	ImageButton buttonDiscuss, buttonCloud, buttonSettings;
	Button buttonAudioTest1, buttonAudioTest2, buttonSendDiscuss, buttonSendCloud, buttonCancel;
	EditText message;
	AlertDialog.Builder alert;
	private Handler tonesHandler, dropletsHandler;
	
	private boolean mPPlaying[];
	private boolean onePlayPlaying;
	private boolean discussToggle = true;
	private boolean cloudToggle = true;
	private boolean dropletsToggle = false;
	private boolean tonesToggle = false;
	private boolean cloudTonesToggle = false;
//	private int tonesRange = 400;
//	private int tonesOffset = 100;
	private int tonesDelay = 250;
	private int dropletsRange = 5000;
	private int dropletsOffset = 4000;
	private String[] tonesFiles, dropletsFiles, cloudFiles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Swarm", "onCreate()");
		
		super.onCreate(savedInstanceState);
		
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//		app = (NomadsApp) this.getApplicationContext();
		app = NomadsApp.getInstance();

		// send reference of Swarm to NomadsApp
		app.setSwarm(this);

		// get NSand instance from Join
		sand = app.getSand();
		
		// initialize assets
		assetManager = context.getAssets();
		try {
			tonesFiles = assetManager.list("tones");
			dropletsFiles = assetManager.list("droplets");
			cloudFiles = assetManager.list("cloud");
//			for (int i=0; i<tonesFiles.length; i++) {
//				Log.i("Swarm", "tones file #" + i + ": " + tonesFiles[i]);
//			}
		} catch (IOException e) {
			Log.i("Swarm", "Error attempting to access assets");
			e.printStackTrace();
		}
		
		// initialize handler for timed sound playback
		tonesHandler = new Handler();
		dropletsHandler = new Handler();
			
		// media players now initialized in onResume()
		// initializeMediaPlayers();

		// initialize UI
		setContentView(R.layout.swarm);
		chatWindow = (TextView) findViewById(R.id.chatWindow);
		chatWindow.setMovementMethod(new ScrollingMovementMethod());
		buttonDiscuss = (ImageButton) findViewById(R.id.buttonDiscuss);
		buttonDiscuss.setOnClickListener(discussListener);
		buttonCloud = (ImageButton) findViewById(R.id.buttonCloud);
		buttonCloud.setOnClickListener(cloudListener);
		buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(settingsListener);
//		buttonAudioTest1 = (Button) findViewById(R.id.buttonAudioTest1);
//		buttonAudioTest1.setOnClickListener(tonesTestButtonListener);
//		buttonAudioTest2 = (Button) findViewById(R.id.buttonAudioTest2);
//		buttonAudioTest2.setOnClickListener(dropletsTestButtonListener);
		buttonSendDiscuss = (Button) findViewById(R.id.sendDiscuss);
		buttonSendDiscuss.setOnClickListener(sendDiscussListener);
		buttonSendCloud = (Button) findViewById(R.id.sendCloud);
		buttonSendCloud.setOnClickListener(sendCloudListener);
		buttonCancel = (Button) findViewById(R.id.cancel);
		buttonCancel.setOnClickListener(cancelListener);
		message = (EditText) findViewById(R.id.message);
		message.setOnFocusChangeListener(messageListener);
		prompt = (TextView) findViewById(R.id.prompt);
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/papyrus.ttf"); 
		prompt.setTypeface(type);

		
		// register with server
		register();
	}

	public void cancelAllTextInput() {
		if (buttonSendDiscuss.getVisibility() == View.VISIBLE) buttonSendDiscuss.setVisibility(View.GONE);
		if (buttonSendCloud.getVisibility() == View.VISIBLE) buttonSendCloud.setVisibility(View.GONE);
		if (buttonCancel.getVisibility() == View.VISIBLE) buttonCancel.setVisibility(View.GONE);
		if (message.getVisibility() == View.VISIBLE) message.setVisibility(View.GONE);
//		if (prompt.getVisibility() == View.VISIBLE) prompt.setVisibility(View.GONE);
//		InputMethodManager imm = (InputMethodManager)getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
	}
	
	public void setMessageFocus(boolean isFocused) {
		message.setCursorVisible(isFocused);
		message.setFocusable(isFocused);
		message.setFocusableInTouchMode(isFocused);

	    if (isFocused) {
	    	message.requestFocus();
	    }
	}
	
//	private void setPlayInterval(int _playInterval) {
//		playInterval = _playInterval;		
//	}
	
	private int getPlayInterval(int _range, int _offset) {
		int pI = (int) (app.getXY()[0] * _range + _offset);
		return pI;
	}


	// ========================================================
	// Network
	// ========================================================
	
	public void register() {
		Log.d("Swarm", "register()");

		if (!app.isConnected()) {
			Log.e("Swarm", "Register failed because connectionStatus is false");
			return;
		}

		// Send the register byte to the Nomads server
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sand.sendGrain(NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER,
				NDataType.BYTE, 1, registerByte);
	}

	public void parseGrain(NGrain _grain) {
		Log.i("Swarm", "parseGrain(): grain received");
		grain = _grain;

//		if (grain == null) {
//			Log.e("Swarm", "parseGrain(): grain is null");
//			return;
//		}
		
		if (grain.appID == NAppIDAuk.CONDUCTOR_PANEL) {
			Log.d("Swarm", "from Conductor Panel: ");
			
			Log.i("Swarm", "grain.command == " + grain.command);

			if (grain.command == NCommandAuk.SET_DISCUSS_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "from Control Panel: Discuss OFF");
					discussToggle = false;
					buttonDiscuss.setVisibility(View.GONE);
					cancelAllTextInput();
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "from Control Panel: Discuss ON");
					discussToggle = true;
					buttonDiscuss.setVisibility(View.VISIBLE);
				}
			}

			else if (grain.command == NCommandAuk.SET_CLOUD_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "from Control Panel: Cloud OFF");
					cloudToggle = false;
					buttonCloud.setVisibility(View.GONE);
					cancelAllTextInput();
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "from Control Panel: Cloud ON");
					cloudToggle = true;
					buttonCloud.setVisibility(View.VISIBLE);
				}
			}
			
			else if (grain.command == NCommandAuk.SET_DROPLET_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "Setting Droplets to OFF");
//					stopDroplets();
					dropletsToggle = false;
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "Setting Droplets to ON");
//					startDroplets();
					dropletsToggle = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SET_CLOUD_SOUND_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "Setting CloudTones to OFF");
					cloudTonesToggle = false;
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "Setting CloudTones to ON");
					cloudTonesToggle = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "Setting Pointer to OFF");
					app.setPointerVisibility(false);
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "Setting Pointer to ON");
					app.setPointerVisibility(true);
				}
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_TONE_STATUS) {
				if (grain.bArray[0] == 0) {
					Log.d("Swarm", "Setting Tones to OFF");
//					stopTones();
					tonesToggle = false;
				} else if (grain.bArray[0] == 1) {
					Log.d("Swarm", "Setting Tones to ON");
//					startTones();
					tonesToggle = true;
				}
			}
			
			else if (grain.command == NCommandAuk.SEND_PROMPT_ON) {
				String text = new String(grain.bArray);
				Log.d("Swarm", "Setting PROMPT Topic: " + text);
				prompt.setText(text);
			}
			
			else if (grain.command == NCommandAuk.SEND_PROMPT_OFF) {
				Log.d("Swarm", "PROMPT IS OFF");
				prompt.setText("");
			}

			else if (grain.command == NCommandAuk.SET_DROPLET_VOLUME) {
				Log.i("Swarm", "changing droplets volume for mPlayers (current same as pointer volume)");
				double dropletsVolVal = (double) grain.iArray[0]; // Using text from
				float dropletsVolume = (float) (Math.pow(dropletsVolVal, 2) / 10000.0);
				
				for (int i = 0; i < mPlayer.length; i++) {
					if (mPlayer[i] != null) {
//						Log.d("Swarm", "setting volume for mPlayer["+i+"] to: " + dropletsVolume);
						mPlayer[i].setVolume(dropletsVolume, dropletsVolume);
					}
				}
			}
			
			else if (grain.command == NCommandAuk.SET_CLOUD_SOUND_VOLUME) {
				Log.i("Swarm", "changing volume for onePlayer");
				double cloudVolVal = (double) grain.iArray[0]; // Using text from
				float cloudVolume = (float) (Math.pow(cloudVolVal, 2) / 10000.0);
				
				onePlayer.setVolume(cloudVolume, cloudVolume);
			}
			
			else if (grain.command == NCommandAuk.SET_POINTER_TONE_VOLUME) {
				Log.i("Swarm", "changing pointer volume for mPlayers");
				double pointerVolVal = (double) grain.iArray[0]; // Using text from
				float pointerVolume = (float) (Math.pow(pointerVolVal, 2) / 10000.0);
				
				for (int i = 0; i < mPlayer.length; i++) {
					if (mPlayer[i] != null) {
//						Log.d("Swarm", "setting volume for mPlayer["+i+"] to: " + pointerVolume);
						mPlayer[i].setVolume(pointerVolume, pointerVolume);
					}
				}
			}
			
		} else if (grain.appID == NAppIDAuk.OC_DISCUSS) {
			if (grain.command == NCommandAuk.SEND_MESSAGE) {
				String msg = new String(grain.bArray);
				Log.d("Swarm", "Discuss message received: " + msg);
				appendTextAndScroll(msg);
				Log.d("Discuss", "ChatWindow: " + msg);
			}
		}

		else if (grain.appID == NAppIDAuk.OC_CLOUD) {
			if (grain.command == NCommandAuk.SEND_MESSAGE) {
				String text = new String(grain.bArray);
				Log.d("Swarm", "Cloud message received: " + text);
			}
		}

//		if (grain != null)
//			grain = null;
	}

	// ========================================================
	// Listeners
	// ========================================================

	Button.OnClickListener discussListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (discussToggle) {
				buttonSendCloud.setVisibility(View.GONE);
				buttonSendDiscuss.setVisibility(View.VISIBLE);
				buttonCancel.setVisibility(View.VISIBLE);
				message.setVisibility(View.VISIBLE);
//				prompt.setVisibility(View.VISIBLE);
				message.setText("");
				setMessageFocus(true);
			}
		}
	};

	Button.OnClickListener cloudListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (cloudToggle) {
				buttonSendDiscuss.setVisibility(View.GONE);
				buttonSendCloud.setVisibility(View.VISIBLE);
				buttonCancel.setVisibility(View.VISIBLE);
				message.setVisibility(View.VISIBLE);
//				prompt.setVisibility(View.VISIBLE);
				message.setText("");
				setMessageFocus(true);
			}
		}
	};

	Button.OnClickListener settingsListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), Settings.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	};

//	Button.OnClickListener tonesTestButtonListener = new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			if (!tonesToggle) {
//				Log.i("Swarm", "Starting tones...");
//				startTones();
//			} else {
//				stopTones();
//			}
//		}
//	};
//	
//	Button.OnClickListener dropletsTestButtonListener = new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			if (!dropletsToggle) {
//				Log.i("Swarm", "Starting droplets...");
//				startDroplets();
//			} else {
//				stopDroplets();
//			}
//		}
//	};
	
	Button.OnClickListener sendDiscussListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			String value = message.getText().toString();
			Log.i("Swarm->Discuss", "value = " + value);
			byte[] discussMsg = value.getBytes();
			// eventually use this:
			// char[] discussMsg = value.toCharArray();
			sand.sendGrain(
					NAppIDAuk.OC_DISCUSS,
					NCommandAuk.SEND_MESSAGE,
					NDataType.CHAR,
					discussMsg.length,
					discussMsg);
			cancelAllTextInput();
		}
	};
	
	Button.OnClickListener sendCloudListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			String value = message.getText().toString();
			Log.i("Swarm", "Cloud sent: " + value);
			byte[] cloudMsg = value.getBytes();
			sand.sendGrain(
					NAppIDAuk.OC_CLOUD,
					NCommandAuk.SEND_MESSAGE,
					NDataType.CHAR,
					cloudMsg.length,
					cloudMsg);
			if (cloudTonesToggle && !onePlayPlaying)
				playSingleRandomSoundFromBank("cloud", cloudFiles);
			cancelAllTextInput();
		}
	};
	
	Button.OnClickListener cancelListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			cancelAllTextInput();
		}
	};
	
	EditText.OnFocusChangeListener messageListener = new OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            if (v == message)
            {
                if (hasFocus)
                {
                    //open keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(message,
                            InputMethodManager.SHOW_FORCED);

                }
                else
                { //close keyboard
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    		message.getWindowToken(), 0);
                }
            }
        }
    };


	// ========================================================
	// Alerts
	// ========================================================

	protected void quitAlert() {
		alert = new AlertDialog.Builder(context);
		// need to create new input field each time

		alert.setTitle("Really quit?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				app.setConnectionStatus(false);
				app.getSand().closeConnection();
				sand = null;
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

	public void playSingleRandomSoundFromBank (String _pathTo, String[] _soundBank) {
		Log.i("Swarm", "playSingleRandomSoundFromBank() started");
		try {
			// if one Player is current playing, return
			if (onePlayer.isPlaying())
				return;
			
			onePlayPlaying = true;
			onePlayer.reset();
			Random rand = new Random();
			int soundFileIndex = (int) (rand.nextFloat() * (float)_soundBank.length);
					
			AssetFileDescriptor afd = context.getAssets().openFd(_pathTo + "/" + _soundBank[soundFileIndex]);
			onePlayer.setDataSource(
					afd.getFileDescriptor(),
					afd.getStartOffset(),
					afd.getLength());
			afd.close();
			onePlayer.prepare();
			// currentPlayer.setLooping(true);
			// currentPlayer.seekTo(0);
			Log.i("Swarm", "playing onePlayer...");
			onePlayer.start();
			onePlayer
					.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							if (onePlayer == mp) {
								Log.i("Swarm",
										"onePlayer completed. Setting onePlayLocked to false");
								onePlayPlaying = false;
							}
						}
					});

		} catch (IllegalArgumentException e) {
			Log.e("Swarm",
					"IllegalArgumentException: " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			Log.e("Swarm", "IllegalStateException: " + e.getMessage(),
					e);
		} catch (IOException e) {
			Log.e("Swarm", "IOException: " + e.getMessage(), e);
		} catch (Exception e) {
			Log.e("Swarm", "Exception: " + e.getMessage(), e);
		}
	}
	
	public void playSoundFromBankXY(String _pathTo, String[] _soundBank) {
		Log.i("Swarm", "playSoundFromBankXY() started");

		for (int i = 0; i < mPlayer.length; i++) {
			if (!mPPlaying[i] && mPlayer[i] != null) {
//				Log.i("Swarm", "mPPlaying[" + i + "] = " + mPPlaying[i]);
				mPPlaying[i] = true;

				try {
					// in case mPlayer is still somehow playing, stop it
					if (mPlayer[i].isPlaying())
						mPlayer[i].stop();

					// allows a previously played mPlayer to play again
					mPlayer[i].reset();
					
					int soundFileIndex = (int) (app.getXY_td()[1] * (float)_soundBank.length);
					
					Log.i("Swarm", "normalized Y value is: " + app.getXY_td()[1]);
					Log.i("Swarm", "soundFileIndex is: " + soundFileIndex);
					Log.i("Swarm", "soundFile is: " + _soundBank[soundFileIndex]);
							
					AssetFileDescriptor afd = context.getAssets().openFd(_pathTo + "/" + _soundBank[soundFileIndex]);
					mPlayer[i].setDataSource(
							afd.getFileDescriptor(),
							afd.getStartOffset(),
							afd.getLength());
					afd.close();
					mPlayer[i].prepare();
					// currentPlayer.setLooping(true);
					// currentPlayer.seekTo(0);
					Log.i("Swarm", "playing MediaPlayer instance #: " + i);
					mPlayer[i].start();
					mPlayer[i]
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									for (int i = 0; i < mPlayer.length; i++) {
										if (mPlayer[i] == mp) {
//											Log.i("Swarm", "mPlayer["+i+"] completed. Setting mPPlaying["+i+"] to false");
											mPPlaying[i] = false;
										}
									}
								}
							});

				} catch (IllegalArgumentException e) {
					Log.e("Swarm",
							"IllegalArgumentException: " + e.getMessage(), e);
				} catch (IllegalStateException e) {
					Log.e("Swarm", "IllegalStateException: " + e.getMessage(),
							e);
				} catch (IOException e) {
					Log.e("Swarm", "IOException: " + e.getMessage(), e);
				} catch (Exception e) {
					Log.e("Swarm", "Exception: " + e.getMessage(), e);
				}

				break;
			}
		}
	}
	
	public void playSoundFromBankXYDynamic(String _pathTo, String[] _soundBank) {
		Log.i("Swarm", "playSoundFromBankXYDynamic() started");

		for (int i = 0; i < mPlayer.length; i++) {
			if (!mPPlaying[i] && mPlayer[i] != null) {
				Log.i("Swarm", "mPPlaying[" + i + "] = " + mPPlaying[i]);
				mPPlaying[i] = true;

				try {
					// in case mPlayer is still somehow playing, stop it
					if (mPlayer[i].isPlaying())
						mPlayer[i].stop();

					// allows a previously played mPlayer to play again
					mPlayer[i].reset();
					
					int soundFileIndex = (int) (app.getXY()[1] * (float)_soundBank.length);
					
					Log.i("Swarm", "normalized Y value is: " + app.getXY()[1]);
					Log.i("Swarm", "soundFileIndex is: " + soundFileIndex);
					Log.i("Swarm", "soundFile is: " + _soundBank[soundFileIndex]);
							
					AssetFileDescriptor afd = context.getAssets().openFd(_pathTo + "/" + _soundBank[soundFileIndex]);
					mPlayer[i].setDataSource(
							afd.getFileDescriptor(),
							afd.getStartOffset(),
							afd.getLength());
					afd.close();
					mPlayer[i].prepare();
					// currentPlayer.setLooping(true);
					// currentPlayer.seekTo(0);
					Log.i("Swarm", "playing MediaPlayer instance #: " + i);
					mPlayer[i].start();
					mPlayer[i]
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									for (int i = 0; i < mPlayer.length; i++) {
										if (mPlayer[i] == mp) {
											Log.i("Swarm",
													"mPlayer["+i+"] completed. Setting mPPlaying["+i+"] to false");
											mPPlaying[i] = false;
										}
									}
								}
							});

				} catch (IllegalArgumentException e) {
					Log.e("Swarm",
							"IllegalArgumentException: " + e.getMessage(), e);
				} catch (IllegalStateException e) {
					Log.e("Swarm", "IllegalStateException: " + e.getMessage(),
							e);
				} catch (IOException e) {
					Log.e("Swarm", "IOException: " + e.getMessage(), e);
				} catch (Exception e) {
					Log.e("Swarm", "Exception: " + e.getMessage(), e);
				}

				break;
			}
		}
	}

	Runnable tonesRunnable = new Runnable() {
		@Override
		public void run() {
			Log.d("NomadsApp", "TT: " + tonesToggle + " app.pIT: " + app.pointerIsTouching());
			if ( tonesToggle && app.pointerIsTouching() ) {
//				Log.d("Swarm", "tonesToggle is true");
				// sounds located in "tones" directory (get rid of this)
				// play a sound from the tonesFiles array
				playSoundFromBankXY("tones", tonesFiles);
				// updateStatus(); // change value of interval
				tonesHandler.postDelayed(tonesRunnable, tonesDelay);
			}
		}
	};
	
	Runnable dropletsRunnable = new Runnable() {
		@Override
		public void run() {
			if ( dropletsToggle && app.pointerIsTouching() ) {
//				Log.d("Swarm", "dropletsToggle is true");
				// sounds located in "droplets" directory (get rid of this)
				// play a sound from the dropletsFiles array
				playSoundFromBankXYDynamic("droplets", dropletsFiles);
				app.dropAnimation();
				// updateStatus(); // change value of interval
				dropletsHandler.postDelayed(dropletsRunnable, getPlayInterval(dropletsRange, dropletsOffset));
			}
		}
	};

	private void startTones() {
		tonesRunnable.run();
	}

	private void stopTones() {
//		if (tonesToggle) {
			tonesHandler.removeCallbacks(tonesRunnable);
//		}
	}
	
	private void startDroplets() {
		dropletsRunnable.run();
	}

	private void stopDroplets() {
//		if (dropletsToggle) {
			dropletsHandler.removeCallbacks(dropletsRunnable);
//		}
	}

	private void initializeMediaPlayers() {
		// initialize MediaPlayer array, boolean array size
		mPlayer = new MediaPlayer[20];
		mPPlaying = new boolean[20];

		// set all media players to ready
//		for (int i = 0; i < mPPlaying.length; i++) {
//			Log.i("Swarm", "setting mPPlaying[i] to false...");
//			mPPlaying[i] = false;
//			Log.i("Swarm", "mPPlaying[i] = " + mPPlaying[i]);
//		}
		
		// Create individual media players
		for (int i = 0; i < mPlayer.length; i++) {
			mPlayer[i] = new MediaPlayer();
		}
		onePlayer = new MediaPlayer();
	}

	void releaseMediaPlayers() {
		// release the media players
		for (int i = 0; i < mPlayer.length; i++) {
//			if (mPlayer[i] != null) {
				mPlayer[i].release();
//				mPlayer[i] = null;
//			}
		}
//		if (onePlayer != null) {
			onePlayer.release();
//			onePlayer = null;
//		}
	}

	// ========================================================
	
	public void pointerStatus (boolean _down) {
		if (_down) {
			startTones();
			startDroplets();
		} else {
			stopTones();
			stopDroplets();
		}
	}

	private void appendTextAndScroll(String text) {
		if (chatWindow != null) {
			chatWindow.append(text + "\n");
			final Layout layout = chatWindow.getLayout();
			if (layout != null) {
				int scrollDelta = layout.getLineBottom(chatWindow
						.getLineCount() - 1)
						- chatWindow.getScrollY()
						- chatWindow.getHeight();
				if (scrollDelta > 0)
					chatWindow.scrollBy(0, scrollDelta);
			}
		}
	}

	@Override
	protected void onPause() {
		Log.i("Swarm", "onPause()");
		super.onPause();

		// turn off all audio playback
		stopTones();
		stopDroplets();
		if (onePlayPlaying) {
			onePlayer.stop();
			cloudTonesToggle = false;
		}
		
		// destroy all media player instances
		releaseMediaPlayers();
		
		// release audio focus
		app.releaseAudioFocus();

		// turn on ringer (?)
		// app.phoneRingerState(true);
	}

	@Override
	protected void onResume() {
		Log.i("Swarm", "onResume()");
		super.onResume();
		
		// request audio focus
		if (app.requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			Log.d("Swarm", "onResume(): audio focus aquired.");
		}

		// create array of media players
		initializeMediaPlayers();

		// turn off ringer
		app.phoneRingerState(false);
		
//		app.setGrainTarget(GrainTarget.SWARM);
	}

	@Override
	protected void onStop() {
		Log.i("Swarm", "onStop()");
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		Log.i("Swarm", "onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i("Swarm", "onRestart()");
		super.onRestart();

		Intent intent = new Intent(getApplicationContext(), Join.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		quitAlert();
	}
}
