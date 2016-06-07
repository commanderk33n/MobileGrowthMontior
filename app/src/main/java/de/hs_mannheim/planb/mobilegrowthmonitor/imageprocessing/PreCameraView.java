package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;

public class PreCameraView extends AppCompatActivity {
private EditText etWeight,etHeightReference;
    private int profile_Id;
    Bundle extras;
    DbHelper dbHelper;
    ProfileData profile;
    float  weight,heightReference;
    SharedPreferences settings;
    final String PREFS_NAME = "Reference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_camera_view);

        etWeight = (EditText) findViewById(R.id.et_weightMeasurement);
        etHeightReference = (EditText) findViewById(R.id.et_heightReference);
        extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        dbHelper = DbHelper.getInstance(getApplicationContext());
        profile = dbHelper.getProfile(profile_Id);
        weight = (float) dbHelper.getLatestMeasurement(profile_Id).weight;
        settings = getSharedPreferences(PREFS_NAME, 0);
        heightReference = settings.getFloat("heightReference",10);
        etWeight.setText(weight+"");
        etHeightReference.setText(heightReference+"");

    }
    public void startMeasurement(View view){
        if (etWeight.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.enter_weight, Toast.LENGTH_LONG).show();
        } else if (etHeightReference.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, R.string.enter_height, Toast.LENGTH_LONG).show();
        } else {
            weight = Float.parseFloat(etWeight.getText().toString());
            heightReference = Float.parseFloat(etHeightReference.getText().toString());

            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat("heightReference", (float) weight);
            editor.commit();
            Intent i = new Intent(getApplicationContext(), CameraView.class);
            i.putExtra("profile_Id", profile_Id);
            i.putExtra("weight", weight);
            i.putExtra("heightReference", heightReference);
            startActivity(i);

            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.measure_start) {


            if (etWeight.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, R.string.enter_weight, Toast.LENGTH_LONG).show();
            } else if (etHeightReference.getText().toString().trim().isEmpty()) {

                Toast.makeText(this, R.string.enter_height, Toast.LENGTH_LONG).show();
            } else {
                weight = Float.parseFloat(etWeight.getText().toString());
                heightReference = Float.parseFloat(etHeightReference.getText().toString());

                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("heightReference",heightReference);
                editor.commit();
                Intent i = new Intent(getApplicationContext(),CameraView.class);
                i.putExtra("profile_Id",profile_Id);
                i.putExtra("weight",weight);
                i.putExtra("heightReference",heightReference);
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
