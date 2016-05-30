package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.app.FragmentManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;


import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CameraView extends BaseActivity implements SensorEventListener {
    private static String TAG = CameraView.class.getSimpleName();
    NativeCam camFrag;
    private String  profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        Bundle extras = getIntent().getExtras();
        profile_name = extras.getString("profile_name");

        camFrag = NativeCam.newInstance(profile_name);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cam_container, camFrag)
                .commit();
    }


    public void afterPictureTaken(){
        Intent intent = new Intent(this, GalleryView.class);
        intent.putExtra("profile_name", profile_name);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}