package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.CameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class ProfileView extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Bundle extras = getIntent().getExtras();
        int profileId = extras.getInt("profile_Id");
        DbHelper dbHelper = DbHelper.getInstance(this);
        ProfileData profile = dbHelper.getProfile(profileId);

        TextView tvFirstname = (TextView) findViewById(R.id.tv_firstname);
        tvFirstname.setText(profile.firstname);

        //TextView tvAge = (TextView) findViewById(R.id.tv_age);
        //tvAge.setText(profile.birthday);


    }

    public void startCamera(View view) {
        Intent intent = new Intent(this, CameraView.class);
        startActivity(intent);
    }

    public void startGallery(View view) {
        Intent intent = new Intent(this, GalleryView.class);
        startActivity(intent);
    }
}
