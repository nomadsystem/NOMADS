// Swarm.java
// Nomads Auksalaq
// Paul Turowski. 2012.08.02

package com.nomads;

import nomads.v210.*;
import nomads.v210.NGlobals.GrainTarget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
//import android.content.Intent;

public class Swarm extends Activity
{
	// setup singleton
	public static Swarm instance;

	private NSand sand;
//	private NGrain grain;
	
//	Button buttonDiscuss, buttonCloud, buttonSettings;
	ImageButton buttonDiscuss, buttonCloud, buttonSettings;
	final Context context = this;
	AlertDialog.Builder alert;
	EditText alertInput;
	
	public void parseGrain(NGrain _grain)
	{
//		grain = _grain;
//
		Log.i("Swarm", "parseGrain() invoked");
//		String msg = new String(grain.bArray);
//		Log.i("Swarm", msg);
//
//		if (grain != null)
//			grain = null;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("Swarm", "onCreate()");
		super.onCreate(savedInstanceState);
		
		// get NSand instance from Join
		sand = Join.instance.getSand();
		
		setContentView(R.layout.swarm);
		
		// set button onClickListeners
		buttonDiscuss = (ImageButton)findViewById(R.id.buttonDiscuss);
		buttonDiscuss.setOnClickListener(discussListener);
		buttonCloud = (ImageButton)findViewById(R.id.buttonCloud);
		buttonCloud.setOnClickListener(cloudListener);
		buttonSettings = (ImageButton)findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(settingsListener);
		
	}
	
//	void goToJoin() {
//		Intent intent = new Intent(getApplicationContext(), Join.class);
//		startActivity(intent);
//	}
	
	//========================================================
	// Buttons
	//========================================================
	
	Button.OnClickListener discussListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			discussAlert();
		}
	};
	
	Button.OnClickListener cloudListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			cloudAlert();
		}
	};
	
	Button.OnClickListener settingsListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), Settings.class);
			startActivity(intent);
		}
	};
	
	//========================================================
	
	protected void discussAlert ()
	{
		alert = new AlertDialog.Builder(context);
		// need to create new input field each time
		alertInput = new EditText(context);
		
		alert.setTitle("Discuss:");
//		alert.setMessage("Message");
		alert.setView(alertInput);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String value = alertInput.getText().toString();
				Log.d("Swarm->Discuss", value);
				byte[] discussMsg = value.getBytes();
				// eventually use this:
				// char[] discussMsg = value.toCharArray();
				sand.sendGrain(NAppIDAuk.OC_DISCUSS, NCommandAuk.SEND_MESSAGE, NDataType.CHAR, discussMsg.length, discussMsg );
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
	
	protected void cloudAlert ()
	{
		alert = new AlertDialog.Builder(context);
		// need to create new input field each time
		alertInput = new EditText(context);
		
		alert.setTitle("Cloud:");
//		alert.setMessage("Message");
		alert.setView(alertInput);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String value = alertInput.getText().toString();
				Log.d("Swarm->Discuss", value);
				byte[] cloudMsg = value.getBytes();
				sand.sendGrain(NAppIDAuk.OC_CLOUD, NCommandAuk.SEND_MESSAGE, NDataType.CHAR, cloudMsg.length, cloudMsg );
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
	
	@Override
	protected void onPause()
	{
		Log.i("Swarm", "onPause()");
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		Log.i("Swarm", "onResume()");
		super.onResume();
		Join.instance.setGrainTarget(GrainTarget.SWARM);
	}
}
