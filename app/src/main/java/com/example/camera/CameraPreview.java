package com.example.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview  extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    Camera mCamera;

    public CameraPreview( Context context, Camera mCamera) {
        super ( context );
        this.mCamera=mCamera;
        this.mHolder=getHolder ();
        mHolder.addCallback ( this );
        mHolder.setType ( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
