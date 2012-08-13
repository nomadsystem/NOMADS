// Swarm.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import java.io.IOException;

import nomads.v210.NAppIDAuk;
import nomads.v210.NCommandAuk;
import nomads.v210.NDataType;
import nomads.v210.NGlobals.GrainTarget;
import nomads.v210.NGrain;
import nomads.v210.NSand;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
//import android.content.Intent;

public class Swarm extends Activity {
	private NomadsApp app;
	private MediaPlayer[] mPlayer;
	// private MediaPlayer currentPlayer;
	private boolean mPPlaying[];
	private boolean dropletsToggle = false;
	private boolean tonesToggle = false;
	private boolean discussToggle = false;
	private boolean cloudToggle = false;

	private NSand sand;
	private NGrain grain;

	TextView chatWindow, prompt;
	ImageButton buttonDiscuss, buttonCloud, buttonSettings;
	Button buttonAudioTest1, buttonAudioTest2, buttonSendDiscuss, buttonSendCloud, buttonCancel;
	EditText message;
	final Context context = this;
	AlertDialog.Builder alert;
	
	String tempString = "";

	private int tonesInterval = 200;
	private int dropletsInterval = 1000;
	private Handler tonesHandler, dropletsHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Swarm", "onCreate()");
		super.onCreate(savedInstanceState);

		app = (NomadsApp) this.getApplicationContext();

		// send reference of Swarm to NomadsApp
		app.setSwarm(this);

		// get NSand instance from Join
		sand = app.getSand();

		// initialize MediaPlayer array and boolean array
		mPlayer = new MediaPlayer[20];
		mPPlaying = new boolean[20];

		// set all media players to ready
		for (int i = 0; i < mPPlaying.length; i++) {
			Log.d("Swarm", "setting mPPlaying[i] to false...");
			mPPlaying[i] = false;
			Log.d("Swarm", "mPPlaying[i] = " + mPPlaying[i]);
		}

