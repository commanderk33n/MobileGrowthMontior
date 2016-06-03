package de.hs_mannheim.planb.mobilegrowthmonitor.datahandler;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * Created by Laura on 02.06.2016.
 */
public class ExportView extends BaseActivity {

    private int profileId;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_view);

        dbHelper = DbHelper.getInstance(this);
        Bundle extras = getIntent().getExtras();
        profileId = extras.getInt("profile_Id");

    }

    /**
     * Writes all measurements from FeedMeasurements table associated to profile with specific id
     * in a csv file and saves it in same folder as pictures
     *
     * @param view
     */
    public void exportData(View view){

        ArrayList<MeasurementData> measurements = dbHelper.getAllMeasurements(profileId);
        if(measurements!=null) {
            System.out.println("measurements enthält : " + measurements.size() + " Elemente");
            CSVWriter writer = null;

            try {
                writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()
                        + "/growpics/database" + profileId + ".csv"), ',');
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

            Toast.makeText(this, "Success! Your database has been exported!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "There are no measurements to export!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbHelper.close();
    }

}
