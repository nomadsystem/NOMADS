package com.nomads;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
//import android.view.View;
import android.widget.TabHost;



public class TabbedBindle extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

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

	    tabHost.setCurrentTab(0);
	}
}
