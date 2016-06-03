package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

/**
 * Needed to create a new Profile.
 */
public class CreateProfileView extends BaseActivity {

    private static final String TAG = CreateProfileView.class.getSimpleName();

    EditText lastname, firstname;
    RadioButton sex_male, sex_female;
    DatePicker birthday;
    DbHelper dbHelper;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_profile_view);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        lastname = (EditText) findViewById(R.id.et_lastname);
        firstname = (EditText) findViewById(R.id.et_firstname);
        sex_male = (RadioButton) findViewById(R.id.rb_sex_male);
        sex_female = (RadioButton) findViewById(R.id.rb_sex_female);
        birthday = (DatePicker) findViewById(R.id.dp_birthday);
        birthday.setMaxDate(System.currentTimeMillis() + 10);
        birthday.updateDate(2010, 0, 1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_profile_view, menu);
        return true;
    }

    /**
     * Fetches Date from DatePicker and converts it in a Dateobject
     *
     * @return Dateobject which contains date from DatePicker
     */
    private Date getDateFromDatePicker() {
        int year = birthday.getYear();
        int monthOfYear = birthday.getMonth();
        int dayOfMonth = birthday.getDayOfMonth();
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        return calendar.getTime();
    }


    /**
     * On selecting menu item save profile, this method validates the user input and notifies the
     * user, if there are any fields left to fill out.
     * When all fields are filled, data is saved in Java Object profileData. In the end profileData
     * is added to FeedProfile table.
     *
     * @param item selected menu item
     * @return
     */
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
            } else if (dateFromDatePicker.after(today.getTime())) {
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

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                profileData.birthday = simpleDateFormat.format(dateFromDatePicker);
                dbHelper.addProfile(profileData);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
