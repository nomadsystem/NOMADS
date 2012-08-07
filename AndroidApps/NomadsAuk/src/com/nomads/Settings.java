package com.nomads;

import nomads.v210.NGrain;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends Activity
{
	NomadsApp app;
	
	TextView connectionStatus, nomadsLink;
	Button quitButton;
	String connectedMessage;
	final Context context = this;
	AlertDialog.Builder alert;
	private NGrain grain;
	
	@Override
	 public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		app = (NomadsApp)getApplicationContext();
		
		// send reference of Swarm to NomadsApp
		app.setSettings(this);
		
		setContentView(R.layout.settings);
		connectionStatus = (TextView)findViewById(R.id.connectionStatus);
		nomadsLink = (TextView)findViewById(R.id.nomadsLink);
		nomadsLink.setMovementMethod(LinkMovementMethod.getInstance());
		quitButton = (Button)findViewById(R.id.quitButton);
		quitButton.setOnClickListener(quitListener);
		
		setConnectedMessage(app.isConnected());
	}
	
	public void setConnectedMessage (boolean _connected)
	{
		if (_connected)
		{
			connectionStatus.setText(
					" Server: " + app.sand.getServerName() +
					"\n Port: " + app.sand.getServerPort() );
		}
		else
		{
			connectionStatus.setText("Not currently connected to the server.");
		}
	}
	
	//========================================================
	// Buttons
	//========================================================
	
	Button.OnClickListener quitListener = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			quitAlert();
		}
	};
	
	protected void quitAlert ()
	{
		alert = new AlertDialog.Builder(context);
		// need to create new input field each time
		
		alert.setTitle("Really quit?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
//				app.stopThread();
//				Join.instance.finish();
//				Swarm.instance.finish();
//				finish();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// Canceled.
			}
		});
		
		alert.show();
	}
	
	//========================================================
	// Network
	//========================================================
	
	public void parseGrain(NGrain _grain)
	{
		grain = _grain;

		Log.i("Settings", "parseGrain() invoked");
//		String msg = new String(grain.bArray);
//		Log.i("Swarm", msg);
//
		if (grain != null)
			grain = null;
	}
	
	//========================================================
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i("Join", "is resumed");
		setConnectedMessage(app.isConnected());
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i("Join", "is paused");
//		stopThread();
	}
}