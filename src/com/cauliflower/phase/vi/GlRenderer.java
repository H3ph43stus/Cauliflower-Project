package com.cauliflower.phase.vi;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class GlRenderer implements Renderer,SensorEventListener {

	private Square itemSquare;		// the square
	private Square monsterSquare;
	private GameActivity activity;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	private float values[] = new float[3];
	private float ydeg = -80;
	private float xdifmax = 20;
	private float ydifmax = 10;
	private float alpha = (float) 0.9;
	private float minDist = 100;

	private static int D2uD = 1000000;
	private int currentX = 0;
	private int currentY = 0;
	private GameInstance game;
	private boolean noPoints = true;
	private float xvals[];
	private float yvals[];
	
	private int j = 0;
	

	LocationManager locationManager;
	LocationListener locationListener = new LocationListener(){
		public void onLocationChanged(Location arg0) {
			currentX = (int)(arg0.getLongitude() * D2uD);
			currentY = (int)(arg0.getLatitude() * D2uD);
			game.setCurX(currentX);
			game.setCurY(currentY);
		}

		public void onProviderDisabled(String provider) {}

		public void onProviderEnabled(String provider) {}

		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	@SuppressWarnings("deprecation")
	public GlRenderer(GameActivity gameActivity, GameInstance game) {
		this.activity = gameActivity;
		// initialise the square
		this.itemSquare = new Square(R.drawable.burr);
		this.monsterSquare = new Square(R.drawable.slendie);
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		values[0] = 0;
		values[1] = 0;
		values[2] = 0;

		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		this.game = game;
	}

	public void onDrawFrame(GL10 gl) {
		if(game.isDead){
			return;
		}
		
		int mX = game.getMonsterX();
		int mY = game.getMonsterY();
		float mLongdif = mX - currentX;
		float mLatdif = mY - currentY;
		float mDeg = (float) Math.toDegrees(Math.atan(mLatdif/mLongdif));
		if(mLongdif > 0)
			mDeg = 90 - mDeg;
		else
			mDeg = 270 - mDeg;
		float mDist = (float) Math.sqrt(mLongdif*mLongdif + mLatdif*mLatdif);
		
		float clear = 0;
		if(mDist < 1000){
			clear = (float) (0.75 * (1000.0 - mDist)/(1000.0));
		}
		gl.glClearColor(0, 0, 0, clear);
		
		// clear Screen and Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Reset the Modelview Matrix
		gl.glLoadIdentity();

		if(noPoints){
			if(game.isReady()){
				xvals = game.loadXValues();
				yvals = game.loadYValues();
				noPoints = false;
				game.startMonster();
				Log.d("points","Got points");
			}
		}
		else{
			drawItems(gl);
		}
		
		if(mDist < (minDist * 0.5)){
			//dead
			Log.d("died","player died");
			game.hasDied();
			game.stopMonster();
			activity.end(game.username + " from group " + game.groupName + " has been killed","You have been killed: " + game.getScore() + "/" + game.found.length + " pages found");
		}
		
		float xdif = mDeg - values[0];
		float ydif = ydeg - values[1];
		float xscale = ((xdif + xdifmax) / (2 * xdifmax)) * 6 - 3;
		float yscale = -(((ydif + ydifmax) / (2 * ydifmax)) * 4 - 2);
		float zscale = 300/mDist;//-(mDist*mDist)/2000;
//		if(++j%100 == 0){
//			Log.d("monster","Deg: " + mDeg + " dist: " + (int)mDist + " at: " + (int)xscale + "," + (int)yscale + "," + zscale + " " + (int)mLongdif + "," + (int)mLatdif);
//			j = 0;
//		}
		
		gl.glLoadIdentity();
		gl.glTranslatef(xscale, yscale, -5);		
		gl.glScalef(zscale, zscale, zscale);
		monsterSquare.draw(gl);
	}

	private void drawItems(GL10 gl) {
		for(int i = 0; i < xvals.length; i++){
			if(game.isFound(i))
				continue;
			float longdif = xvals[i] - currentX;
			float latdif = yvals[i] - currentY;
			float deg = (float) Math.toDegrees(Math.atan(latdif/longdif));
			if(longdif > 0){
				deg = 90 - deg;
			}
			else{
				deg = 270 - deg;
			}
			float dist = (float) Math.sqrt(longdif*longdif + latdif*latdif);
			if(dist < minDist){
				Log.d("score","player scored");
				if(game.pointFound(i)){
					//won
					//game.hasDied();
					game.stopMonster();
					
					activity.end(game.username + " from group " + game.groupName + " has won the game","You have won the game: All " + game.score + " pages found");
				}
//				String m = "Found page " + game.getScore();
//				if(firstScore){
//					Looper.prepare();
//					firstScore = false;
//				}
//				Toast.makeText(activity.getApplicationContext(), m, Toast.LENGTH_SHORT).show();
//				activity.sendToast(m);
			}
//			Log.d("location",i + " Deg: " + deg + " dist: " + dist);
//			Log.d("dif","longdif " + i + ": " + longdif + " latdif: " + latdif);

			float xdif = deg - values[0];
			float ydif = ydeg - values[1];
			float xscale = ((xdif + xdifmax) / (2 * xdifmax)) * 6 - 3;
			float yscale = -(((ydif + ydifmax) / (2 * ydifmax)) * 4 - 2);
			float zscale = 100/dist;//-(mDist*mDist)/2000;
			
			gl.glLoadIdentity();
			gl.glTranslatef(xscale, yscale, -5);		
			gl.glScalef(zscale, zscale, zscale);			
			itemSquare.draw(gl);
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Load the texture for the square
		itemSquare.loadGLTexture(gl, this.activity);
		monsterSquare.loadGLTexture(gl, this.activity);

		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do

		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); 

		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void onClose(){
		game.stopMonster();
		locationManager.removeUpdates(locationListener);
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		//		Log.d("sensor", "Getting values: " + (int)event.values[0] + "," + (int)event.values[1]);
		values[0] = (1-alpha)*event.values[0] + alpha*values[0];
		values[1] = (1-alpha)*event.values[1] + alpha*values[1];
		values[2] = (1-alpha)*event.values[2] + alpha*values[2];
	}

}