		// initialize handler for timed sound playback
		tonesHandler = new Handler();
		dropletsHandler = new Handler();

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
		buttonAudioTest1 = (Button) findViewById(R.id.buttonAudioTest1);
		buttonAudioTest1.setOnClickListener(tonesTestButtonListener);
		buttonAudioTest2 = (Button) findViewById(R.id.buttonAudioTest2);
		buttonAudioTest2.setOnClickListener(dropletsTestButtonListener);
		buttonSendDiscuss = (Button) findViewById(R.id.sendDiscuss);
		buttonSendDiscuss.setOnClickListener(sendDiscussListener);
		buttonSendCloud = (Button) findViewById(R.id.sendCloud);
		buttonSendCloud.setOnClickListener(sendDiscussListener);
		buttonCancel = (Button) findViewById(R.id.cancel);
		buttonCancel.setOnClickListener(cancelListener);
		message = (EditText) findViewById(R.id.message);
		message.setOnFocusChangeListener(messageListener);
		prompt = (TextView) findViewById(R.id.prompt);
	}

	public void cancelAllTextInput() {
		buttonSendDiscuss.setVisibility(View.GONE);
		buttonSendCloud.setVisibility(View.GONE);
		buttonCancel.setVisibility(View.GONE);
		message.setVisibility(View.GONE);
//		prompt.setVisibility(View.GONE);
//		InputMethodManager imm = (InputMethodManager)getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
	}
	
	public void setMessageFocus(boolean isFocused)
	{
		message.setCursorVisible(isFocused);
		message.setFocusable(isFocused);
		message.setFocusableInTouchMode(isFocused);

	    if (isFocused)
	    {
	    	message.requestFocus();
	    }
	}


	// ========================================================
	// Network
	// ========================================================

	public void parseGrain(NGrain _grain) {
		Log.d("Swarm", "parseGrain(): grain received");
		grain = _grain;

//		if (grain == null) {
//			Log.d("Swarm", "parseGrain(): grain is null");
//			return;
//		}
		
		if (grain.appID == NAppIDAuk.CONDUCTOR_PANEL) {
			Log.d("Swarm", "from Conductor Panel: ");


			if (grain.command == NCommandAuk.SET_DROPLET_STATUS) {
				if (grain.bArray[0] == 0) {
					dropletsToggle = false;
					Log.d("Swarm", "Setting Droplets to OFF");
				} else if (grain.bArray[0] == 1) {
					dropletsToggle = true;
					Log.d("Swarm", "Setting Droplets to ON");
				}
			}

			else if (grain.command == NCommandAuk.SET_DISCUSS_STATUS) {
				if (grain.bArray[0] == 0) {
					discussToggle = false;
					Log.d("Swarm", "DISCUSS_STATUS false");
				} else if (grain.bArray[0] == 1) {
					discussToggle = true;
					Log.d("Swarm", "DISCUSS_STATUS true");
				}
			}

			else if (grain.command == NCommandAuk.SET_CLOUD_STATUS) {
				if (grain.bArray[0] == 0) {
					cloudToggle = false;
					Log.d("Swarm", "CLOUD_STATUS false");
				} else if (grain.bArray[0] == 1) {
					cloudToggle = true;
					Log.d("Swarm", "CLOUD_STATUS true");
				}
			}

			else if (grain.command == NCommandAuk.SET_DROPLET_VOLUME) {
//				double tDropVal = (double) grain.iArray[0]; // Using text from
//															// NGrain byte
//															// array--Should
//															// change to int
//															// array ***STK
//															// 6/20/12
//				float tDropVolume = (float) (Math.pow(tDropVal, 2) / 10000.0);
//
//				Log.d("Swarm", "tDropVolume = " + tDropVolume);
//				// TO DO: Make this a log function. . .
//				myOC_Pointer.myBusReader.amplitude.set(tDropVolume);

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

		else if (grain.appID == NAppIDAuk.DISCUSS_TOPIC)
			if (grain.command == NCommandAuk.SEND_MESSAGE) {
				String text = new String(grain.bArray);
				Log.d("Swarm", "Setting Discuss Topic");
				prompt.setText(text);
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
				prompt.setVisibility(View.VISIBLE);
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
				prompt.setVisibility(View.VISIBLE);
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

	Button.OnClickListener tonesTestButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!tonesToggle) {
				Log.d("Swarm", "Starting tones...");
				startTones();
			} else {
				stopTones();
			}
		}
	};
	
	Button.OnClickListener dropletsTestButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!dropletsToggle) {
				Log.d("Swarm", "Starting droplets...");
				startDroplets();
			} else {
				stopDroplets();
			}
		}
	};
	
	Button.OnClickListener sendDiscussListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			String value = message.getText().toString();
			Log.d("Swarm->Discuss", "value = " + value);
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
			Log.d("Swarm", "Cloud sent: " + value);
			byte[] cloudMsg = value.getBytes();
			sand.sendGrain(
					NAppIDAuk.OC_CLOUD,
					NCommandAuk.SEND_MESSAGE,
					NDataType.CHAR,
					cloudMsg.length,
					cloudMsg);
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

	public void playSound(String soundFile) {
		Log.d("Swarm", "playSound() started");

		for (int i = 0; i < mPlayer.length; i++) {
			if (!mPPlaying[i] && mPlayer[i] != null) {
				Log.d("Swarm", "mPPlaying[" + i + "] = " + mPPlaying[i]);
				mPPlaying[i] = true;

				try {
					if (mPlayer[i].isPlaying())
						mPlayer[i].stop();

					mPlayer[i].reset();
					AssetFileDescriptor afd = context.getAssets().openFd(
							soundFile);
					mPlayer[i].setDataSource(afd.getFileDescriptor(),
							afd.getStartOffset(), afd.getLength());
					afd.close();
					mPlayer[i].prepare();
					// currentPlayer.setLooping(true);
					// currentPlayer.seekTo(0);
					Log.d("Swarm", "playing MediaPlayer instance #: " + i);
					mPlayer[i].start();
					mPlayer[i]
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									for (int i = 0; i < mPlayer.length; i++) {
										if (mPlayer[i] == mp) {
											Log.d("Swarm",
													"mPlayer["
															+ i
															+ "] completed... setting mPPlaying[i] to false");
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
			if (tonesToggle) {
				 Log.d("Swarm", "tonesToggle is true");
				String soundfile = getSoundFileTones(app.getXY());
				playSound(soundfile);
				// updateStatus(); // change value of interval
				tonesHandler.postDelayed(tonesRunnable, tonesInterval);
			}
		}
	};
	
	Runnable dropletsRunnable = new Runnable() {
		@Override
		public void run() {
			if (dropletsToggle) {
				 Log.d("Swarm", "dropletsToggle is true");
				String soundfile = getSoundFileDroplets(app.getXY());
				playSound(soundfile);
				// updateStatus(); // change value of interval
				dropletsHandler.postDelayed(dropletsRunnable, dropletsInterval);
			}
		}
	};
	
	private String getSoundFileTones (float[] _xy) {
		String soundfile = "tones1.mp3";
		return soundfile;
	}
	
	private String getSoundFileDroplets (float[] _xy) {
		String soundfile = "1.mp3";
		return soundfile;
	}

	private void startTones() {
		tonesToggle = true;
		tonesRunnable.run();
	}

	private void stopTones() {
		tonesToggle = false;
		tonesHandler.removeCallbacks(tonesRunnable);
	}
	
	private void startDroplets() {
		dropletsToggle = true;
		dropletsRunnable.run();
	}

	private void stopDroplets() {
		dropletsToggle = false;
		dropletsHandler.removeCallbacks(tonesRunnable);
	}

	private void initializeMediaPlayers() {
		// Create array of new media players
		for (int i = 0; i < mPlayer.length; i++) {
			mPlayer[i] = new MediaPlayer();
		}
	}

	void releaseMediaPlayers() {
		// release the media players
		for (int i = 0; i < mPlayer.length; i++) {
			if (mPlayer[i] != null) {
				mPlayer[i].release();
				mPlayer[i] = null;
			}
		}
	}

	// ========================================================

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
		Log.d("Swarm", "onPause()");
		super.onPause();

		// destroy all media player instances
		releaseMediaPlayers();

		// turn off all audio playback
		tonesToggle = false;
		dropletsToggle = false;

		// turn on ringer (?)
		// app.phoneRingerState(true);
	}

	@Override
	protected void onResume() {
		Log.d("Swarm", "onResume()");
		super.onResume();
		app.setGrainTarget(GrainTarget.SWARM);

		// create array of media players
		initializeMediaPlayers();

		// turn off ringer
		app.phoneRingerState(false);
	}

	@Override
	protected void onStop() {
		Log.d("Swarm", "onStop()");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		Log.d("Swarm", "onRestart()");
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
