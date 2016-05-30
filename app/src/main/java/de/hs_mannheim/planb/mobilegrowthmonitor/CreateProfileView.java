package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

public class CreateProfileView extends BaseActivity {

    private static final String TAG = CreateProfileView.class.getSimpleName();

    EditText lastname, firstname;
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
        lastname = (EditText) findViewById(R.id.et_lastname);
        firstname = (EditText) findViewById(R.id.et_firstname);
        sex_male = (RadioButton) findViewById(R.id.rb_sex_male);
        sex_female = (RadioButton) findViewById(R.id.rb_sex_female);
        birthday = (DatePicker) findViewById(R.id.dp_birthday);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_profile_view, menu);
        return true;
    }

    private Date getDateFromDatePicker(){
        int year = birthday.getYear();
        int monthOfYear = birthday.getMonth();
        int dayOfMonth = birthday.getDayOfMonth();
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        return calendar.getTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_profile) {

            Calendar today = Calendar.getInstance();
            Date dateFromDatePicker = getDateFromDatePicker();

            if (firstname.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter the childs firstname!", Toast.LENGTH_LONG).show();
           
            } else if (lastname.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter the childs lastname!", Toast.LENGTH_LONG).show();
            } else if (!(sex_female.isChecked() || sex_male.isChecked())) {
                Toast.makeText(this, "Please choose the gender!", Toast.LENGTH_LONG).show();
            } else if(dateFromDatePicker.after(today.getTime())){
                Toast.makeText(this, "Please choose a reasonable birthday!", Toast.LENGTH_LONG).show();
            } else {
                ProfileData profileData = new ProfileData();
                profileData.lastname = lastname.getText().toString();
                profileData.firstname = firstname.getText().toString();

                if (sex_male.isChecked() && !sex_female.isChecked()) {
                    profileData.sex = 1;
                } else {
                    profileData.sex = 0;
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                profileData.birthday = format.format(dateFromDatePicker);
                dbHelper.addProfile(profileData);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
