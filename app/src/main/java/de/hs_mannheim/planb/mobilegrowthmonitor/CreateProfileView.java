package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;

public class CreateProfileView extends AppCompatActivity {

    private static final String TAG = CreateProfileView.class.getSimpleName();

    EditText surname, forename;
    CheckBox sex_male, sex_female;
    DatePicker birthday;
    Button btn_next;
    DbHelper dbHelper;
    MainView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_view);

        dbHelper = DbHelper.getInstance(this);
        surname = (EditText) findViewById(R.id.et_surname);
        forename = (EditText) findViewById(R.id.et_forename);
        sex_male = (CheckBox) findViewById(R.id.cb_sex_male);
        sex_female = (CheckBox) findViewById(R.id.cb_sex_female);
        birthday = (DatePicker) findViewById(R.id.dp_birthday);

        btn_next = (Button) findViewById(R.id.btn_saveProfile);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProfileData profileData = new ProfileData();
                if (!surname.getText().toString().isEmpty()) {
                    profileData.surname = surname.getText().toString();
                } else {
                    profileData.surname = "";
                }
                if (!forename.getText().toString().isEmpty()) {
                    profileData.forename = forename.getText().toString();
                } else {
                    profileData.forename = "";
                }
                if (!sex_female.hasSelection() && sex_male.hasSelection()) {
                    profileData.sex = 1;
                } else {
                    profileData.sex = 0;
                }
                int year = birthday.getYear();
                int monthOfYear = birthday.getMonth();
                int dayOfMonth = birthday.getDayOfMonth();
                Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Long aLong = calendar.getTimeInMillis();
                profileData.birthday = aLong.intValue();
                dbHelper.addProfile(profileData);
                finish();
            }
        });
    }
    
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
