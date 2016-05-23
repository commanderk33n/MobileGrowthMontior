package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;


public class GalleryView extends AppCompatActivity {

    static ArrayList<Bitmap> bitmapList = new ArrayList<>();
    public static ArrayList<String> pathList = new ArrayList<>();
    GridView imageGrid;
    private String profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);

        Bundle extras = getIntent().getExtras();
        this.profile_name = extras.getString("profile_name");

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        imageGrid.setAdapter(new ImageAdapter(this, bitmapList));
    }

    public void getFromSdCard() {

        // TODO: change this to Internal again after size measurement is finished
        //File folder = new File(getFilesDir().getPath() + File.separator +"MobileGrowthMonitor_pictures");
        File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");

        if (folder.isDirectory()) {
            File[] listFile = folder.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                try {
                    // TODO: this check is inaccurate. Consider using an Id or something else
                    // if (pathList.get(i).contains(profile_name)) {
                    pathList.add(listFile[i].getAbsolutePath());
                    bitmapList.add(urlImageToBitmap(pathList.get(i)));
                    // }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap result = null;
        if (imageUrl != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            result = BitmapFactory.decodeFile(imageUrl, options);
        }
        return result;
    }
}
