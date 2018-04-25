package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Device.class, DevicePowerData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DeviceDao deviceModel();

    public abstract DevicePowerDataDao devicePowerDataDModel();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            buildInstance(context);
        }

        return INSTANCE;
    }

    private static synchronized void buildInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "Database.db").build();
        }
    }
}
