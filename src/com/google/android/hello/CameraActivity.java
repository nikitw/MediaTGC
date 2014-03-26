
package com.google.android.hello;






import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Toast;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;



import android.provider.MediaStore;
import android.util.Log;

import android.view.View.OnClickListener;

import android.widget.FrameLayout;


public class CameraActivity extends Activity {
	private static final String TAG = "CameraDemo";
	private static int i=0;
	public Intent musicIntent = new Intent();
	public Bitmap bmp,bmp1;
	public ImageView im;
	protected static final int TAKE_PHOTO_CODE = 0;
	Preview preview;
	public Context xc;
	Button buttonClick,buttonClick1;
	Dialog settingsDialog;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cammain);
        xc=this;
		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		
		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            startActivityForResult(intent, 1888);*/
	            preview.camera.takePicture(null, null, jpegCallback);
			}
		});
		


		
		Log.d(TAG, "onCreate'd");
	}
	
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};
	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();
	    
            	 	 
	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =9
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				bmp=BitmapFactory.decodeByteArray(data,0,data.length);
				bmp=toGrayscale(bmp);
				bmp1=bmp;
				int r,g,b,black=0,white=0;
				String str="";
		        for(int i=0;i<bmp.getWidth();i++)
		          	 for(int j=0;j<bmp.getHeight();j++)
		          	 {
		          		 {
		          			 int temp=bmp.getPixel(i, j);
		          			  r=(temp>>16)& 0xff;
		          			  g=(temp>>8)& 0xff;
		          			  b=temp & 0xff;
		          			  temp=(r+g+b)/3;
		          			 
		          			if(temp>=70)
		          				 {bmp1.setPixel(i, j, 0xff000000 | (255 << 16) | (255 << 8) | 255);white++;}
		          			else
		          				 {bmp1.setPixel(i, j, 0);black++;}
		          		 }
		          	 }
		        str+="Black: :  "+black;
		        Toast.makeText(xc, str, Toast.LENGTH_LONG).show();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp1.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] x= stream.toByteArray();
				
				
		
				/*
				File folder = new File(Environment.getExternalStorageDirectory() + "/mycam");
				if(!folder.exists())
				{	folder.mkdir();
				}  
				File filex = new File(Environment.getExternalStorageDirectory() + "/mycam/camimage"+i+".jpg");
				if(!filex.exists())
				{	
					outStream = new FileOutputStream(filex);
					outStream.write(x);
					outStream.flush();
					outStream.close();
				}
				else{
					outStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/mycam" ,"camimage"+i+"_0"+i+".jpg"));
					outStream.write(x);
					outStream.flush();
					outStream.close();
				}*/
				preview.camera.startPreview();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				//bmp.recycle();
				//bmp=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/mycam/camimage"+i+".jpg");
				settingsDialog = new Dialog(xc);
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.img,null));
				settingsDialog.show();
				im=(ImageView)settingsDialog.findViewById(R.id.imageXc);
				im.setImageBitmap(bmp1);
				
				goGetGesture();
				i++;
				im.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					            startActivityForResult(intent, 1888);*/
					            settingsDialog.dismiss();
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	public void goGetGesture()
	{
		int Mat[][]=new int[bmp.getWidth()][bmp.getHeight()];
		int Mat1[][]=new int[bmp.getWidth()][bmp.getHeight()];
		
        for(int i=0;i<bmp.getWidth();i++)
         	 for(int j=0;j<bmp.getHeight();j++)
         	 {
         		 Mat[i][j]=bmp.getPixel(i, j)& 0xff;
         		 if(i==0 || j==0 || i==bmp.getWidth()-1 || j==bmp.getHeight()-1)
         			 Mat[i][j]=255;
         	 }
        int mask[][]=new int[3][3];
        for(int i=0;i<3;i++)
        	for(int j=0;j<3;j++)
        	{
        		mask[i][j]=1;
        	}
        int white=0,black=0;
        for(int i=1;i<bmp.getWidth()-1;i++)
        	 for(int j=1;j<bmp.getHeight()-1;j++)
        	 {
        		 Mat1[i][j]=(Mat[i-1][j-1]*mask[0][0]+
        		 			Mat[i-1][j]*mask[0][1]+
        		 			Mat[i-1][j+1]*mask[0][2]+
        		 			Mat[i][j-1]*mask[1][0]+
        		 			Mat[i][j]*mask[1][1]+
        		 			Mat[i][j+1]*mask[1][2]+
        		 			Mat[i+1][j-1]*mask[2][0]+
        		 			Mat[i+1][j]*mask[2][1]+
        		 			Mat[i+1][j+1]*mask[2][2])/9;
        		 if(Mat1[i][j]>=225)white++;
        		 else black++;
        		 
        	 }
        if(black>=130000)
        {
        	musicIntent.setAction("com.android.music.musicservicecommand.togglepause");
	        musicIntent.putExtra("command", "togglepause"); // or "next" or "previous"
	        sendBroadcast(musicIntent);
	        return;
        }
            		

	}
	@Override
    public void onPause() {
		super.onPause();
	}
	@Override
	public void onStop()
	{
		super.onStop();
		preview.camera.release();
	}
	@Override
	public void onRestart()
	{
		super.onRestart();
		preview.camera.startPreview();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		preview.camera.release();
	}
	
}
