package com.nomads;

import nomads.v210.*;

import android.app.Activity;
import android.content.Context;
//import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Join extends Activity {
	TabbedBindle tabbedBindle;
	NSand sand;
	NGrain grain;
	
	EditText loginID;
	TextView loginPrompt, connectedMsg;
	Button buttonConnect, buttonDisconnect;
	String tempString = "";
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
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
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Join", "is resumed");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Join", "is paused");
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
			// Hide EditText and Connect Button
			loginPrompt.setVisibility(View.GONE);
			loginID.setVisibility(View.GONE);
			buttonConnect.setVisibility(View.GONE);
			
			String tString = loginID.getText().toString();
			int tLen = tString.length();
			//    char[] tStringAsChars = tString.toCharArray();
			byte[] tStringAsBytes = tString.getBytes();
			
			// should be Bindle, register, uint8, 1, 1

			sand.sendGrain(NAppID.BINDLE, NCommand.LOGIN, NDataType.CHAR, tLen, tStringAsBytes );


			// The data 
			Log.i("Join", "sending:  (" + tLen + ") of this data type");

//                for (int i=0; i<tLen; i++) {
//                Log.i("Join", "sending:  " + tString.charAt(i));
//                streamOut.writeByte(tString.charAt(i));
//                }

			Log.i("Join", "sending: (" + tString + ")");
			loginID.setText("");
			
			// hide soft keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(loginID.getWindowToken(), 0);

			connectedMsg.setText(tString + " is now connected!");
			connectedMsg.setVisibility(View.VISIBLE);
			buttonDisconnect.setVisibility(View.VISIBLE);
			tabbedBindle.setTabs(1);
		}
	};
	
	Button.OnClickListener buttonDisconnectOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			connectedMsg.setVisibility(View.GONE);
			buttonDisconnect.setVisibility(View.GONE);

			Log.i("Join", "Disconnected");
			loginID.setText("");
			
			// hide soft keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(loginID.getWindowToken(), 0);
			
			tabbedBindle.setTabs(0);
			loginPrompt.setVisibility(View.VISIBLE);
			loginID.setVisibility(View.VISIBLE);
			buttonConnect.setVisibility(View.VISIBLE);
		}
	};
	
	public void setSand(NSand _sand) {
		sand = _sand;
		Log.i("Join", "setSand()");
	}
	
	public void setTB(TabbedBindle _tb){
		tabbedBindle = _tb;
	}
}