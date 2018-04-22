package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import lombok.Data;

@Data
@Entity(foreignKeys = @ForeignKey(entity = Device.class,
                                  parentColumns = "id",
                                  childColumns = "device_id"))
@TypeConverters({DateConverter.class})
public class DevicePowerData {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "device_id")
    private int deviceId;

    private Date date;

    private double value;
}
