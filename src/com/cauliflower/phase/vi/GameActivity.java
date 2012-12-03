package com.cauliflower.phase.vi;

import java.io.IOException;

import com.google.android.maps.GeoPoint;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements SurfaceHolder.Callback{
	private Camera camera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private PictureSurfaceView mGLSurfaceView;
	
	private GeoPoint monsterLoc = new GeoPoint(-78498878,38040464);
	private int monsterX = -78498800;
	private int monsterY = 38040420;
	
	private GameInstance game;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mSurfaceView = new SurfaceView(this);
		addContentView(mSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		mGLSurfaceView = new PictureSurfaceView(this, game); 
		addContentView(mGLSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
//		setContentView(R.layout.activity_main);
//		mGLSurfaceView = (PictureSurfaceView) findViewById(R.id.pictureSurfaceView1);
//		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT); 
		
		Bundle data = getIntent().getExtras();
		String username = data.getString("username");
		String group = data.getString("group");
		game = new GameInstance(group,username);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(arg2, arg3);
		try {
			camera.setPreviewDisplay(arg0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mGLSurfaceView.r.onClose();
		camera.stopPreview();
		camera.release();
	}
}
