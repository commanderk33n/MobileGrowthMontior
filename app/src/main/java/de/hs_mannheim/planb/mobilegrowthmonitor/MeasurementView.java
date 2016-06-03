package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * Created by Laura on 21.05.2016.
 */
public class MeasurementView extends BaseActivity {

    private EditText height, weight;
    private Button save;
    private DbHelper dbHelper;
    private int profile_Id;
    private int age;
    private ProfileData profile;
    private TextView bmi, bmiCategory;

    /**
     * Fetches Profile from database
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measurement_view);

        height = (EditText) findViewById(R.id.et_height);
        weight = (EditText) findViewById(R.id.et_weight);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        age = extras.getInt("profileAge");
        profile = dbHelper.getProfile(profile_Id);


    }

    /**
     * Data is fetched from input fields and put in the database.
     * BMI is calculated and analyzed.
     *
     * @param view
     */
    public void saveMeasurement(View view) {

        if (validate()) {
            double height = Double.parseDouble(this.height.getText().toString()) / 100.0;
            double weight = Double.parseDouble(this.weight.getText().toString());

            double bmi_value = weight / (height * height);

            MeasurementData measurementData = new MeasurementData();
            measurementData.height = height;
            measurementData.weight = weight;
            measurementData.index = profile_Id;
            measurementData.image = ""; //TODO: picture-path needs to be saved here
            Calendar today = Calendar.getInstance();
            today.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            measurementData.date = format.format(today.getTime());

            dbHelper.addMeasurement(measurementData);

            bmi = (TextView) findViewById(R.id.tv_bmi);
            bmi.setVisibility(View.VISIBLE);

            bmiCategory = (TextView) findViewById(R.id.tv_bmi_category);
            bmiCategory.setVisibility(View.VISIBLE);

            bmi.setText(String.format("The BMI is: %.2f.",  bmi_value));

            if (age < 19 && age > 7) {
                bmiCategory.setText("The child " + bmiCategorizeChild(age, bmi_value, profile.sex));
            } else if (age > 19) {
                bmiCategory.setText("You " + bmiCategorize(bmi_value, profile.sex));
            } else {
                bmiCategory.setText("There is no valid BMI in this age class");
            }
        }
    }


    // Tabellen von http://www.bmi-rechner.net/
    public String bmiCategorizeChild(int age, double bmi, int sex) {
        String result = "";
        System.out.println("name: " + profile.lastname);
        System.out.println("Gender : " + profile.sex);
        if(sex == 0 ){
            double[][] perzentileFemale = {{8, 12.2, 13.2, 15.9, 18.8, 22.3},
                    {9, 13.0, 13.7, 16.4, 19.8, 23.4},
                    {10, 13.4, 14.2, 16.9, 20.7, 23.4},
                    {11, 13.8, 14.6, 17.7, 20.8, 22.9},
                    {12, 14.8, 16.0, 18.4, 21.5, 23.4},
                    {13, 15.2, 15.6, 18.9, 22.1, 24.4},
                    {14, 16.2, 17.0, 19.4, 23.2, 26.0},
                    {15, 16.9, 17.6, 20.2, 23.2, 27.6},
                    {16, 16.9, 17.8, 20.3, 22.8, 24.2},
                    {17, 17.1, 17.8, 20.5, 23.4, 25.7},
                    {18, 17.6, 18.3, 20.6, 23.5, 25.0}};
            int i = 0;
            while (perzentileFemale[i][0] != age) {
                i += 1;
            }

            if(bmi < perzentileFemale[i][1]){
                result = "is serverly underweight!";
                setBackgroundColor(1);
            } else if(bmi < perzentileFemale[i][2]){
                result = "is underweight weight!";
                setBackgroundColor(2);
            } else if (bmi > perzentileFemale[i][2] && bmi < perzentileFemale[i][4]){ //3
                result = "has normal weight!";
                setBackgroundColor(4);
            }else if(bmi > perzentileFemale[i][4] && bmi < perzentileFemale[i][5]){
                result = "is overweight!";
                setBackgroundColor(2);
            }else {
                result = "is severely overweight";
                setBackgroundColor(1);
            }


        }else {

            double[][] perzentileMale = {{8, 12.5, 14.2, 16.4, 19.3, 22.6},
                    {9, 12.8, 13.7, 17.1, 19.4, 21.6},
                    {10, 13.9, 14.6, 17.1, 21.4, 25.0},
                    {11, 14.0, 14.3, 17.8, 21.2, 23.1},
                    {12, 14.6, 14.8, 18.4, 22.0, 24.8},
                    {13, 15.6, 16.2, 19.1, 21.7, 24.5},
                    {14, 16.1, 16.7, 19.8, 22.6, 25.7},
                    {15, 17.0, 17.8, 20.2, 23.1, 25.9},
                    {16, 17.8, 18.5, 21.0, 23.7, 26.0},
                    {17, 17.6, 18.6, 21.6, 23.7, 25.8},
                    {18, 17.6, 18.6, 21.8, 24.0, 26.8}};

            int i = 0;
            while (perzentileMale[i][0] != age) {
                i += 1;
            }
            System.out.println("Zeile: " + i);

            if(bmi < perzentileMale[i][1]){
                result = "is serverly underweight!";
                setBackgroundColor(1);
            } else if(bmi < perzentileMale[i][2]){
                result = "is underweight weight!";
                setBackgroundColor(2);
            } else if (bmi > perzentileMale[i][2] && bmi < perzentileMale[i][4]){ //3
                result = "has normal weight!";
                setBackgroundColor(4);
            }else if(bmi > perzentileMale[i][4] && bmi < perzentileMale[i][5]){
                result = "is overweight!";
                setBackgroundColor(2);
            }else {
                result = "is severely overweight";
                setBackgroundColor(1);
            }
        }

        return result;
    }

    public String bmiCategorize(double bmi, int sex) {
        if (sex == 0) {
            if (bmi < 19) {
                setBackgroundColor(1);
                return "are underweight!";
            } else if (bmi < 25) {
                setBackgroundColor(4);
                return "have normal weight!";
            } else if (bmi < 31) {
                setBackgroundColor(2);
                return "are overweight!";
            } else if (bmi < 41) {
                setBackgroundColor(3);
                return "have obesity!";
            } else {
                setBackgroundColor(1);
                return "have severe Obesity!";
            }
        } else {
            if (bmi < 20) {
                setBackgroundColor(1);
                return "are underweight!";
            } else if (bmi < 26) {
                setBackgroundColor(4);
                return "have normal weight!";
            } else if (bmi < 31) {
                setBackgroundColor(2);
                return "are overweight!";
            } else if (bmi < 41) {
                setBackgroundColor(3);
                return "have obesity!";
            } else {
                setBackgroundColor(1);
                return "have severe Obesity!";
            }
        }
    }

    public boolean validate() {
        if (height.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter the childs height!", Toast.LENGTH_LONG).show();
            return false;
        } else if (weight.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter the childs weight!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void setBackgroundColor(int color){
        switch (color){
            case 1:
                bmiCategory.setBackgroundColor(Color.parseColor("#FFFF4D00"));
                break;
            case 2:
                bmiCategory.setBackgroundColor(Color.parseColor("#FFFF7B00"));
                break;
            case 3:
                bmiCategory.setBackgroundColor(Color.parseColor("#FFFFB300"));
                break;
            case 4:
                bmiCategory.setBackgroundColor(Color.parseColor("#FFCDE28F"));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbHelper.close();
    }

}
