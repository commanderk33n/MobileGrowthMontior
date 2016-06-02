package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
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
import de.hs_mannheim.planb.mobilegrowthmonitor.datahandler.GraphView;
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

    /**
     * Support is set to toolbar. Profile is fetched from database. Profile data is set to text
     * views.
     * @param savedInstanceState
     */
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
        dbHelper.close();
        TextView tvFirstname = (TextView) findViewById(R.id.tv_firstname);
        tvFirstname.setText(profile.firstname + ",");

        mProfileImage = (ImageButton) findViewById(R.id.ib_profilepic);
        if (profile.profilepic != null) { // if profilepic is null it keeps the drawable
            Bitmap originalBitmap = BitmapFactory.decodeFile(profile.profilepic);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, false);
            mProfileImage.setImageBitmap(rotateBitmap(resizedBitmap, 90));
        }

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        ImageView imGender = (ImageView) findViewById(R.id.iv_gender);
        if (profile.sex == 0) {
            Drawable dw = getApplicationContext().getResources().getDrawable(R.drawable.female_24);
            imGender.setImageDrawable(dw);
        } else {
            Drawable dw = getApplicationContext().getResources().getDrawable(R.drawable.male_24);
            imGender.setImageDrawable(dw);
        }


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

        setMeasurementTextViews();

    }

    /**
     * Latest measurement is fetched from database and data is set to textviews in profile view.
     */
    private void setMeasurementTextViews() {

        MeasurementData measurementData = dbHelper.getLatestMeasurement(profile_Id);

        if (measurementData != null) {
            TextView tvLastMeasurement = (TextView) findViewById(R.id.tv_date_last_measurement);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar date = new GregorianCalendar();
                date.setTime(format.parse(measurementData.date));
                String text = format.format(date.getTime());
                tvLastMeasurement.setText(text);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView tvWeight = (TextView) findViewById(R.id.tv_weight);
            tvWeight.setText(measurementData.weight + " kg");

            TextView tvHeight = (TextView) findViewById(R.id.tv_height);
            tvHeight.setText(measurementData.height + " m");
        }

    }

    /**
     * Selects photo to be displayed as profile picture. User can choose whether to take a photo,
     * choose a picture from the gallery or cancel action.
     */
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

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_view, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_profile) {
            dbHelper.deleteProfile(profile_Id);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, false);
                mProfileImage.setImageBitmap(rotateBitmap(resizedBitmap, 270));

            }
        } else if (requestCode == 3) {
            setMeasurementTextViews();
        }
    }

    /**
     * OnClick method to start CameraView Activity
     * @param view
     */
    public void startCamera(View view) {
        Intent intent = new Intent(this, CameraView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start GraphView Activity
     * @param view
     */
    public void startGraph(View view){
        Intent intent = new Intent(this, GraphView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start GalleryView Activity
     * @param view
     */
    public void startGallery(View view) {
        Intent intent = new Intent(this, GalleryView.class);
        intent.putExtra("profile_Id", profile.index);
        startActivity(intent);
    }

    /**
     * OnClick method to start MeasurementView Activity
     * @param view
     */
    public void startMeasurement(View view) {
        Intent intent = new Intent(this, MeasurementView.class);
        Log.v("ProfileView -> Measu", " " + profile_Id);
        intent.putExtra("profile_Id", profile_Id);
        intent.putExtra("profileAge", age);
        startActivityForResult(intent, 3);
    }

    /**
     * Rotates Bitmap
     *
     * @param source bitmap to be rotated
     * @param angle angle that the picture has to be rotated
     * @return rotated bitmap
     */
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}