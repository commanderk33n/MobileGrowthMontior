package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbDummyData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.Filereader;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.PreCameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * Created by Laura on 21.05.2016.
 */
public class MeasurementView extends BaseActivity {

    public static EditText eT_height, eT_weight;
    private static ImageView mImageView;
    private DbHelper dbHelper;
    private int profile_Id;
    private double weight, height;
    private static String image, edited;
    private ProfileData profile;
    private TextView bmi, bmiCategory, heightText, weightText, heightCategory, weightCategory;
    private static Button undo;
    public static final String TAG = MeasurementView.class.getSimpleName();
    public static Context baseContext;
    private static volatile boolean startCallback;
    private static volatile boolean goBack;
    double[][] weightData, heightData, bmiData;
    private boolean gender;
    private Date birthday;
    private Thread dataGetter = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.i("datagetter", "start");

            gender = profile.sex == 1;
            birthday = null;
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

            try {
                birthday = format2.parse(profile.birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bmiData = new Filereader(getApplicationContext()).giveMeTheData(1, birthday, gender,null);
            heightData = new Filereader(getApplicationContext()).giveMeTheData(2, birthday, gender,null);
            weightData = new Filereader(getApplicationContext()).giveMeTheData(3, birthday, gender,null);
            Log.i("datagetter", "done");

        }
    });

    private static Thread callbackWaiter;

    /**
     * Fetches Profile from database
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.measurement_view);

        eT_height = (EditText) findViewById(R.id.et_height);
        eT_weight = (EditText) findViewById(R.id.et_weight);
        baseContext = getBaseContext();
        dbHelper = DbHelper.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");

        profile = dbHelper.getProfile(profile_Id);


        if (extras.containsKey("weight")) {
            weight = extras.getFloat("weight");
        } else {
            if (dbHelper.getLatestMeasurement(profile_Id) != null) {

                weight = dbHelper.getLatestMeasurement(profile_Id).weight;
            }
        }
        if (extras.containsKey("height")) {
            height = extras.getDouble("height");

        } else {
            if (dbHelper.getLatestMeasurement(profile_Id) != null) {

                height = dbHelper.getLatestMeasurement(profile_Id).height;
            }
        }


        startCallback = extras.getBoolean("startCallback", false);
        goBack = false;

        Log.i(TAG, "startCallback before if" + startCallback);
        if (startCallback) {
            callbackWaiter = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Thread started");
                    int i = 0;
                    while (startCallback && i < 50) {
                        i++;
                        Log.i(TAG, "goBack= " + goBack);
                        Log.i(TAG, "startCallback = " + startCallback);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            if (goBack) {
                                Intent intent = new Intent(MeasurementView.this, PreCameraView.class);
                                intent.putExtra("profile_Id", profile.index);
                                startActivity(intent);
                                startCallback = false;
                            }
                            e.printStackTrace();
                        }
                    }
                }
            });
            callbackWaiter.start();


        }

        dataGetter.start();
        eT_weight.setText(weight + "");
        eT_height.setText(height + "");
        Log.i(TAG, "weight = " + weight);
        profile = dbHelper.getProfile(profile_Id);
        mImageView = (ImageView) findViewById(R.id.iv_result_pic);
       // mImageView.setVisibility(View.GONE);
        undo = (Button) findViewById(R.id.btn_undo);

           DbDummyData dbDummyData = new DbDummyData(getApplicationContext());
         dbDummyData.addData(profile_Id);
    }

    /**
     * Data is fetched from input fields and put in the database.
     * BMI is calculated and analyzed.
     *
     * @param view
     */
    public void saveMeasurement(View view) {
        try {
            dataGetter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (validate()) {
            double height = Double.parseDouble(this.eT_height.getText().toString());
            double weight = Double.parseDouble(this.eT_weight.getText().toString());
            height =  height*10;
            height = height-height%1;
            height = height/10;
            MeasurementData measurementData = new MeasurementData();
            measurementData.height = height;
            measurementData.weight = weight;
            measurementData.index = profile_Id;
            if (!(image == null)) {
                measurementData.image = image;
            } else {
                measurementData.image = "";
            }
           // image = null; todo: see if this causes problems
            Calendar today = Calendar.getInstance();
            today.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            measurementData.date = format.format(today.getTime());

            dbHelper.addMeasurement(measurementData);

            showTexts();
            if(undo!= null){
                undo.setVisibility(View.INVISIBLE);
            }

        }
    }


    public static void goBack() {
        goBack = true;
        callbackWaiter.interrupt();
        Log.i(TAG, "goBack()");
    }

    private void showTexts() {
        double height = Double.parseDouble(eT_height.getText().toString()) / 100.0;
        double weight = Double.parseDouble(eT_weight.getText().toString());

        double bmiValue = weight / (height * height);
        bmiValue *=100;
        bmiValue-=bmiValue%1;
        bmiValue/=100;

        bmi = (TextView) findViewById(R.id.tv_bmi);
        bmi.setVisibility(View.VISIBLE);

        bmiCategory = (TextView) findViewById(R.id.tv_bmi_category);
        bmiCategory.setVisibility(View.VISIBLE);

        bmi.setText(String.format(getString(R.string.bmi), bmiValue));

        heightText = (TextView) findViewById(R.id.tv_heightText);
        heightText.setVisibility(View.VISIBLE);

        heightCategory = (TextView) findViewById(R.id.tv_height_category);
        heightCategory.setVisibility(View.VISIBLE);

        weightText = (TextView) findViewById(R.id.tv_weightText);
        weightText.setVisibility(View.VISIBLE);

        weightCategory = (TextView) findViewById(R.id.tv_weight_category);
        weightCategory.setVisibility(View.VISIBLE);



        bmiCategory.setText(getTextBMI(bmiData, birthday, bmiValue));

        heightCategory.setText(getTextHeight(heightData, birthday, height * 100));

        weightCategory.setText(getTextWeight(weightData, birthday, weight));


    }

    private String getTextBMI(double[][] data, Date birthday, double bmi) {

       int age = Utils.getAgeInMonths(birthday,null);
        if (age > 228) {
            return bmiCategorize(bmi, profile.sex);
        } else {
            //  optimal.add(data[i][data[0].length / 2]);
            for (int i = 0; i < data.length; i++) {
                if ((int) data[i][0] == age) {

                    //  optimal.add(data[i][data[0].length / 2]);
                    double sdMinus1 = (data[i][data[0].length / 2 - 2]);
                    double sdPlus1 = (data[i][data[0].length / 2 + 2]);
                    double sdMinus2 = (data[i][data[0].length / 2 - 3]);
                    double sdPlus2 = (data[i][data[0].length / 2 + 3]);
                    if (bmi > sdPlus2) {
                        setBackgroundColor(1, bmiCategory);
                        return getString(R.string.bmi_category_weight_child_severly_overweight);

                    } else if (bmi > sdPlus1) {
                        setBackgroundColor(2, bmiCategory);
                        return getString(R.string.bmi_category_weight_child_overweight);
                    } else if (bmi < sdMinus2) {
                        setBackgroundColor(1, bmiCategory);
                        return getString(R.string.bmi_category_weight_child_severly_underweight);
                    } else if (bmi < sdMinus1) {
                        setBackgroundColor(3, bmiCategory);
                        return getString(R.string.bmi_category_weight_child_underweight);
                    }
                    setBackgroundColor(4, bmiCategory);
                    return getString(R.string.bmi_category_weight_child_normal_weight);

                }
            }


        }
        return "error hehehehehehehe";
    }

    private String getTextHeight(double[][] data, Date birthday, double height) {

        int age = Utils.getAgeInMonths(birthday,null);

        if (age > 228) {
            age = 228;
        }else if(age<=60){
            age*=30;
        }
        for (int i = 0; i < data.length; i++) {
            if ((int) data[i][0] == age) {
           //     heightPosition =i;
                double sdMinus1 = (data[i][data[0].length / 2 - 2]);
                double sdPlus1 = (data[i][data[0].length / 2 + 2]);
                double sdMinus2 = (data[i][data[0].length / 2 - 3]);
                double sdPlus2 = (data[i][data[0].length / 2 + 3]);
                if (height > sdPlus2) {
                    setBackgroundColor(2, heightCategory);
                    return getString(R.string.category_height_child_very_tall);
                } else if (height > sdPlus1) {
                    setBackgroundColor(3, heightCategory);
                    return getString(R.string.category_height_child_tall);
                } else if (height < sdMinus2) {
                    setBackgroundColor(1, heightCategory);
                    return getString(R.string.category_height_child_very_short);
                } else if (height < sdMinus1) {
                    setBackgroundColor(2, heightCategory);
                    return getString(R.string.category_height_child_short);
                }
                setBackgroundColor(4, heightCategory);
                return getString(R.string.category_height_child_normal_height);

            }
        }

        return "error hehehehehehehe";
    }



    private String getTextWeight(double[][] data, Date birthday, double weight) {

       int age = Utils.getAgeInMonths(birthday,null);

        if (age > 120) {
            return getString(R.string.bmi_category_weight_not_valid);
        } else if (age < 60) {
            age *= 30;
        }


        for (int i = 0; i < data.length; i++) {
            if ((int) data[i][0] == age) {
           //     weightPosition =i;
                double sdMinus1 = (data[i][data[0].length / 2 - 2]);
                double sdPlus1 = (data[i][data[0].length / 2 + 2]);
                double sdMinus2 = (data[i][data[0].length / 2 - 3]);
                double sdPlus2 = (data[i][data[0].length / 2 + 3]);
                if (weight > sdPlus2) {
                    setBackgroundColor(1, weightCategory);
                    return getString(R.string.bmi_category_weight_child_severly_overweight);

                } else if (weight > sdPlus1) {
                    setBackgroundColor(2, weightCategory);
                    return getString(R.string.bmi_category_weight_child_overweight);
                } else if (weight < sdMinus2) {
                    setBackgroundColor(1, weightCategory);
                    return getString(R.string.bmi_category_weight_child_severly_underweight);
                } else if (weight < sdMinus1) {
                    setBackgroundColor(2, weightCategory);
                    return getString(R.string.bmi_category_weight_child_underweight);
                }
                setBackgroundColor(4, weightCategory);
                return getString(R.string.bmi_category_weight_child_normal_weight);

            }
        }

        return "error hehehehehehehe";
    }


    // Tabellen von http://www.bmi-rechner.net/
    // Still needed?
    private String bmiCategorizeChild(int age, double bmi, int sex) {
        String result = "";
        System.out.println("name: " + profile.lastname);
        System.out.println("Gender : " + profile.sex);
        if (sex == 0) {
            double[][] percentileFemale = {{8, 12.2, 13.2, 15.9, 18.8, 22.3},
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
            while (percentileFemale[i][0] != age) {
                i += 1;
            }

            if (bmi < percentileFemale[i][1]) {
                result = getString(R.string.bmi_category_weight_child_severly_underweight);
                setBackgroundColor(1, bmiCategory);
            } else if (bmi < percentileFemale[i][2]) {
                result = getString(R.string.bmi_category_weight_child_underweight);
                setBackgroundColor(2, bmiCategory);
            } else if (bmi > percentileFemale[i][2] && bmi < percentileFemale[i][4]) { //3
                result = getString(R.string.bmi_category_weight_child_normal_weight);
                setBackgroundColor(4, bmiCategory);
            } else if (bmi > percentileFemale[i][4] && bmi < percentileFemale[i][5]) {
                result = getString(R.string.bmi_category_weight_child_overweight);
                setBackgroundColor(2, bmiCategory);
            } else {
                result = getString(R.string.bmi_category_weight_child_severly_overweight);
                setBackgroundColor(1, bmiCategory);
            }


        } else {

            double[][] percentileMale = {{8, 12.5, 14.2, 16.4, 19.3, 22.6},
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
            while (percentileMale[i][0] != age) {
                i += 1;
            }
            System.out.println("Zeile: " + i);

            if (bmi < percentileMale[i][1]) {
                setBackgroundColor(1, bmiCategory);
                result = getString(R.string.bmi_category_weight_child_severly_underweight);
            } else if (bmi < percentileMale[i][2]) {
                result = getString(R.string.bmi_category_weight_child_underweight);
                setBackgroundColor(2, bmiCategory);
            } else if (bmi > percentileMale[i][2] && bmi < percentileMale[i][4]) { //3
                result = getString(R.string.bmi_category_weight_child_normal_weight);
                setBackgroundColor(4, bmiCategory);
            } else if (bmi > percentileMale[i][4] && bmi < percentileMale[i][5]) {
                result = getString(R.string.bmi_category_weight_child_overweight);
                setBackgroundColor(2, bmiCategory);
            } else {
                result = getString(R.string.bmi_category_weight_child_severly_overweight);
                setBackgroundColor(1, bmiCategory);
            }
        }

        return result;
    }

    public static void setMeasurement(MeasurementData measurementData) {
        if (measurementData == null) {
            return;
            //todo: create toast
        }
        goBack = false;
        callbackWaiter.interrupt();
        eT_height.setText("" + measurementData.height);
        image = measurementData.image;
        edited = measurementData.edited;
        File imgFile = new File("" + measurementData.edited);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            mImageView.setImageBitmap(bitmap);
            
            mImageView.setVisibility(View.VISIBLE);
        }
        undo.setVisibility(View.VISIBLE);
    }


    private String bmiCategorize(double bmi, int sex) {
        if (sex == 0) {
            if (bmi < 19) {
                setBackgroundColor(1, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_underweight);
            } else if (bmi < 25) {
                setBackgroundColor(4, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_normal_weight);
            } else if (bmi < 31) {
                setBackgroundColor(3, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_overweight);
            } else if (bmi < 41) {
                setBackgroundColor(2, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_obesity);
            } else {
                setBackgroundColor(1, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_sever_obesity);
            }
        } else {
            if (bmi < 20) {
                setBackgroundColor(1, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_underweight);
            } else if (bmi < 26) {
                setBackgroundColor(4, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_normal_weight);
            } else if (bmi < 31) {
                setBackgroundColor(2, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_overweight);
            } else if (bmi < 41) {
                setBackgroundColor(3, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_obesity);
            } else {
                setBackgroundColor(1, bmiCategory);
                return getString(R.string.bmi_category_weight_adult_sever_obesity);
            }
        }
    }

    private boolean validate() {
        if (eT_height.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.enter_height, Toast.LENGTH_LONG).show();
            return false;
        } else if (eT_weight.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.enter_weight, Toast.LENGTH_LONG).show();
            return false;
        } else {
            double height = Double.parseDouble(eT_height.getText().toString());
            double weight = Double.parseDouble(eT_weight.getText().toString());
        /*   if (height > heightData[heightPosition][heightData[0].length-1] -10 ||  //weight / height should not be more than 10 cm/kg +- SD+-3
                    height < heightData[heightPosition][heightData[0].length/2-3] +10||
                    weight > weightData[weightPosition][weightData[0].length-1]-10 ||
                    weight <  weightData[weightPosition][heightData[0].length/2-3] + 10 ||
                   height <5||height>250||weight<1||weight>200) {*/

            if(height>250||height<10||weight>200||weight<1){
                Toast.makeText(this, R.string.validate_data, Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

    private void setBackgroundColor(int color, TextView tv) {
        switch (color) {
            case 1:
                tv.setBackgroundColor(Color.parseColor("#FF973D2B")); //dark red
                break;
            case 2:
                tv.setBackgroundColor(Color.parseColor("#FFFF4D00")); //light red
                break;
            case 3:
                tv.setBackgroundColor(Color.parseColor("#FFFFB300")); //lighter orange
                break;
            case 4:
                tv.setBackgroundColor(Color.parseColor("#FFCDE28F")); //green
                break;
            default:
                break;
        }
    }

    public void undo(View view) {
        File file = new File(image);
        file.delete();
        File fileOriginal = new File(edited);
        fileOriginal.delete();
        Intent intent = new Intent(this, PreCameraView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", profile_Id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

}
