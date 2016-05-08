package de.hs_mannheim.planb.mobilegrowthmonitor.datavisual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;

/**
 * Created by eikood on 05.05.2016.
 */
public class GalleryView extends AppCompatActivity{

    static ArrayList<Bitmap> bitmapList = new ArrayList<>();
    public static ArrayList<String> pathList = new ArrayList<>();
    GridView imageGrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view);

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

        File folder = new File(getFilesDir().getPath() + File.separator +"MobileGrowthMonitor_pictures");


        if (folder.isDirectory()) {
            File[] listFile = folder.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                pathList.add(listFile[i].getAbsolutePath());
                try {
                    bitmapList.add(urlImageToBitmap(pathList.get(i)));
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
            options.inSampleSize = 4;
            result = BitmapFactory.decodeFile(imageUrl, options);
        }
        return result;
    }

}
