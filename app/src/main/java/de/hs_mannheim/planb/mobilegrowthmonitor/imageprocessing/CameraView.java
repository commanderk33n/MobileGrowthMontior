package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.app.FragmentManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;


import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CameraView extends BaseActivity implements SensorEventListener {
    private static String TAG = CameraView.class.getSimpleName();
    NativeCam camFrag;
    private String  profile_name;
    private int profile_Id;
    DbHelper db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
        DbHelper db = DbHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");

        profile_name = db.getProfile(profile_Id).firstname;

        camFrag = NativeCam.newInstance(profile_name);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cam_container, camFrag)
                .commit();
    }


    public void afterPictureTaken(){
        Intent intent = new Intent(this, GalleryView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);
       // finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}