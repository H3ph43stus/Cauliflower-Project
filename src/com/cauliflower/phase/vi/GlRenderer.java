package com.cauliflower.phase.vi;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GlRenderer implements Renderer,SensorEventListener {

	private Square 		square;		// the square
	private Context 	context;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float values[] = new float[3];
	private float xdeg = 120;
	private float ydeg = -80;
	private float xdifmax = 20;
	private float ydifmax = 15;
	float alpha = (float) 0.9;
	
	
	/** Constructor to set the handed over context */
	@SuppressWarnings("deprecation")
	public GlRenderer(Context context) {
		this.context = context;
		
		// initialise the square
		this.square = new Square();
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		values[0] = 0;
		values[1] = 0;
		values[2] = 0;
	}

	public void onDrawFrame(GL10 gl) {
		// clear Screen and Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Reset the Modelview Matrix
		gl.glLoadIdentity();

		float xdif = xdeg - values[0];
		float ydif = ydeg - values[1];
		float xscale = ((xdif + xdifmax) / (2 * xdifmax)) * 6 - 3;
		float yscale = -(((ydif + ydifmax) / (2 * ydifmax)) * 4 - 2);
		float zscale = values[2]/30;
		Log.d("draw","Draw at: " + xscale + " " + yscale);
//		if(Math.abs(xdif) > xdifmax || Math.abs(ydif) > ydifmax)
//			return;
		// Drawing
		gl.glTranslatef(xscale, yscale, -5+zscale);		// move 5 units INTO the screen
												// is the same as moving the camera 5 units away
//		gl.glScalef(0.5f, 0.5f, 0.5f);			// scale the square to 50% 
												// otherwise it will be too large
		square.draw(gl);						// Draw the triangle
		
//		gl.glTranslatef(2f, 0f, 0f);
		
//		square.draw(gl);

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
		square.loadGLTexture(gl, this.context);
		
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
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		Log.d("sensor", "Getting values: " + (int)event.values[0] + "," + (int)event.values[1]);
		values[0] = (1-alpha)*event.values[0] + alpha*values[0];
		values[1] = (1-alpha)*event.values[1] + alpha*values[1];
		values[2] = (1-alpha)*event.values[2] + alpha*values[2];
	}

}
