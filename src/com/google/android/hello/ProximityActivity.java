package com.google.android.hello;

import java.util.List;



import com.google.android.hello.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import android.widget.TableRow;
import android.widget.TextView;


public class ProximityActivity extends Activity {	
	
	public static android.widget.CheckBox checkBox;
	
	// new code
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxmain);
        boolean ismpgRunning= false,isServiceRunning = false;
        
        ActivityManager am = (ActivityManager) this.getSystemService (ACTIVITY_SERVICE);
        List<RunningServiceInfo> rsi = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo runningServiceInfo : rsi) {
			if (runningServiceInfo.service.getClassName().equals(ProximityToolService.class.getName())) {
				isServiceRunning = true;}
			if (runningServiceInfo.service.getClassName().equals(MPGService.class.getName())) {
					ismpgRunning = true;}
			}
		
        
      //  if (! isServiceRunning)
       // 	startService(new Intent(getApplicationContext(), ProximityToolService.class));
       // else
       // 	stopService(new Intent(getApplicationContext(), ProximityToolService.class));
        android.widget.TableLayout tl=new android.widget.TableLayout(this);  
        TableRow tr=new TableRow(this); 
        TextView tv=new TextView(this);
        tv.setText("Enable");
        
        tv.setTextSize(20);
        //tv.setWidth(350);
        checkBox=new android.widget.CheckBox(this);
        checkBox.setChecked(isServiceRunning);
        checkBox.setWidth(LayoutParams.WRAP_CONTENT);
        tr.addView(tv);
        tr.addView(checkBox);
        TableRow tr1=new TableRow(this); 
        TextView tv1=new TextView(this);
        tv1.setText("Media Gestures");
        tv1.setTextSize(20);
        //tv1.setWidth(350);
        final android.widget.CheckBox checkBox1=new android.widget.CheckBox(this);
        
        checkBox1.setWidth(LayoutParams.WRAP_CONTENT);
        checkBox1.setClickable(false);
        if(checkBox.isChecked())
        	checkBox1.setVisibility(android.view.View.VISIBLE);
        checkBox1.setChecked(ismpgRunning);
        tr1.addView(tv1);
        tr1.addView(checkBox1);
        tl.addView(tr); 
         tl.addView(tr1);
        
        setContentView(tl);
 
        checkBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		        if (checkBox.isChecked()) {
		        	checkBox1.setClickable(true);
		        	startService(new Intent(getApplicationContext(), ProximityToolService.class));
		        } else {
		        	checkBox1.setChecked(false);
		        	checkBox1.setClickable(false);
		        	stopService(new Intent(getApplicationContext(), MPGService.class));
		        	stopService(new Intent(getApplicationContext(), ProximityToolService.class));
		        }
			}
		});
       checkBox1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(checkBox1.isChecked()){
					startService(new Intent(getApplicationContext(), MPGService.class));
					//ProximityToolService.mpg=true;
					
				}
				else{//ProximityToolService.mpg=false;
				stopService(new Intent(getApplicationContext(), MPGService.class));
				}
				
			}
		
    	});
        }
        
        
	
	@Override
    public void onPause() {
		super.onPause();
        
	}
}