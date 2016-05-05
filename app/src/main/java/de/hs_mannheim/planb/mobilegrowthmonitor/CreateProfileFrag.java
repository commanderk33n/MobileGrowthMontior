package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CreateProfileFrag extends Fragment {


    private static final String TAG = CreateProfileFrag.class.getSimpleName();

    EditText surename, forename;
    CheckBox sex_male, sex_female;
    DatePicker birthday;
    Button btn_next;
    DbHelper dbHelper;
    View mView;
    FragmentTransaction fragmentTransaction;
    CreateProfileFrag createProfileFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create_profile, container, false);
        dbHelper = DbHelper.getInstance(mView.getContext());
        surename = (EditText) mView.findViewById(R.id.et_surename);
        forename = (EditText) mView.findViewById(R.id.et_forename);
        sex_male = (CheckBox) mView.findViewById(R.id.cb_sex_male);
        sex_female = (CheckBox) mView.findViewById(R.id.cb_sex_female);
        birthday = (DatePicker) mView.findViewById(R.id.dp_birthday);



        btn_next = (Button) mView.findViewById(R.id.btn_saveProfile);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProfileData profileData = new ProfileData();
                if(!surename.getText().toString().isEmpty()) {
                    profileData.surename = surename.getText().toString();
                } else {
                    profileData.surename = "";
                }
                if (!forename.getText().toString().isEmpty()){
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
                Long aLong =  calendar.getTimeInMillis();
                profileData.birthday = aLong.intValue();
                dbHelper.addProfile(profileData);

            }
        });
        return mView;

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
