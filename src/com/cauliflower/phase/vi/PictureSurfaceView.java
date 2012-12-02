package com.cauliflower.phase.vi;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class PictureSurfaceView extends GLSurfaceView { 
	public GlRenderer r;

	public PictureSurfaceView(Context context) {       
		super(context); 
		r = new GlRenderer(context);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(r);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);

	}  
	
	public PictureSurfaceView(Context context, AttributeSet attrs) {       
		super(context,attrs); 
		r = new GlRenderer(context);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setRenderer(r);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);  
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);

	}  

}
