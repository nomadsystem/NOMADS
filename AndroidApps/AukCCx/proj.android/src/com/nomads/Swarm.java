/****************************************************************************
Copyright (c) 2010-2012 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
// modified by Paul Turowski. 2012.07

package com.nomads;

import nomads.v210.NGrain;
import nomads.v210.NSand;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxEditText;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxRenderer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class Swarm extends Cocos2dxActivity{
	private Cocos2dxGLSurfaceView mGLView;
	NSand sand;
	NGrain grain;
	
	Button buttonTest;
	ImageButton imgButton;
	final Context context = this;
	AlertDialog.Builder alert;
	EditText alertInput;
	
	public void parseGrain(NGrain _grain) {
//		grain = _grain;
//
		Log.i("Swarm", "parseGrain() invoked");
//		String msg = new String(grain.bArray);
//		Log.i("Swarm", msg);
//
		if (grain != null)
			grain = null;
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// get NSand instance from Join
		sand = Join.join.sand;
		
		// set sandTarget to Swarm activity instance
		Join.join.setSandTarget(this);
		
		if (detectOpenGLES20()) {
			// get the packageName,it's used to set the resource path
			String packageName = getApplication().getPackageName();
			super.setPackageName(packageName);
			
            // RelativeLayout
            ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(
                		ViewGroup.LayoutParams.FILL_PARENT,
                		ViewGroup.LayoutParams.FILL_PARENT);
//            RelativeLayout relativeLayout = new RelativeLayout(this);
            setContentView(R.layout.swarm);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
//            relativeLayout.setLayoutParams(framelayout_params);
//
//            // Cocos2dxEditText layout
//            ViewGroup.LayoutParams edittext_layout_params =
//                new ViewGroup.LayoutParams(
//                		ViewGroup.LayoutParams.FILL_PARENT, 
//                		ViewGroup.LayoutParams.WRAP_CONTENT);
//            Cocos2dxEditText edittext = new Cocos2dxEditText(this);
//            edittext.setLayoutParams(edittext_layout_params);
//
//            // ...add to FrameLayout
//            relativeLayout.addView(edittext);

            // Cocos2dxGLSurfaceView
	        mGLView = new Cocos2dxGLSurfaceView(this);

            // ...add to FrameLayout
	        relativeLayout.addView(mGLView, 0);

	        mGLView.setEGLContextClientVersion(2);
	        mGLView.setCocos2dxRenderer(new Cocos2dxRenderer());
//            mGLView.setTextField(edittext);
            
            // TEST BUTTON
            ViewGroup.LayoutParams discuss_layout_params =
                    new ViewGroup.LayoutParams(
                    		ViewGroup.LayoutParams.WRAP_CONTENT,
                    		ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonTest = new Button(this);
            buttonTest.setText("DISCUSS");
//            buttonTest = (Button)findViewById(R.id.button1);
//            buttonTest.setLayoutParams(discuss_layout_params);
            relativeLayout.addView(buttonTest, discuss_layout_params);
//            relativeLayout.addView(buttonTest);
            
            // TEST IMAGE BUTTON
//            ViewGroup.LayoutParams discuss2_layout_params =
//                    new ViewGroup.LayoutParams(
//                    		ViewGroup.LayoutParams.WRAP_CONTENT,
//                    		ViewGroup.LayoutParams.WRAP_CONTENT);
//            imgButton = new ImageButton(this);
//            imgButton.setBackgroundResource(R.drawable.ic_tab_test_grey);
//            imgButton.setLayoutParams(discuss2_layout_params);
//            relativeLayout.addView(imgButton);

            // Set framelayout as the content view
//			setContentView(relativeLayout);
			
//			alert = new AlertDialog.Builder(context);
//			// need to create new input field each time
//			alertInput = new EditText(context);
//			
//			alert.setTitle("Discuss:");
////			alert.setMessage("Message");
//			alert.setView(alertInput);
//
//			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//			  String value = alertInput.getText().toString();
//			  	Log.d("Discuss", value);
//			  }
//			});
//
//			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			  public void onClick(DialogInterface dialog, int whichButton) {
//			    // Canceled.
//			  }
//			});
//			
//			alert.show();
		}
		else {
			// include message for user
			Log.d("activity", "doesn't support gles2.0");
			finish();
		}	
	}
	
//	void goToJoin() {
//		Intent intent = new Intent(getApplicationContext(), Join.class);
//		startActivity(intent);
//	}
	
	 @Override
	 protected void onPause() {
	     super.onPause();
	     mGLView.onPause();
	 }

	 @Override
	 protected void onResume() {
		 
	     super.onResume();
	     mGLView.onResume();
	 }
	 
	 private boolean detectOpenGLES20() 
	 {
	     ActivityManager am =
	            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	     ConfigurationInfo info = am.getDeviceConfigurationInfo();
	     return (info.reqGlEsVersion >= 0x20000);
	 }
	 
	 // moved to Join.java
//     static {
//         System.loadLibrary("game");
//     }
}
