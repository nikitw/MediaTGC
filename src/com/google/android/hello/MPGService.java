package com.google.android.hello;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MPGService extends Service {
	public com.android.music.IMediaPlaybackService service;
	public boolean flag=false;
	public SensorManager sm;
	public SensorEvent snew;
	PTimer pTimer;
	public long upd;
	public int count=0,start=0;
	public Intent musicIntent = new Intent();
	ProximitySensorListener pxSensorListener;
public class ProximitySensorListener implements SensorEventListener{
	
		Sensor pxSensor;
		boolean inProximity = false;
		
		public ProximitySensorListener() {
		pxSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			//Toast.makeText(getApplicationContext(),"accuracy:" + accuracy, Toast.LENGTH_SHORT).show();
		}

		public void onSensorChanged(SensorEvent event) {
			snew=event;
			//Toast.makeText(getApplicationContext(),"prox reading:"""+event.values[1]+""+event.values[2], Toast.LENGTH_SHORT).show();
			long actualTime=0;
			count++;
			if(start==1)
			{	actualTime=upd;start=0;}
			
			
			
			if(count%2==0){
			try{
				long lastupdate = System.currentTimeMillis();
				if(lastupdate-actualTime>=1000)
				{
					musicIntent.setAction("com.android.music.musicservicecommand.togglepause");
			        musicIntent.putExtra("command", "togglepause"); // or "next" or "previous"
			        sendBroadcast(musicIntent);
			        return;
				}
					
				Toast.makeText(getApplicationContext(),"next ", Toast.LENGTH_SHORT).show();
				
				        musicIntent.setAction("com.android.music.musicservicecommand.next");
				        musicIntent.putExtra("command", "next"); // or "next" or "previous"
				        sendBroadcast(musicIntent);
				        return;
				}
			catch(Exception e){}
			}
			else
			{
				actualTime=System.currentTimeMillis();
				upd=actualTime;
				start=1;
			}			
		}

		public void registerListener() {
	        sm.registerListener(pxSensorListener, pxSensor, SensorManager.SENSOR_DELAY_UI);
		}
		
		public void unregisterListener() {
	        sm.unregisterListener(pxSensorListener, pxSensor);
		}

		public boolean isInProximity() {
			return inProximity;
		}

		
	}

public class PTimer extends android.os.CountDownTimer {
	private boolean running;

	private int tickCount;
	
	
	public PTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		running = false;
	}
	
	public android.os.CountDownTimer fire() {
		if (running)
			stop();
		running = true;
		
		tickCount = 0;
		
		//if (km.inKeyguardRestrictedInputMode())
		//if (!pm.isScreenOn())
		
		return start();
	}
	
	public void stop() {
		cancel();
		running = false;
	}

	@Override
	public void onFinish() {
		running = false;
		
	}

	@Override
	public void onTick(long millisUntilFinished) {
		
	 tickCount++;
	
		
			
		}
	}
	
//this is only compatible with 2.2.1 and below stack trace not available in 2.3 and above
//media playerPlayback services are blocked in 2.3 MediaPlaybackService.AIDL is not configured public
public class MediaPlayerServiceConnection implements ServiceConnection {
	

    public void onServiceConnected(ComponentName name, IBinder boundService) {
        Log.i("MediaPlayerServiceConnection", "Connected! Name: " + name.getClassName());

        // This is the important line
       service = com.android.music.IMediaPlaybackService.Stub.asInterface((IBinder) boundService);
        stopService(new Intent(getApplicationContext(), ProximityToolService.class));
        // If all went well, now we can use the interface
        
       //==============thers some problem here======================//
        
       
        
        
        
      
         
    }      
       
    

    public void onServiceDisconnected(ComponentName name) {
        service=null;
        
       
       // Log.i("MediaPlayerServiceConnection", "Disconnected!");
    }
}





	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
	
		stopService(new Intent(getApplicationContext(), ProximityToolService.class));
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    	pxSensorListener = new ProximitySensorListener();
    pxSensorListener.registerListener(); 
    	
		
		Toast.makeText(getApplicationContext(), "MPG service started", Toast.LENGTH_LONG).show();
	//Intent i = new Intent();
      //  i.setClassName("com.android.music", "com.android.music.MediaPlaybackService");
         
       //ServiceConnection conn = new MediaPlayerServiceConnection();
        //this.bindService(i, conn, 0);
	}
	public void onDestroy() {
	//	sm.unregisterListener(sensorEventListener);
		pxSensorListener.unregisterListener();
		startService(new Intent(getApplicationContext(), ProximityToolService.class));
		ProximityActivity.checkBox.setChecked(true);
		Toast.makeText(getApplicationContext(), "MPG service stopped", Toast.LENGTH_SHORT).show();
		Toast.makeText(getApplicationContext(), "Proximity tool service started", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onStart(Intent intent, int startid) {
	}
}