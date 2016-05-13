package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CreateProfileView extends BaseActivity {

    private static final String TAG = CreateProfileView.class.getSimpleName();

    EditText surname, firstname;
    RadioButton sex_male, sex_female;
    DatePicker birthday;
    Button btn_next;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_profile_view);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(this);
        surname = (EditText) findViewById(R.id.et_lastname);
        firstname = (EditText) findViewById(R.id.et_firstname);
        sex_male = (RadioButton) findViewById(R.id.rb_sex_male);
        sex_female = (RadioButton) findViewById(R.id.rb_sex_female);
        birthday = (DatePicker) findViewById(R.id.dp_birthday);

        btn_next = (Button) findViewById(R.id.btn_saveProfile);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProfileData profileData = new ProfileData();
                if (!surname.getText().toString().isEmpty()) {
                    profileData.lastname = surname.getText().toString();
                } else {
                    profileData.lastname = "";
                }
                if (!firstname.getText().toString().isEmpty()) {
                    profileData.firstname = firstname.getText().toString();
                } else {
                    profileData.firstname = "";
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
                //long aLong = calendar.getTimeInMillis(); // TODO: try to save date as string in database
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                //format.format(calendar);
                System.out.println(format.format(calendar.getTime()));

                profileData.birthday = format.format(calendar.getTime());
                //System.out.println(format.format(calendar));
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

    public void startGalleryOrCamera(View view) {

    }
}
