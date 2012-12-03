package com.cauliflower.phase.vi;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class PictureSurfaceView extends GLSurfaceView { 
	public GlRenderer r;

	public PictureSurfaceView(Context context, GameInstance game, GameActivity gameActivity) {       
		super(context); 
		r = new GlRenderer(context,game,gameActivity);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(r);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);

	}  
}
