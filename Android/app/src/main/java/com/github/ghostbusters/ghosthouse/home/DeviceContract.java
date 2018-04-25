package com.github.ghostbusters.ghosthouse.home;

import android.provider.BaseColumns;

/**
 * Created by javiosa on 16/04/18.
 */

public final class DeviceContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DeviceContract() {
    }

    /* Inner class that defines the table contents */
    public static class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "devices";
        public static final String COLUMN_NAME_USERID = "userId";
        public static final String COLUMN_NAME_DEVICE = "deviceName";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";

    }
}
