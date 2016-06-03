package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hs_mannheim.planb.mobilegrowthmonitor.ProfileView;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;

/**
 * Created by eikood on 09.05.2016.
 */
public class NativeCam extends Fragment implements SensorEventListener {

    // part of the name for the picture
    String profileName;

    // Native camera.
    private Camera mCamera;

    // View to display the camera output.
    private CameraPreview mPreview;

    private Activity mActivity;

    private Button captureButton;


    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    // used to convert radiant to degree
    private static final int FROM_RADS_TO_DEGS = -57;


    /**
     * Default empty constructor.
     */
    public NativeCam() {
        super();
    }


    /**
     * Static factory method
     *
     * @return
     */
    public static NativeCam newInstance(String profile_name) {
        NativeCam fragment = new NativeCam();
        fragment.profileName = profile_name;
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * OnCreateView fragment override
     * Also provides a reference to rotation vector sensor and registers Sensor Event Listener
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.native_cam, container, false);

        // Create our Preview view and set it as the content of our activity.
        boolean opened = safeCameraOpenInView(view);

        if (opened == false) {
            Log.d("CameraGuide", "Error, Camera failed to open");
            return view;
        }

        // Init the capture button.
        captureButton = (Button) view.findViewById(R.id.btn_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (mRotationSensor != null) {
            mSensorManager.registerListener((SensorEventListener) mActivity, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(mActivity, "Sensor not found!", Toast.LENGTH_LONG).show();
            captureButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    /**
     * Monitors the data from the rotation vector sensor and calls the update method
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    /**
     * Converts rotation vector in rotation matrix, remaps the rotation matrix to
     * portrait coordinate system. Based on the rotation matrix the orientation is
     * computed and the converts the angle in degrees.
     * The button to take a picture is only set visible, when the angle differs less than 5 degrees
     * in each direction.
     *
     * @param vectors
     */
    private synchronized void update(float[] vectors) {

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);

        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);

        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        ((TextView) mActivity.findViewById(R.id.pitch)).setText("Pitch: " + Math.round(pitch));
        ((TextView) mActivity.findViewById(R.id.roll)).setText("Roll: " + Math.round(roll));

        if (Math.round(pitch) < 5.0 && Math.round(pitch) > -5.0 && Math.round(roll) < 5.0 && Math.round(roll) > -5.0) {
            captureButton.setVisibility(View.VISIBLE);

        } else {

            captureButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * In onResume the sensorManager and rotationSensor have to be set again and the sensor event
     * listener has to be registered again.
     */
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Recommended "safe" way to open the camera.
     *
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        qOpened = (mCamera != null);

        if (qOpened == true) {
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, view);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    /**
     * Safe method for getting a camera instance.
     *
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * when fragment is destroyed the sensor event listener is unregistered and
     * releaseCameraAndPreview() is called
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
        if (mRotationSensor != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    /**
     * Surface on which the camera projects it's capture results. This is derived both from Google's docs and the
     * excellent StackOverflow answer provided below.
     * <p/>
     * Reference / Credit: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
     */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        // SurfaceHolder
        private SurfaceHolder mHolder;

        // Our Camera.
        private Camera mCamera;

        // Flash modes supported by this camera
        private List<String> mSupportedFlashModes;

        public CameraPreview(Context context, Camera camera, View cameraView) {
            super(context);

            // Capture the context
            setCamera(camera);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        /**
         * Begin the preview of the camera input.
         */
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         *
         * @param camera
         */
        private void setCamera(Camera camera) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            mCamera = camera;
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, mCameraInfo);
            mCamera.setDisplayOrientation(getCorrectCameraOrientation(mCameraInfo));
            mCamera.getParameters().setRotation(getCorrectCameraOrientation(mCameraInfo));

            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
            }
            requestLayout();
        }

        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         *
         * @param holder
         */
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Dispose of the camera preview.
         *
         * @param holder
         */
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        /**
         * React to surface changed events
         *
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            mCamera.stopPreview();
            try {
                Camera.Parameters parameters = mCamera.getParameters();

                // Set the auto-focus mode to "continuous"
                // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                // if-statement added so old smartphones are supported, checks whether
                // focus_mode_continous_picture is supported and only then sets the mode
                if (mCamera.getParameters().getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                parameters.setPreviewSize(1920, 1080);
                parameters.setPictureSize(1920, 1080);

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                try{
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPreviewSize(1280, 720);
                    parameters.setPictureSize(1280, 720);

                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                }catch(Exception f){
                    try{  Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setPreviewSize(768, 432);
                        parameters.setPictureSize(768, 432);

                        mCamera.setParameters(parameters);
                        mCamera.startPreview();

                    }catch (Exception g){

                    }
                }
            }
        }

        /**
         * Calculate the measurements of the layout
         *
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }

    // get Correct CameraView Orientation for rotation
    public int getCorrectCameraOrientation(Camera.CameraInfo info) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            final File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    Log.i("Thread", "started");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    final Bitmap turnedBitmap = ProfileView.rotateBitmap(bitmap, 90);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    turnedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    turnedBitmap.recycle();
                    byte[] byteArray = stream.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(pictureFile);
                        fos.write(byteArray);
                        fos.close();
                        Log.i("Thread", "finished");
                        NativeCam.this.onDestroy();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }).start();


            ((CameraView) getActivity()).afterPictureTaken();

        }
    };

    /**
     * Used to return the camera File output.
     */
    private File getOutputMediaFile() {
        // Create a media file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/growpics/" + timeStamp + "_filter.jpg";
        File testFile = new File(fileName);
        //mediaFile = new File(getActivity().getFilesDir().getPath() + File.separator +
        //"MobileGrowthMonitor_pictures" + File.separator + "IMG_" + profileName + "_" + timeStamp + ".jpg");
        Toast.makeText(getActivity(), "Success! Your picture has been saved! Loading Profile...", Toast.LENGTH_LONG)
                .show();
        return testFile;
    }
}