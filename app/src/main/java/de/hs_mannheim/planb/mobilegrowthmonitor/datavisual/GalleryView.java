package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.ProfileView;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;


public class GalleryView extends AppCompatActivity {
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("as", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    static ArrayList<Bitmap> bitmapList = new ArrayList<>();
    public static ArrayList<String> pathList = new ArrayList<>();
    GridView imageGrid;
    String profile_name;
    int profile_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);

        Bundle extras = getIntent().getExtras();
        this.profile_Id = extras.getInt("profile_Id");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("as", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d("as", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        bitmapList.clear();
        pathList.clear();
        if (hasFocus) {
            refreshView();
        }
    }

    public void refreshView() {
        imageGrid = (GridView) findViewById(R.id.gridview);
        bitmapList = new ArrayList<>();
        getFromSdCard();

        imageGrid.setAdapter(new ImageAdapter(this, bitmapList,pathList));
    }

    public void getFromSdCard() {

        // TODO: change this to Internal again after height measurement is finished
        //File folder = new File(getFilesDir().getPath() + File.separator +"MobileGrowthMonitor_pictures");
        File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");

        if (folder.isDirectory()) {
            File[] listFile = folder.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                try {
                    // TODO: this check is inaccurate. Consider using an Id or something else
                    // if (pathList.get(i).contains(profile_name)) {
                    pathList.add(listFile[i].getAbsolutePath());

                    bitmapList.add(urlImageToBitmap(pathList.get(i),false));
                    // }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static Bitmap urlImageToBitmap(String imageUrl,boolean hiRes) throws Exception {
        Bitmap result = null;
        if (imageUrl != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if(hiRes) {
                options.inSampleSize = 1;
            }else{
                options.inSampleSize = 10;

            }
            //result = rotateBitmap(BitmapFactory.decodeFile(imageUrl, options), 90);
            result = BitmapFactory.decodeFile(imageUrl, options);

        }
        return result;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", 1);
        startActivity(intent);

    }
}
