package com.cauliflower.phase.vi;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class PictureSurfaceView extends GLSurfaceView { 
	public GlRenderer r;

	public PictureSurfaceView(GameActivity gameActivity, GameInstance game) {       
		super(gameActivity); 
		r = new GlRenderer(gameActivity,game);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(r);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);
	}  
}
