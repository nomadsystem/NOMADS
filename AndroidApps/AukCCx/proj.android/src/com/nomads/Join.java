package com.nomads;

import nomads.v210.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class Join extends Activity {
	public static Join join;
	Activity currentTarget;
	
	NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	
	TextView joinStatus;
	String tempString = "";
	Button buttonTest;
	
	int[] xy = new int[2];
	
	//========================================================
	// JNI methods
	//========================================================
	
	// native (c++) method; sets object reference
	public native void setObj ();
	
	static{
		System.loadLibrary("game");
	}
	
	public void touchPos(int tX, int tY) {
//		Log.i("Join.java", "x: " + tX + " y: " + tY);
		xy[0] = tX;
		xy[1] = tY;
		
		sand.sendGrain( NAppIDAuk.OC_POINTER, NCommandAuk.SEND_SPRITE_XY, NDataType.INT32, 2, xy );
    }
	
	//========================================================
	// Network methods
	//========================================================
	
	void tryConnect (){
		sand = new NSand();
		if (!sand.connect()) {
			Log.i("Join", "Connect failed");
			return;
		}
//		startThread();
		
		byte[] registerByte = new byte[1];
		registerByte[0] = 1;
		sand.sendGrain( NAppIDAuk.OPERA_CLIENT, NCommandAuk.REGISTER, NDataType.UINT8, 1, registerByte );
		
		
		// Switch to Swarm activity
		Intent intent = new Intent(getApplicationContext(), Swarm.class);
		startActivity(intent);
	}
	
	private class NomadsAppThread extends Thread {
		Join client; //Replace with current class name
		boolean active = true;

		public NomadsAppThread(Join _client) {
			client = _client;
		}
		
		public void kill() {
			active = false;
			Log.i("Join > NomadsAppThread", "active = false");
		}
		
		public void run() {			
//			NGlobals.lPrint("NomadsAppThread -> run()");
			while (active) {
//				try{
					grain = sand.getGrain();
					grain.print(); //prints grain data to console
					handle.post(updateUI);
//				}
//				catch (NullPointerException npe) {
//					Log.i("Join > NomadsAppThread", "NullPointerException");
//				}
			}
		}
		
		final Runnable updateUI = new Runnable() {
	    	@Override
	        public void run() {
				client.passGrain();
	        }
	    };
	}
	
	public synchronized void startThread() {
		if(nThread == null){
			nThread = new NomadsAppThread(this);
			nThread.start();
			Log.i("Join", "Thread started.");
		}
		else{
			Log.i("Join", "startThread: thread != null.");
		}
	}

	public synchronized void stopThread() {
		if(nThread != null){
			nThread.kill();
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("Join", "NomadsAppThread stopped.");
			sand.close();
			Log.i("Join", "sand.close()");
		}
	}
	
	private void passGrain() {
		if (currentTarget == this) {
			Log.i("Join", "parseGrain()");
//			String msg = new String(grain.bArray);
//			Log.i("Join", msg);

			if (grain != null)
				grain = null;
        }
//		else if (currentTarget instanceof Discuss) {
//            ((Discuss) currentTarget).parseGrain(grain);
//        }
//        else if (currentTarget instanceof Cloud) { 
//            ((Cloud) currentTarget).parseGrain(grain);
//        }
        else if (currentTarget instanceof Swarm) {
        	((Swarm) currentTarget).parseGrain(grain);
        }
        else {
        	Log.i("Join", "sendGrain() error: not a valid NSand target");
        }
	}
	
	public void setSandTarget(Activity _target) {
		currentTarget = _target;
		Log.i("Join", "setSandTarget()");
	}
	
	//========================================================
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		// set static join
		join = this;
		// set NSand target to this
		currentTarget = this;
		
		//send instance reference to Swarm.cpp
		setObj();
		
		// Connect
		tryConnect();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		joinStatus = (TextView)findViewById(R.id.joinStatus);
		buttonTest = (Button)findViewById(R.id.test);
		buttonTest.setOnClickListener(buttonSendOnClickListener);
	}
	
	//========================================================
	// Buttons
	//========================================================
	
	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String test = "HI";
			byte[] testMessage = test.getBytes();
			sand.sendGrain(NAppIDAuk.OC_DISCUSS, NCommandAuk.SEND_MESSAGE, NDataType.CHAR, 2, testMessage );
		}
	};
	
	//========================================================
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Join", "is resumed");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Join", "is paused");
//		stopThread();
	}
}