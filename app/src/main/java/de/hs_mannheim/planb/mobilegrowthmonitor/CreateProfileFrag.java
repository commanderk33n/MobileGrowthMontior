package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CreateProfileFrag extends Fragment {


    private static final String TAG = CreateProfileFrag.class.getSimpleName();

    EditText surename, forename, sex, birthday;
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
        sex = (EditText) mView.findViewById(R.id.et_sex);
        birthday =  (EditText) mView.findViewById(R.id.et_birthday);

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
                if (!sex.getText().toString().isEmpty()) {
                    profileData.sex = sex.getText().toString();
                } else {
                    profileData.sex = "";
                }
                if (!birthday.getText().toString().isEmpty()) {
                    profileData.birthday = birthday.getText().toString();
                } else {
                    profileData.birthday = "";
                }
                dbHelper.addProfile(profileData);
                createProfileFrag = new CreateProfileFrag();
                fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.detach(createProfileFrag);
                fragmentTransaction.remove(createProfileFrag);
                fragmentTransaction.commit();
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
