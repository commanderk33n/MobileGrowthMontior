package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * Created by Laura on 02.06.2016.
 * <p>
 * This view gives the possibility to export a morphing gif
 * also it enables the export of the database as a .csv file
 */
public class ExportView extends BaseActivity {

    private int profileId;
    private DbHelper dbHelper;
    ProfileData profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_view);

        dbHelper = DbHelper.getInstance(this);
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");
        profile = dbHelper.getProfile(profileId);

    }

    /**
     * Writes all measurements from FeedMeasurements table associated to profile with specific id
     * in a csv file and saves it in same folder as pictures
     *
     * @param view
     */
    public void exportData(View view) {

        ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);
        if (measurements != null) {
            CSVWriter writer = null;

            try {
                writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()
                        + "/growpics/database" + profileId + ".csv"), ';');
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] entries = new String[measurements.size()];

            for (int i = 0; i < measurements.size(); i++) {
                MeasurementData measurementData = measurements.get(i);
                entries[i] = measurementData.toString();
                System.out.println(entries[i]);
            }

            writer.writeNext(entries);
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, R.string.database_exported, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_csv_file));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()
                    + "/growpics/database" + profileId + ".csv")));
            intent.setType("application/csv");
            startActivity(Intent.createChooser(intent, "Send mail"));

        } else {
            Toast.makeText(this, R.string.no_measurements, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * exports an animated gif of the profile specific files
     *
     * @param view
     */
    public void exportGif(View view) {
        Toast.makeText(getApplicationContext(), R.string.loading, Toast.LENGTH_LONG).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                GalleryView.getFromSdCard();
                try {
                    GalleryView.writeGif();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "MobileGrowthMonitor Data");
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_with_gif));
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()
                            + "/growpics/gif.gif")));
                    intent.setType("image/gif");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.pictures_converted_gif, Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (IllegalArgumentException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "There are no pictures to create a gif-file.", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

}
