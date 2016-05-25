package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.ProfileData;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.CameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * ProfileView.Activity
 * Show selected Profiles - and Profile functions
 */

public class ProfileView extends BaseActivity {


    private int profile_Id;
    private int age;
    private ImageButton mProfileImage;
    private DbHelper dbHelper;
    private ProfileData profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_view);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        profile_Id = extras.getInt("profile_Id");
        DbHelper dbHelper = DbHelper.getInstance(this);
        profile = dbHelper.getProfile(profile_Id);

        TextView tvFirstname = (TextView) findViewById(R.id.tv_firstname);
        tvFirstname.setText(profile.firstname);

        mProfileImage = (ImageButton) findViewById(R.id.ib_profilepic);
        if (profile.profilepic != null) { // if profilepic is null it keeps the drawable
            Bitmap originalBitmap = BitmapFactory.decodeFile(profile.profilepic);
            mProfileImage.setImageBitmap(originalBitmap);
        }

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        TextView tvAge = (TextView) findViewById(R.id.tv_age);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date tempDate = format.parse(profile.birthday);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(tempDate);
            Calendar today = Calendar.getInstance();
            age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvAge.setText(Integer.toString(age));

        // here is a test, whether the database FeedMeasurement works
        // TODO: add a check, whether there was already taken a measurement
        MeasurementData data1 = new MeasurementData();
        data1.size = 1.65;
        data1.weight = 55.0;
        data1.index = profile_Id;
        data1.image = "";
        data1.date = "2016-05-23";
        dbHelper.addMeasurement(data1);

        MeasurementData data2 = new MeasurementData();
        data2.size = 1.70;
        data2.weight = 80.0;
        data2.index = profile_Id;
        data2.image = "";
        data2.date = "2016-05-24";
        dbHelper.addMeasurement(data2);

        TextView tvLastMeasurement = (TextView) findViewById(R.id.tv_date_last_measurement);
        MeasurementData measurementData = dbHelper.getLatestMeasurement(profile_Id);
        tvLastMeasurement.setText(measurementData.date);
        //TODO: set size and weight TextView

    }


    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileView.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    // TODO: startCam
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_profile) {
            dbHelper.deleteProfile(profile_Id);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // TODO: get and save image from cam
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                dbHelper.setProfilePic(profile_Id, picturePath);
                Bitmap originalBitmap = BitmapFactory.decodeFile(picturePath);
                //  Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 150, 150, false);
                mProfileImage.setImageBitmap(originalBitmap);

            }
        }
    }

    public void startCamera(View view) {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("profile_name", profile.firstname);
        startActivity(intent);
    }

    public void startGallery(View view) {
        Intent intent = new Intent(this, GalleryView.class);
        intent.putExtra("profile_name", profile.firstname);
        startActivity(intent);
    }

    public void startMeasurement(View view){
        Intent intent = new Intent(this, MeasurementView.class);
        Log.v("ProfileView -> MEasu"," "+ profile_Id);
        intent.putExtra("profile_Id", profile_Id);
        intent.putExtra("profileAge", age);
        startActivity(intent);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}