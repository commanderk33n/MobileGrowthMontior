package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import de.hs_mannheim.planb.mobilegrowthmonitor.ProfileView;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;


public class GalleryView extends AppCompatActivity {
    public static final String TAG = GalleryView.class.getSimpleName();
    public static ArrayList<String> pathList;
    GridView imageGrid;
    int profile_Id;


    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);

        Bundle extras = getIntent().getExtras();
        this.profile_Id = extras.getInt("profile_Id");


        //  writeGif();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  onWindowFocusChanged(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("Gallery", "onFocusChanged");

        if (pathList == null) {
            pathList = new ArrayList<>(
            );
        }
        if (hasFocus) {
            File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");
            File[] listFile = folder.listFiles();



                if (folder.isDirectory()) {

                    refreshView();
                }
             else {
                if (pathList.size() == 0) {
                    refreshView();
                }
                imageGrid = (GridView) findViewById(R.id.gridview);

                imageGrid.setAdapter(new ImageAdapter(this, pathList));

            }
        }
    }

    public void refreshView() {
        // File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");

        getFromSdCard(this);
        imageGrid = (GridView) findViewById(R.id.gridview);
        imageGrid.setAdapter(new ImageAdapter(this, pathList));
    }

    public static void getFromSdCard(Context context) {

        // TODO: change this to Internal again after height measurement is finished
        //File folder = new File(getFilesDir().getPath() + File.separator +"MobileGrowthMonitor_pictures");
        File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");

        if (folder.isDirectory()) {
            File[] listFile = folder.listFiles();
            pathList.clear();
            for (int i = 0; i < listFile.length; i++) {

                try {
                    // TODO: this check is inaccurate. Consider using an Id or something else
                    // if (pathList.get(i).contains(profile_name)) {

                    pathList.add(0, listFile[i].getAbsolutePath());

                    // }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static Bitmap urlImageToBitmap(String imageUrl, boolean hiRes) throws Exception {
        Bitmap result = null;
        if (imageUrl != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (hiRes) {
                options.inSampleSize = 1;
            } else {
                options.inSampleSize = 3;

            }
            result = BitmapFactory.decodeFile(imageUrl, options);

        }
        return result;
    }

    protected static Bitmap urlImageToBitmapGif(String imageUrl) throws Exception {
        Bitmap result = null;
        if (imageUrl != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 3;

            result = BitmapFactory.decodeFile(imageUrl, options);

        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);

    }

    public static byte[] generateGIF() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        if(pathList ==null){
            File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");
            pathList = new ArrayList<>();
            if (folder.isDirectory()) {
                File[] listFile = folder.listFiles();
                for (int i = 0; i < listFile.length; i++) {

                    try {
                        // TODO: this check is inaccurate. Consider using an Id or something else
                        // if (pathList.get(i).contains(profile_name)) {

                        pathList.add(listFile[i].getAbsolutePath());
                        // }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.i(TAG, "pathlist" + pathList.size());
        for (String s : pathList) {
            try {
                Bitmap b = GalleryView.urlImageToBitmapGif(s);
                bitmaps.add(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setRepeat(0);
        encoder.delay = 50;
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    public static void writeGif() {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/growpics/gif.gif");
            outStream.write(generateGIF());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}