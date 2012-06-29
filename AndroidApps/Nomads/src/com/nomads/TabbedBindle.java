package com.nomads;

import nomads.v210.*;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TabHost;

public class TabbedBindle extends TabActivity {
	Activity currentActivity;
	final Handler handle = new Handler();
	
	NSand sand;
	private NGrain grain;
	private NomadsAppThread nThread;
	
	private class NomadsAppThread extends Thread {
		TabbedBindle client; //Replace with current class name

		public NomadsAppThread(TabbedBindle _client) {
			client = _client;
			sand = new NSand();
			sand.connect();
		}
		
		public void run() {			
			NGlobals.lPrint("NomadsAppThread -> run()");
			while (true) {
				grain = sand.getGrain();
				grain.print(); //prints grain data to console
				handle.post(updateUI);
			}
		}
		
		final Runnable updateUI = new Runnable() {
	    	@Override
	        public void run() {
				client.sendGrain();
	        }
	    };
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    // Thread needs to start before initializing tabs
	    // since the reference to sand is sent based on the active tab
	    startThread();

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    tabHost.setOnTabChangedListener(TabChangeListener);

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, DiscussClient.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("discuss")
	    	.setIndicator("Discuss", res.getDrawable(R.drawable.ic_tab_test))
	    	.setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, Cloud.class);
	    spec = tabHost.newTabSpec("cloud")
	    	.setIndicator("Cloud", res.getDrawable(R.drawable.ic_tab_test))
	    	.setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Poll.class);
	    spec = tabHost.newTabSpec("poll")
	    	.setIndicator("Poll", res.getDrawable(R.drawable.ic_tab_test))
	    	.setContent(intent);
	    tabHost.addTab(spec);
	    
//	    tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);

	    // Set the first active tab
	    tabHost.setCurrentTab(0);
	}

			TabHost.OnTabChangeListener TabChangeListener = new TabHost.OnTabChangeListener() {
	        @Override
	        public void onTabChanged(String tabId) {
	            currentActivity = getCurrentActivity();
	            if (currentActivity instanceof DiscussClient) {
	                ((DiscussClient) currentActivity).setSand(sand);
	            }
	            else if (currentActivity instanceof Cloud) { 
	                ((Cloud) currentActivity).setSand(sand);
	            }
	            else if (currentActivity instanceof Poll) { 
	                ((Poll) currentActivity).setSand(sand);
	            }
	        }
		};
	
	private void sendGrain() {
		if (currentActivity instanceof DiscussClient) {
            ((DiscussClient) currentActivity).parseGrain(grain);
        }
        else if (currentActivity instanceof Cloud) { 
            ((Cloud) currentActivity).parseGrain(grain);
        }
        else if (currentActivity instanceof Poll) { 
            ((Poll) currentActivity).parseGrain(grain);
        }
	}
	
	public synchronized void startThread() {
		if(nThread == null){
			nThread = new NomadsAppThread(this);
			nThread.start();
			Log.i("TabbedBindle", "Thread started.");
		}
		else{
			Log.i("TabbedBindle", "startThread: thread != null.");
		}
	}

	public synchronized void stopThread() {
		if(nThread != null){
			sand.close();
			Thread moribund = nThread;
			nThread = null;
			moribund.interrupt();
			Log.i("TabbedBindle", "Thread stopped.");
		}
	}
}
