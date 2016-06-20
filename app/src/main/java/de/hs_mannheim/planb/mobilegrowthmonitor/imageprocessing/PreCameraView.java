package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.hs_mannheim.planb.mobilegrowthmonitor.profiles.ProfileView;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.ImageAdapter;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class PreCameraView extends BaseActivity {
    private EditText etWeight, etHeightReference;
    private int profile_Id;
    Bundle extras;
    DbHelper dbHelper;
    ProfileData profile;
    String profileName;
    float weight, heightReference;
    SharedPreferences settings;
    final String PREFS_NAME = "Reference";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_cam_view);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pre_camera_view);
            setSupportActionBar(toolbar);
        }else{
            Button btnSaveProfile = (Button) findViewById(R.id.btn_save_measurement);
            btnSaveProfile.setVisibility(View.VISIBLE);
            btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveMeasurement();
                }
            });
        }


        etWeight = (EditText) findViewById(R.id.et_weightMeasurement);
        etHeightReference = (EditText) findViewById(R.id.et_heightReference);
        extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        dbHelper = DbHelper.getInstance(getApplicationContext());
        profile = dbHelper.getProfile(profile_Id);
        profileName = profile.firstname;

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

            saveMeasurement();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * starts the camera view
     *
     */
    private void saveMeasurement(){

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
            Intent intent = new Intent(getApplicationContext(), CameraView.class);
               /* try {
                    camFragLoader.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/


            ImageAdapter.REFERENCE_OBJECT_HEIGHT = heightReference;

            intent.putExtra("profile_Id", profile_Id);
            intent.putExtra("weight", weight);
            intent.putExtra("heightReference", heightReference);
            intent.putExtra("profileName", profileName);

            startActivity(intent);

            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
