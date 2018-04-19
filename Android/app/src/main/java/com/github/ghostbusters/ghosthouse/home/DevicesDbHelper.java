package com.github.ghostbusters.ghosthouse.home;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by javiosa on 16/04/18.
 */


public class DevicesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Devices.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceContract.DeviceEntry.TABLE_NAME + " (" +
                    DeviceContract.DeviceEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviceContract.DeviceEntry.COLUMN_NAME_USERID + DevicesDbHelper.TEXT_TYPE + DevicesDbHelper.COMMA_SEP +
                    DeviceContract.DeviceEntry.COLUMN_NAME_DEVICE + DevicesDbHelper.TEXT_TYPE + DevicesDbHelper.COMMA_SEP +
                    DeviceContract.DeviceEntry.COLUMN_NAME_LONGITUDE + DevicesDbHelper.TEXT_TYPE + DevicesDbHelper.COMMA_SEP +
                    DeviceContract.DeviceEntry.COLUMN_NAME_LATITUDE + DevicesDbHelper.TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DeviceContract.DeviceEntry.TABLE_NAME;


    public DevicesDbHelper(final Context context) {
        super(context, DevicesDbHelper.DATABASE_NAME, null, DevicesDbHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(DevicesDbHelper.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DevicesDbHelper.SQL_DELETE_ENTRIES);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }
}
