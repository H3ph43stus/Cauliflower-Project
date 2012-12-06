package com.cauliflower.phase.vi;

import java.io.IOException;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Intent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GameActivity extends Activity implements SurfaceHolder.Callback{
	private Camera camera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private PictureSurfaceView mGLSurfaceView;
	
	private int monsterX = -78498800;
	private int monsterY =  38040420;
	
	private GameInstance game;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle data = getIntent().getExtras();
		String username = data.getString("username");
		String group = data.getString("groupName");
		game = new GameInstance(group,username);
		game.update();
		game.setMonsterX(monsterX);
		game.setMonsterY(monsterY);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mSurfaceView = new SurfaceView(this);
		addContentView(mSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		mGLSurfaceView = new PictureSurfaceView(this, game); 
		addContentView(mGLSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT); 
		
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
		camera = Camera.open();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mGLSurfaceView.r.onClose();
		camera.stopPreview();
		camera.release();
	}

	public void end(String string, String string2) {
		Intent intent = new Intent(this, TwitterTestActivity.class);
		intent.putExtra("message",string);
		intent.putExtra("endtext", string2);
		startActivity(intent);
	}

	public void sendToast(String m) {
		Looper.prepare();
		Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
	}
}
