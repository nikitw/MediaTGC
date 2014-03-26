package com.google.android.hello;
import android.app.KeyguardManager;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

public class ProximityToolService extends Service {
	public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
	 SensorManager sm;
	KeyguardManager km;
	AudioManager am;
	PowerManager pm;
	PowerManager.WakeLock wl;
	PTimer pTimer;
	boolean wiredHeadSet = false;
		
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public boolean isWiredHeadSetOn() {
		return wiredHeadSet /*am.isWiredHeadsetOn()*/;
	}
	
	public boolean isHeadSetPluggedIn() {
		return (isWiredHeadSetOn() || am.isBluetoothA2dpOn());
	}
	
	public void turnProximityToolServiceOn(/*boolean check*/) {
		if (true||isHeadSetPluggedIn()) {
			if (!wl.isHeld()) {
				wl.acquire();
				Toast.makeText(getApplicationContext(), "Proximity Tool Service sense ENABLED.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void turnProximityToolServiceOff(boolean check) {
		if (!check || !isHeadSetPluggedIn()) {
			if (wl.isHeld()) {
				wl.release();
				Toast.makeText(getApplicationContext(), "Proximity Tool Service sense DISABLED.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	BroadcastReceiver headSetPlugReceiver;
	public class HeadSetPlugReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getExtras().getInt("state") == 0) {
				wiredHeadSet = false;
				turnProximityToolServiceOff(true);
			}
			else {
				wiredHeadSet = true;
				turnProximityToolServiceOn();
			}
		}

	}

	int remoteAudioClass = BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE | BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES | BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO; 
	BroadcastReceiver bluetoothConnectReceiver;
	public class BluetoothConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice btDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
			int btDevClass = btDevice.getBluetoothClass().getDeviceClass();
			if ((btDevClass & remoteAudioClass) != 0)
			{
				turnProximityToolServiceOn();
			}
		}

	}
	
	BroadcastReceiver bluetoothDisconnectReceiver;
	public class BluetoothDisconnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice btDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
			int btDevClass = btDevice.getBluetoothClass().getDeviceClass();
			if ((btDevClass & remoteAudioClass) != 0)
			{
				turnProximityToolServiceOff(true);
			}
		}

	}

	public class PTimer extends android.os.CountDownTimer {
		private boolean running;
		/*
		private int tickCount;
		*/
		
		public PTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			running = false;
		}
		
		public android.os.CountDownTimer fire() {
			if (running)
				stop();
			running = true;
			/*
			tickCount = 0;
			*/
			//if (km.inKeyguardRestrictedInputMode())
			//if (!pm.isScreenOn())
			turnProximityToolServiceOn();
			return start();
		}
		
		public void stop() {
			cancel();
			running = false;
		}

		@Override
		public void onFinish() {
			running = false;
			//if (! km.inKeyguardRestrictedInputMode())
				turnProximityToolServiceOff(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			/*
			tickCount += 1;
			*/
			if (/*tickCount >= 1 &&*/ pm.isScreenOn()) {
				stop();
				turnProximityToolServiceOff(true);
			}
		}
		
	}
	
	BroadcastReceiver phoneStateChangeReceiver;
	public class PhoneStateChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String newState = intent.getStringExtra("state");
			//Toast.makeText(getApplicationContext(), "Phone state: " + newState, Toast.LENGTH_SHORT).show();
			if ( android.telephony.TelephonyManager.EXTRA_STATE_IDLE.equals(newState) ) {
				pTimer.fire();
			}
			if ( android.telephony.TelephonyManager.EXTRA_STATE_RINGING.equals(newState) ) {
				pTimer.stop();
				turnProximityToolServiceOn();
			}
			if ( android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK.equals(newState) ) {
				pTimer.stop();
				turnProximityToolServiceOff(true);
			}
		}

	}
	
	
	@Override
	public void onCreate() {
		
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		wl = pm.newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "PTWLTAG");
		long screenLockTimeout = 15000;
		try {
			screenLockTimeout = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		pTimer = new PTimer(screenLockTimeout, 5000);
		headSetPlugReceiver = new HeadSetPlugReceiver();
		bluetoothConnectReceiver = new BluetoothConnectReceiver();
		bluetoothDisconnectReceiver = new BluetoothDisconnectReceiver();
		phoneStateChangeReceiver = new PhoneStateChangeReceiver();
		registerReceiver(bluetoothConnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
		registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
		registerReceiver(phoneStateChangeReceiver, new IntentFilter(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED));
		registerReceiver(headSetPlugReceiver, new IntentFilter(android.content.Intent.ACTION_HEADSET_PLUG));
		
		Toast.makeText(getApplicationContext(), "Proximity Tool Service started.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		
		pTimer.cancel();
		unregisterReceiver(headSetPlugReceiver);
		unregisterReceiver(bluetoothConnectReceiver);
		unregisterReceiver(bluetoothDisconnectReceiver);
		/*
		pxSensorListener.unregisterListener();
		*/
		turnProximityToolServiceOff(false);
		Toast.makeText(getApplicationContext(), "Proximity Tool Service stopped.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
	}
}
