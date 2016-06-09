package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class PreCameraView extends BaseActivity {
    private EditText etWeight, etHeightReference;
    private int profile_Id;
    Bundle extras;
    DbHelper dbHelper;
    ProfileData profile;
    float weight, heightReference;
    SharedPreferences settings;
    final String PREFS_NAME = "Reference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_cam_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pre_camera_view);
        setSupportActionBar(toolbar);

        etWeight = (EditText) findViewById(R.id.et_weightMeasurement);
        etHeightReference = (EditText) findViewById(R.id.et_heightReference);
        extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        dbHelper = DbHelper.getInstance(getApplicationContext());
        profile = dbHelper.getProfile(profile_Id);
        MeasurementData measurementData = dbHelper.getLatestMeasurement(profile_Id);
        if (measurementData != null) {
            weight = (float) measurementData.weight;
        } else {
            weight = 0.0f;
        }

        settings = getSharedPreferences(PREFS_NAME, 0);
        heightReference = settings.getFloat("heightReference", 10);
        etWeight.setText(weight + "");
        etHeightReference.setText(heightReference + "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pre_camera_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.measure_start) {


            if (etWeight.getText().toString().trim().isEmpty() || Float.parseFloat(etWeight.getText().toString()) < 0) {
                Toast.makeText(this, R.string.enter_weight, Toast.LENGTH_LONG).show();
            } else if (etHeightReference.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, R.string.enter_heightReference, Toast.LENGTH_LONG).show();
            } else {
                weight = Float.parseFloat(etWeight.getText().toString());
                heightReference = Float.parseFloat(etHeightReference.getText().toString());

                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("heightReference", heightReference);
                editor.commit();
                Intent i = new Intent(getApplicationContext(), CameraView.class);
                i.putExtra("profile_Id", profile_Id);
                i.putExtra("weight", weight);
                i.putExtra("heightReference", heightReference);
                startActivity(i);

                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
