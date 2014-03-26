package com.google.android.hello;

import com.google.android.music.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.content.res.*;
public class TGCActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
                                // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;        // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, CameraActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("camera").setIndicator("Camera",
                          res.getDrawable(R.drawable.ic_tab_camera))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ProximityActivity.class);
        spec = tabHost.newTabSpec("proximity").setIndicator("Proximity",
                          res.getDrawable(R.drawable.ic_tab_proximity))
                      .setContent(intent);
        tabHost.addTab(spec);

        
        intent = new Intent().setClass(this, GravityActivity.class);
        spec = tabHost.newTabSpec("gravity").setIndicator("Gravity",
                          res.getDrawable(R.drawable.ic_tab_gravity))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
    }
}