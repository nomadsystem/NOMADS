package com.nomads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import nomads.v210.*;

public class Login extends Activity {
	EditText login;
	TextView textIn;
	Button buttonSend;
	String tempString = "";
//	LoginThread loginThread;
	Intent intent;
	
	NSand joinSand;
	private NomadsAppThread nThread;
	
	private class NomadsAppThread extends Thread {
		Login client; //Replace with current class name

		public NomadsAppThread(Login _client) {
			client = _client;
			joinSand = new NSand();
		    joinSand.connect();
		}
// Handle is not used yet (no incoming data)
		/*
		public void run() {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true)  {
				client.handle();
			}
		}
		*/
	}

	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.login);
	     login = (EditText)findViewById(R.id.textOut);
	     textIn = (TextView)findViewById(R.id.textIn);
	     buttonSend = (Button)findViewById(R.id.sendUser);
	     buttonSend.setOnClickListener(buttonSendOnClickListener);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Login", "is resumed");
		startThread();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Login", "is paused");
		stopThread();
	}
	
	public synchronized void startThread(){
		if(nThread == null){
			nThread = new NomadsAppThread(this);
			nThread.start();
			Log.i("Login", "Thread started.");
		}
		else{
			Log.i("Login", "startThread: thread != null.");
		}
	}

	public synchronized void stopThread(){
		if(nThread != null){
			joinSand.close();
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("Login", "Thread stopped.");
		}
	}
	
	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			buttonSend.setEnabled(false);
			String tString = login.getText().toString();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();

			joinSand.sendGrain((byte)NAppID.LOGIN, (byte)NCommand.SEND_MESSAGE, (byte)NDataType.BYTE, tLen, tStringAsBytes );


			// The data 
			NGlobals.cPrint("sending:  (" + tLen + ") of this data type");

			//                for (int i=0; i<tLen; i++) {
			//                NGlobals.cPrint("sending:  " + tString.charAt(i));
			//                streamOut.writeByte(tString.charAt(i));
			//                }

			NGlobals.cPrint("sending: (" + tString + ")");
			login.setText("");

			intent = new Intent().setClass(Login.this, TabbedBindle.class);
			startActivity(intent);
		}
	};
}