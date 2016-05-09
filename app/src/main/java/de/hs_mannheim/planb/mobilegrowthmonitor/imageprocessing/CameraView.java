package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.app.FragmentManager;
import android.os.Bundle;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CameraView extends BaseActivity {
    private static String TAG = CameraView.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        NativeCam frag = NativeCam.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cam_container, frag)
                .commit();
    }
}