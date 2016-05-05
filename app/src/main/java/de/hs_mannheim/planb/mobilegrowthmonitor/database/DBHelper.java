package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by eikood on 05.05.2016.
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
        db.execSQL(DbContract.CREATE_PROFILES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.FeedProfile.TABLE_NAME);
            onCreate(db);
        }
    }

    public void addProfile(ProfileData profileData) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DbContract.FeedProfile.COLUMN_NAME_SURENAME, profileData.surename);
            values.put(DbContract.FeedProfile.COLUMN_NAME_FORENAME, profileData.forename);
            values.put(DbContract.FeedProfile.COLUMN_NAME_SEX, profileData.sex);
            values.put(DbContract.FeedProfile.COLUMN_NAME_BIRTHDAY, profileData.birthday);
            db.insertOrThrow(DbContract.FeedProfile.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error while trying to add a new profileData to db");
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
                    profileData.surename = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_SURENAME));
                    profileData.forename = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_FORENAME));
                    profileData.sex = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_SEX));
                    profileData.birthday = cursor.getString(cursor.getColumnIndex(DbContract.FeedProfile.COLUMN_NAME_BIRTHDAY));
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

    public void deleteProfile(String name) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("delete from " + DbContract.FeedProfile.TABLE_NAME + " where surename ='" + name + "'");
            db.setTransactionSuccessful();

        } catch (SQLException e) {
            Log.e(TAG, "Error while trying to delete profile from db");
        } finally {
            db.endTransaction();
        }
    }


}
