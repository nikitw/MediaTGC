package com.google.android.hello;
import java.util.List;

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
public class GravityActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxmain);
        boolean isacclrunning=false;
        ActivityManager am = (ActivityManager) this.getSystemService (ACTIVITY_SERVICE);
        List<RunningServiceInfo> rsi = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo runningServiceInfo : rsi) {
        	if (runningServiceInfo.service.getClassName().equals(AccelerometerService.class.getName())) {
				isacclrunning = true;}
        }
        android.widget.TableLayout tl=new android.widget.TableLayout(this);  
        TableRow tr=new TableRow(this); 
        TextView tv=new TextView(this);
        tv.setText("Enable");
        tv.setTextSize(20);
        final android.widget.CheckBox checkBox=new android.widget.CheckBox(this);
        checkBox.setChecked(isacclrunning);
        checkBox.setWidth(LayoutParams.WRAP_CONTENT);
        tr.addView(tv);
        tr.addView(checkBox);
        tl.addView(tr); 
        setContentView(tl);
        checkBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(checkBox.isChecked()){
					startService(new Intent(getApplicationContext(), AccelerometerService.class));
					//ProximityToolService.mpg=true;
					
				}
				else{//ProximityToolService.mpg=false;
				stopService(new Intent(getApplicationContext(), AccelerometerService.class));
				}
				
			}
		
   	});
    }
}