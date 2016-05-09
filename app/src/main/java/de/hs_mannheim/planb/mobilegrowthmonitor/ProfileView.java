package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.os.Bundle;
import android.widget.TextView;

import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class ProfileView extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Bundle extras = getIntent().getExtras();
        int profileId = extras.getInt("profile_Id");

        TextView textView = (TextView) findViewById(R.id.teeest);
        textView.setText(profileId + "ist angemeldet");
    }
}
