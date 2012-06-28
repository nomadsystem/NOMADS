// Cloud Client
// Nomads - University of Virginia
// 2012.05

package com.nomads;

import nomads.v210.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Cloud extends Activity {
	private NSand cloudSand;
	private NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	
	EditText input;
	TextView topic;
	TextView messageView;
	String tempString = "";
	Button speak;

	private class NomadsAppThread extends Thread {
		Cloud client; //Replace with current class name

		public NomadsAppThread(Cloud _client) {
			client = _client;
			cloudSand = new NSand();
			cloudSand.connect();
		}
		
		public void run() {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true) {
				grain = cloudSand.getGrain();
				grain.print(); //prints grain data to console
				handle.post(updateUI);
			}
		}
		
		final Runnable updateUI = new Runnable() {
	    	@Override
	        public void run() {
				client.setUI();
	        }
	    };
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.cloud);
	    input = (EditText)findViewById(R.id.textout);
		topic = (TextView)findViewById(R.id.promptin);
		messageView = (TextView)findViewById(R.id.messageViewer);
		speak = (Button)findViewById(R.id.send);
		speak.setOnClickListener(buttonSendOnClickListener);
		
		startThread();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Cloud", "is resumed");
//		startThread();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Cloud", "is paused");
//		stopThread();
	}
	
	public synchronized void startThread(){
		if(nThread == null){
			nThread = new NomadsAppThread(this);
			nThread.start();
			Log.i("CloudThread", "Thread started.");
		}
		else{
			Log.i("CloudThread", "startThread: cloudThread == !null.");
		}
	}

	public synchronized void stopThread(){
		if(nThread != null){
			cloudSand.close();
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("CloudThread", "Thread stopped.");
		}
	}
	
	public void setUI() {
		Log.i("Cloud", "setUI()");

		String msg = new String(grain.bArray);
		
		if (grain.appID == NAppID.DISCUSS_PROMPT) {
			topic.setText(msg);
			tempString = new String(msg);
		}
		// Disable discuss when the student panel button is off
		else if (grain.appID == NAppID.INSTRUCTOR_PANEL) {
			if (msg.equals("DISABLE_DISCUSS_BUTTON")) {
				speak.setEnabled(false);
				topic.setText("Discuss Disabled");
			}
			else if (msg.equals("ENABLE_DISCUSS_BUTTON")) {
				speak.setEnabled(true);
				topic.setText(tempString);
			}			
		}
		else {
			grain = null;
		}
		if (grain != null)
			grain = null;
	}
	 
	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			String tString = input.getText().toString();
			int tLen = tString.length();
			byte[] tStringAsBytes = tString.getBytes();

			cloudSand.sendGrain((byte)NAppID.CLOUD_CHAT, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );
			
			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			input.setText("");
			input.requestFocus();
		}
	};
}