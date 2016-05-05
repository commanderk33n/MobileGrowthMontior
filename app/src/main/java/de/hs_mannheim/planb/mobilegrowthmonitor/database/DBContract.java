package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import android.provider.BaseColumns;

/**
 * Created by eikood on 05.05.2016.
 */
public final class DbContract {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbContract() {
    }

    public static final String DATABASE_NAME = "growthMonitor";
    public static final int DATABASE_VERSION = 1;

    public static abstract class FeedProfile implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_SURENAME = "surename";
        public static final String COLUMN_NAME_FORENAME = "forename";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_SEX = "sex";
    }

    public static abstract class FeedMeasurement implements BaseColumns {
        public static final String TABLE_NAME = "measurement";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_IMAGE = "image_path";
    }

    public static final String CREATE_PROFILES_TABLE = "CREATE TABLE " + FeedProfile.TABLE_NAME +
            "(" +
            FeedProfile._ID + " INTEGER PRIMARY KEY," +
            FeedProfile.COLUMN_NAME_SURENAME + " TEXT," +
            FeedProfile.COLUMN_NAME_FORENAME + " TEXT," +
            FeedProfile.COLUMN_NAME_SEX + " TEXT," +
            FeedProfile.COLUMN_NAME_BIRTHDAY + " TEXT"+ ")";

    public static final String PROFILE_ALL_SELECT_QUERY = "SELECT * FROM " + FeedProfile.TABLE_NAME;


}
