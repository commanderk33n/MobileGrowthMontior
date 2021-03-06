package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.app.FragmentManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.MeasurementView;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CameraView extends BaseActivity implements SensorEventListener {
    private static String TAG = CameraView.class.getSimpleName();
    private NativeCam camFrag;
    private int profile_Id;
    private float weight, heightReference;
    private DbHelper db;
    private ProfileData profile;
    private String profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_view);
        db = DbHelper.getInstance(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        heightReference = extras.getFloat("heightReference");
        weight = extras.getFloat("weight");
        profile = db.getProfile(profile_Id);
        profileName = profile.firstname;


        camFrag = NativeCam.newInstance(profile_Id, heightReference, profileName);


        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cam_container, camFrag)
                .commit();
    }

    protected void afterPictureTaken(double height) {
        Intent intent = new Intent(this, MeasurementView.class);
        intent.putExtra("profile_Id", profile_Id);
        intent.putExtra("weight", weight);
        intent.putExtra("height", height);
        intent.putExtra("startCallback", true);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PreCameraView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}