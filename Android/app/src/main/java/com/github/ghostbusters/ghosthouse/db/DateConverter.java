package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(long timestamp) {
        return new Date(timestamp);
    }

    @TypeConverter
    public static long toTimestamp(@NonNull Date date) {
        return date.getTime();
    }
}
