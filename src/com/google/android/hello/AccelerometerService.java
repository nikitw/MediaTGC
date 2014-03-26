package com.google.android.hello;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class AccelerometerService extends Service {
	public com.android.music.IMediaPlaybackService service;
	public boolean flag=false;
	public SensorManager sm;
	public SensorEvent snew;
	public int count=0;
	AccelerometerSensorListener axSensorListener;
	public long lastUpdate;
	public boolean color;
	public View view;
	public Intent musicIntent=new Intent(android.content.Intent.ACTION_VIEW);
	public class AccelerometerSensorListener implements SensorEventListener{
		
		Sensor axSensor;
		
		
		public AccelerometerSensorListener() {
			axSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			
		}
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			// Movement
			float x = values[0];
			float y = values[1];
			

			
			long actualTime = System.currentTimeMillis();
			if(x<=-8){
				if (actualTime - lastUpdate < 200) {
					return;
				}
				lastUpdate = actualTime;
				 musicIntent.setAction("com.android.music.musicservicecommand.next");
			        musicIntent.putExtra("command", "next"); // or "next" or "previous"
			        sendBroadcast(musicIntent);
			        return;
			}
			if(x>=8){
				if (actualTime - lastUpdate < 200) {
					return;
				}
				lastUpdate = actualTime;
				 musicIntent.setAction("com.android.music.musicservicecommand.previous");
			        musicIntent.putExtra("command", "prev"); // or "next" or "previous"
			        sendBroadcast(musicIntent);
			        return;				
			}
			if(y<=-8){
				if (actualTime - lastUpdate < 200) {
					return;
				}
				lastUpdate = actualTime;
				 musicIntent.setAction("com.android.music.musicservicecommand.togglepause");
			        musicIntent.putExtra("command", "togglepause"); // or "next" or "previous"
			        sendBroadcast(musicIntent);
			        return;				
			}
			if(y>=8){
				if (actualTime - lastUpdate < 200) {
					return;
				}
				lastUpdate = actualTime;
				 musicIntent.setAction("com.android.music.musicservicecommand.pause");
			        musicIntent.putExtra("command", "pause"); // or "next" or "previous"
			        sendBroadcast(musicIntent);
			        return;				
			}

		}

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	public void registerListener() {
        sm.registerListener(axSensorListener, axSensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	public void unregisterListener() {
        sm.unregisterListener(axSensorListener, axSensor);
	}
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    	axSensorListener = new AccelerometerSensorListener();
    	axSensorListener.registerListener(); 
    	
		
		Toast.makeText(getApplicationContext(), "Accl service started", Toast.LENGTH_LONG).show();
		lastUpdate = System.currentTimeMillis();
	}
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Accl service stopped", Toast.LENGTH_LONG).show();
		axSensorListener.unregisterListener();
	}
	@Override
	public void onStart(Intent intent, int startid) {
	}
}
	