package edu.doane.ist.camtest8;

import android.app.Activity;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Prototype camera application using deprecated Camera object, displaying images to a custom
 * SurfaceView.
 *
 * @author Mark M. Meysenburg
 * @version 11/21/2017
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    /** Custom SurfaceView that displays the camera preview. */
    private MySurfaceView mSurfaceView;

    /** Object holding the MySurfaceView object. */
    private SurfaceHolder mSurfaceHolder;

    /** Reference to the camera. */
    private Camera mCamera;

    /**
     * Set up the main activity for this application.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     *                           null.
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize MySurfaceView and SurfaceHolder objects
        mSurfaceView = (MySurfaceView)findViewById(R.id.mySurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();

        // add this object as handler for MySurfaceView callbacks
        mSurfaceHolder.addCallback(this);
    }

    /**
     * When the MySurfaceView object becomes available, get a reference to the camera, set
     * its parameters, etc.
     *
     * @param surfaceHolder SurfaceHolder object holding the MySurfaceView
     */
    @Override public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // open the camera
        try {
            mCamera = Camera.open();
        } catch (RuntimeException e) {
            Log.e("CAMERA ACCESS", e.toString());
            return;
        }

        // configure size of preview image
        Camera.Parameters param = mCamera.getParameters();

        // dump preview sizes to the LogCat console for testing purposes
//        List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
//        for(Camera.Size s : previewSizes) {
//            Log.d("PS:", "(" + s.width + ", " + s.height + ")");
//        }

        // dump image sizes to the LogCat console for testing purposes
//        List<Camera.Size> imageSizes = param.getSupportedPictureSizes();
//        for(Camera.Size s : imageSizes) {
//            Log.d("PS:", "(" + s.width + ", " + s.height + ")");
//        }

        param.setPreviewSize(960, 720);

        // try to apply close-up focus, steadying, and fluorescent lights
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        param.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
        param.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);

        mCamera.setParameters(param);

        // start camera preview
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e("CAMERA PREVIEW", e.toString());
            return;
        }
    }

    /**
     * When the MySurfaceView has changed orientation or size, adjust display orientation and
     * restart the preview.
     *
     * @param surfaceHolder SurfaceHolder object holding the MySurfaceView
     * @param format The new PixelFormat of the surface
     * @param width The new width of the surface
     * @param height The new height of the surface
     */
    @Override public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                         int height) {

        // rotate the image to match the orientation of the phone
        setCameraDisplayOrientation(this, 0, mCamera);

        // only continue if the surface already exists
        if(mSurfaceHolder.getSurface() != null) {

            // stop the preview if it is already happening
            try {
                mCamera.stopPreview();
            } catch (Exception e) {

            }

            // set the preview display to the new surface, and then restart the preview
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.e("SURFACE CHANGED", e.toString());
            }
        }
    }

    /**
     * Stop the camera preview and release the camera when the MySurfaceView is destroyed.
     *
     * @param surfaceHolder SurfaceHolder object holding the MySurfaceView
     */
    @Override public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * Set the orientation of the camera preview to match the orientation of the phone.
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    public void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
