package com.nomads;

//import com.nomads.TabbedBindle.NomadsAppThread;

import nomads.v210.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

public class Join extends Activity {
	Join join;
	NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
	final Handler handle = new Handler();
	
	EditText loginID;
	TextView loginPrompt, connectedMsg;
	Button buttonConnect, buttonDisconnect;
	String tempString = "";
	
	// native (c++) method; sets object reference
	public native void setObj ();
	static{
		System.loadLibrary("Join");
	}
	
	
//	public void touchPos(int tX, int tY) {
//		// for below, use (int[] touchPos)
////		int x = touchPos[0];
////		int y = touchPos[1];
//		Log.i("Join.java", "x: " + tX + " y: " + tY);
////		Log.i("Swarm.java", "x: " + touchPos);
//        
//    }
	
//	public static void goToJoin() {
//		backToJoin();
//	}
//	
//	void backToJoin () {
//		Intent intent = new Intent(getApplicationContext(), Join.class);
//		startActivity(intent);
//	}
	
	
	boolean tryConnect (){
		sand = new NSand();
		if (!sand.connect()) {
			Log.i("Join", "Connect failed");
			return false;
		}
		startThread();
		return true;
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
				try{
					grain = sand.getGrain();
					grain.print(); //prints grain data to console
					handle.post(updateUI);
				}
				catch (NullPointerException npe) {
					Log.i("Join > NomadsAppThread", "NullPointerException");
				}
			}
		}
		
		final Runnable updateUI = new Runnable() {
	    	@Override
	        public void run() {
				client.parseGrain(grain);
	        }
	    };
	}
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		
		//send reference to this object instance to Swarm.cpp
		new Join().setObj();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		loginID = (EditText)findViewById(R.id.textOut);
		loginPrompt = (TextView)findViewById(R.id.loginPrompt);
		buttonConnect = (Button)findViewById(R.id.sendUser);
		buttonConnect.setOnClickListener(buttonSendOnClickListener);
		connectedMsg = (TextView)findViewById(R.id.connectedMsg);
		buttonDisconnect = (Button)findViewById(R.id.disconnect);
		buttonDisconnect.setOnClickListener(buttonDisconnectOnClickListener);
	}
	
	public void parseGrain(NGrain _grain) {
		grain = _grain;

		Log.i("Join", "parseGrain()");
		String msg = new String(grain.bArray);
		Log.i("Join", msg);

//		if (grain.appID == NAppID.DISCUSS_PROMPT) {
//			topic.setText(msg);
//			tempString = new String(msg);
//		}
//		// Disable discuss when the student panel button is off
//		else if (grain.appID == NAppID.INSTRUCTOR_PANEL) {
//			if (msg.equals("DISABLE_DISCUSS_BUTTON")) {
//				speak.setEnabled(false);
//				topic.setText("Discuss Disabled");
//				chatWindow.setText("");
//			}
//			else if (msg.equals("ENABLE_DISCUSS_BUTTON")) {
//				speak.setEnabled(true);
//				topic.setText(tempString);
//			}			
//		}
//		else if (grain.appID == NAppID.WEB_CHAT || grain.appID == NAppID.SERVER){
//			chatWindow.append(msg + "\n");
//			input.requestFocus();
//		}
//		else {
//			grain = null;
//		}
		if (grain != null)
			grain = null;
	}
	
	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			// test connection
			if (tryConnect()){
				// Hide EditText and Connect Button
				loginPrompt.setVisibility(View.GONE);
				loginID.setVisibility(View.GONE);
				buttonConnect.setVisibility(View.GONE);
				
				String tString = loginID.getText().toString();
				int tLen = tString.length();
				//    char[] tStringAsChars = tString.toCharArray();
				byte[] tStringAsBytes = tString.getBytes();
	
				sand.sendGrain(NAppID.BINDLE, NCommand.LOGIN, NDataType.CHAR, tLen, tStringAsBytes );
	
				// The data 
				Log.i("Join", "sending:  (" + tLen + ") of this data type");
	
//	                for (int i=0; i<tLen; i++) {
//	                Log.i("Join", "sending:  " + tString.charAt(i));
//	                streamOut.writeByte(tString.charAt(i));
//	                }
	
				Log.i("Join", "sending: (" + tString + ")");
				loginID.setText("");
				
				// hide soft keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(loginID.getWindowToken(), 0);
	
				connectedMsg.setText(tString + " is now connected!");
				connectedMsg.setVisibility(View.VISIBLE);
				buttonDisconnect.setVisibility(View.VISIBLE);
	//			tabbedBindle.setTabs(1);
				
				Intent intent = new Intent(getApplicationContext(), Swarm.class);
				startActivity(intent);
			}
		}
	};
	
	Button.OnClickListener buttonDisconnectOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			Log.i("Join", "Disconnected");
			stopThread();
			
			connectedMsg.setVisibility(View.GONE);
			buttonDisconnect.setVisibility(View.GONE);
			
			loginID.setText("");
			loginPrompt.setVisibility(View.VISIBLE);
			loginID.setVisibility(View.VISIBLE);
			buttonConnect.setVisibility(View.VISIBLE);
			
			// hide soft keyboard
//			InputMethodManager imm = (InputMethodManager)getSystemService(
//				      Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(loginID.getWindowToken(), 0);
			
//			tabbedBindle.setTabs(0);
		}
	};
	
//	public void setSand(NSand _sand) {
//		sand = _sand;
//		Log.i("Join", "setSand()");
//	}
//	
//	public void setTB(TabbedBindle _tb){
//		tabbedBindle = _tb;
//	}
	
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
}