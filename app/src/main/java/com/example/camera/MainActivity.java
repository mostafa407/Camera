package com.example.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_INTENT = 1;
    Camera mCamera;
    HorizontalScrollView horizontalScrollView;
    private final int PERMISSION_CONSTANT=1000;
    private ImageView imageFlashLight;
    private static final  int CAMERA_REQUEST=50;
    private boolean flashLightStatus=false;
    private static OrientationEventListener orientationEventListener=null;
    private static boolean fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        Button ivCapture=findViewById ( R.id.ivCapture );
        ImageView ivFilter=findViewById ( R.id.ivFilter );
        ImageView imageView=findViewById ( R.id.image_view );

        FrameLayout frameLayout=findViewById ( R.id.r1CameraPreview );
        horizontalScrollView =findViewById ( R.id.filterLayout );
        ivCapture.setOnClickListener ( this );
        ivFilter.setOnClickListener ( this );


        if (ContextCompat.checkSelfPermission ( MainActivity.this,
                Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions ( MainActivity.this ,
                    new String[]{
                            Manifest.permission.CAMERA
                    }  ,100);
        }

ivCapture.setOnClickListener ( new View.OnClickListener ( ) {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        startActivityForResult ( intent,100 );
    }
} );

     /*   final boolean hasCameraFlash=getPackageManager ().hasSystemFeature ( PackageManager.FEATURE_CAMERA_FLASH );
        boolean isEnable= ContextCompat.checkSelfPermission ( this,Manifest.permission.CAMERA  )
                ==PackageManager.PERMISSION_GRANTED;
        imageFlashLight.setEnabled ( isEnable );
CameraManager cameraManager=(CameraManager)getSystemService ( Context.CAMERA_SERVICE );
        try {
            String cameraId= cameraManager.getCameraIdList ()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode ( cameraId,true );
                flashLightStatus=true;
                imageFlashLight.setImageResource ( R.drawable.ic_flash_on );
            }
        } catch (CameraAccessException e) {

        }

*/

    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==100){
            assert data != null;
            Bitmap captureImage=(Bitmap)data.getExtras ().get ( "data" );
            ImageView imageView=findViewById ( R.id.image_view );
            


        }
    }

    private  void checkPermissionAndGive(){
        initialize();
    }
    private void initialize(){
       // mCamera = getCameraInstance();
        mCamera=Camera.open ();
        CameraPreview mPreview=new CameraPreview (this,mCamera);
        FrameLayout r1cameraPreview=findViewById ( R.id.r1CameraPreview );
        r1cameraPreview.addView ( mPreview );
        rotateCamera ();


        orientationEventListener=new OrientationEventListener (this ) {
            @Override
            public void onOrientationChanged(int orientation) {
                   rotateCamera ();

            }
        };
        orientationEventListener.enable ();
        mPreview.setOnLongClickListener ( new View.OnLongClickListener ( ) {
            @Override
            public boolean onLongClick(View v) {
                if (whichCamera){
                    if (fm){
                        p.setFocusMode ( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE );
                    }else {
                        p.setFlashMode ( Camera.Parameters.FLASH_MODE_AUTO );
                    }
                    try {
                        mCamera.setParameters ( p );
                    }catch (Exception e){

                    }
                    fm=!fm;
                }
                return true;
            }
        } );
    }
    @Override
    protected void onPause() {
        super.onPause ( );
        releaseCamera ();


        }

    private void releaseCamera(){
        if (mCamera!=null){

            mCamera.release ();
            orientationEventListener.disable ();
            mCamera=null;
            whichCamera=!whichCamera;
        }
    }
    private static List<String>camEffects;

    private static boolean hasFlash(){
          camEffects=p.getSupportedColorEffects ();
        final List<String>flashModes=p.getSupportedFlashModes ();
        if (flashModes==null){
            return false;
        }
        for (String flashMode :flashModes){
            if (Camera.Parameters.FLASH_MODE_ON.equals ( flashMode )){
                return true;
            }

        }
        return false;
    }

    private int rotation;
    private static boolean whichCamera = true;
    private static Camera.Parameters p;
    private void rotateCamera(){
        if (mCamera!=null){
            rotation=this.getWindowManager ().getDefaultDisplay ().getRotation ();
            if (rotation==0){
                rotation=90;
            }else if (rotation==1){
                rotation=0;
            }else if (rotation==2){
                rotation=270;
            }else {
                rotation=180;
            }mCamera.setDisplayOrientation ( rotation );
            if (!whichCamera){
                if (rotation==90){
                    rotation= 270;
                }else  if (rotation==270){
                    rotation=90;
                }
            }
            p=mCamera.getParameters ();
            p.setRotation ( rotation );
            mCamera.setParameters ( p );

        }
    }

    private static Camera.Parameters parameters;
    private Camera getCameraInstance(){
        Camera c=null;
        try {
            c= Camera.open ();
        }catch (Exception e){
            e.printStackTrace ();
        }
        return c;
    }
    private  Camera.PictureCallback mPicture=new Camera.PictureCallback ( ) {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile=getOutputMediaFile();
            if (pictureFile==null){
                return;
            }
            MediaScannerConnection.scanFile ( MainActivity.this,
                    new String[]{pictureFile.toString ( )},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener ( ) {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            mCamera.startPreview ();
                        }
                    } );
            try {
                FileOutputStream fos= new FileOutputStream ( pictureFile );
                fos.write ( data );
                fos.close ();

            } catch (FileNotFoundException e) {
                e.printStackTrace ( );
            }catch (IOException e){
                e.printStackTrace ();
            }

        }
    } ;

    private File getOutputMediaFile() {
        File mediaStorageDir=new File ( Environment.getExternalStoragePublicDirectory ( Environment.DIRECTORY_PICTURES ) ,"My images");
        if (!mediaStorageDir.exists ()){
            if (!mediaStorageDir.mkdirs ()){
                return null;
            }
        }
        SecureRandom random=new SecureRandom (  );
        int num=random.nextInt ( 100000 );
        return new File ( mediaStorageDir.getAbsolutePath ()+File.separator +"IMG_"+num + ".jpg" );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId ()){
            case R.id.ivFilter:
                if (horizontalScrollView.getVisibility ()==View.VISIBLE){
                    horizontalScrollView.setVisibility ( View.GONE );
                }else {
                    horizontalScrollView.setVisibility ( View.VISIBLE );
                }
                break;
            case R.id.ivCapture:
                mCamera.takePicture ( null,null,mPicture );



                  }
           /* case R.id.Gallery:

                Intent intent1=new Intent (  );
                intent1.setType ( "image/*" );
                intent1.putExtra ( intent1.EXTRA_ALLOW_MULTIPLE, true );
                intent1.setAction ( intent1.ACTION_GET_CONTENT );
                startActivityForResult ( intent1.createChooser ( intent1,"Select Picture(s)" ), PICK_IMAGE_INTENT);

*/



        }
    private void flashLightOn(){
       try {
           parameters.setFlashMode ( Camera.Parameters.FLASH_MODE_TORCH );
           mCamera.setParameters ( parameters );
           mCamera.startPreview ();
           flashLightStatus=true;
           imageFlashLight.setImageResource ( R.drawable.ic_flash_on );
       }catch (Exception e){
           Toast.makeText ( this, e.toString (), Toast.LENGTH_LONG ).show ( );
       }


    }
    private void flashLightOff(){

        try {
            parameters.setFlashMode ( Camera.Parameters.FLASH_MODE_TORCH );
            mCamera.setParameters ( parameters );
            mCamera.startPreview ();
            flashLightStatus=false;
            imageFlashLight.setImageResource ( R.drawable.ic_flash_off );
        }catch (Exception e){
            Toast.makeText ( this, e.toString (), Toast.LENGTH_LONG ).show ( );

        }
    }


    public  void colorEffectFilter(View v){
        try {
            Camera.Parameters parameters=mCamera.getParameters ();
            switch (v.getId ()){
                case  R.id.r1None:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_NONE );
                    mCamera.setParameters ( parameters );
                    break;
                case R.id.r1Aqua:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_AQUA );
                    mCamera.setParameters ( parameters );
                    break;
                case R.id.r1BlackBoard:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_BLACKBOARD );
                    mCamera.setParameters ( parameters );
                    break;
                case R.id.r1Mono:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_MONO );
                    mCamera.setParameters ( parameters );
                    break;
                case R.id.r1Negative:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_NEGATIVE );
                    mCamera.setParameters ( parameters );
                    break;
                case R.id.r1CameraPreview:
                    parameters.setColorEffect ( Camera.Parameters.EFFECT_WHITEBOARD );
                    mCamera.setParameters ( parameters );
                    break;
            }

        }catch (Exception e){

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        if (mCamera==null){
            mCamera.release ();
            mCamera=null;
        }
    }

}

