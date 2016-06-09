package de.hs_mannheim.planb.mobilegrowthmonitor.pinlock;

/**
 * BaseActivity for all Activities of MobileGrowthMonitor
 * !every Activity must extend BaseActivity so it is AppLock-secured!
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static PageListener pageListener;

    public static void setListener(PageListener listener) {
        pageListener = listener;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pageListener != null) {
            pageListener.onActivityCreated(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (pageListener != null) {
            pageListener.onActivityStarted(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageListener != null) {
            pageListener.onActivityResumed(this);
        }
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pageListener != null) {
            pageListener.onActivityPaused(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (pageListener != null) {
            pageListener.onActivityStopped(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pageListener != null) {
            pageListener.onActivityDestroyed(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (pageListener != null) {
            pageListener.onActivitySaveInstanceState(this);
        }
    }
}
