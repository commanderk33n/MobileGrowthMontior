package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Basic SQLiteOpenHelper implementation
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private static DbHelper mDbHelper;

    public static synchronized DbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(context);
        }
        return mDbHelper;
    }

    private DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.CREATE_PROFILES_TABLE_QUERY);
        db.execSQL(DbContract.CREATE_MEASUREMENT_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.FeedProfile.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.FeedMeasurement.TABLE_NAME);
            onCreate(db);
        }
    }

    public void addProfile(ProfileData profileData) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbContract.FeedProfile.COLUMN_NAME_LASTNAME, profileData.lastname);
            values.put(DbContract.FeedProfile.COLUMN_NAME_FIRSTNAME, profileData.firstname);
            values.put(DbContract.FeedProfile.COLUMN_NAME_SEX, profileData.sex);
            values.put(DbContract.FeedProfile.COLUMN_NAME_BIRTHDAY, profileData.birthday);
            values.put(DbContract.FeedProfile.COLUMN_NAME_PROFILEPIC, profileData.profilepic);
            db.insertOrThrow(DbContract.FeedProfile.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while trying to add a new profile to db");
        } finally {
            db.endTransaction();
        }
    }

    public void setProfilePic(int index, String path) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues value = new ContentValues();
            value.put(DbContract.FeedProfile.COLUMN_NAME_PROFILEPIC, path);
            String where = DbContract.FeedProfile.COLUMN_NAME_ID + "='" + index + "'";
            db.update(DbContract.FeedProfile.TABLE_NAME, value, where, null);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error while trying to add a profilePic to db");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteProfile(int index) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("delete from " + DbContract.FeedProfile.TABLE_NAME + " where " +
                    DbContract.FeedProfile.COLUMN_NAME_ID + " ='" + index + "'");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error while trying to delete profile from db");
        } finally {
            db.endTransaction();
        }
    }

    public List<ProfileData> getAllProfiles() {
        List<ProfileData> profileDataList = new ArrayList<>();
        String q = DbContract.PROFILE_ALL_SELECT_QUERY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ProfileData profileData = new ProfileData();
                    profileData.index = cursor.getInt(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_ID));
                    profileData.lastname = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_LASTNAME));
                    profileData.firstname = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_FIRSTNAME));
                    profileData.sex = cursor.getInt(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_SEX));
                    profileData.birthday = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_BIRTHDAY));
                    profileData.profilepic = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_PROFILEPIC));
                    profileDataList.add(profileData);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get all profiles from db");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return profileDataList;
    }

    public ProfileData getProfile(int index) {
        String q = "select * from " + DbContract.FeedProfile.TABLE_NAME + " where " +
                DbContract.FeedProfile.COLUMN_NAME_ID + " = '" + index + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        ProfileData profileData = new ProfileData();
        try {
            if (cursor.moveToFirst()) {
                do {
                    profileData.index = cursor.getInt(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_ID));
                    profileData.lastname = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_LASTNAME));
                    profileData.firstname = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_FIRSTNAME));
                    profileData.sex = cursor.getInt(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_SEX));
                    profileData.birthday = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_BIRTHDAY));
                    profileData.profilepic = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_PROFILEPIC));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error while trying to fetch profile from db by id");
        }
        return profileData;
    }

    public MeasurementData getLatestMeasurement(int index){

        ArrayList<MeasurementData> profileDataList = new ArrayList<>();
        String q = "SELECT * FROM " + DbContract.FeedMeasurement.TABLE_NAME +
                " WHERE " + DbContract.FeedMeasurement.COLUMN_NAME_ID + " = '" + index + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    MeasurementData data = new MeasurementData();
                    data.date = cursor.getString(cursor.getColumnIndex(DbContract.FeedMeasurement.COLUMN_NAME_DATE));
                    data.image = cursor.getString(cursor.getColumnIndex(DbContract.FeedMeasurement.COLUMN_NAME_IMAGE));
                    data.index = cursor.getInt(cursor.getColumnIndex(DbContract.FeedMeasurement.COLUMN_NAME_ID));
                    data.size = cursor.getDouble(cursor.getColumnIndex(DbContract.FeedMeasurement.COLUMN_NAME_SIZE));
                    data.weight = cursor.getDouble(cursor.getColumnIndex(DbContract.FeedMeasurement.COLUMN_NAME_WEIGHT));
                    profileDataList.add(data);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get all measurements from db");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        int indexLatestMeasurement = 0;
        for(int i = 0, j = 1; i < profileDataList.size() - 1; i++, j++){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date latestDate = format.parse(profileDataList.get(i).date);
                Date dateToCompare = format.parse(profileDataList.get(j).date);
                if(dateToCompare.after(latestDate)){
                    indexLatestMeasurement = j;
                }
            } catch (java.text.ParseException e) {
                Log.e(TAG, "Error while trying to determine the latest Measurement Date!");
            }


        }

        return profileDataList.get(indexLatestMeasurement);
    }

    public void addMeasurement(MeasurementData measurementData) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbContract.FeedMeasurement.COLUMN_NAME_DATE, measurementData.date);
            values.put(DbContract.FeedMeasurement.COLUMN_NAME_ID, measurementData.index);
            values.put(DbContract.FeedMeasurement.COLUMN_NAME_IMAGE, measurementData.image);
            values.put(DbContract.FeedMeasurement.COLUMN_NAME_SIZE, measurementData.size);
            values.put(DbContract.FeedMeasurement.COLUMN_NAME_WEIGHT, measurementData.weight);
            db.insertOrThrow(DbContract.FeedMeasurement.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while trying to add a new measurement to db");
        } finally {
            db.endTransaction();
        }
    }
}
