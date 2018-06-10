package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import lombok.Data;

@Data
@Entity
@TypeConverters({DeviceStateConverter.class})
public class Device {
    @PrimaryKey(autoGenerate = true)
    private int id;

    /* Can be null if the device is not assigned to an user */
    private String userId;

    /* Location information can be missing */
    private Double latitude;
    private Double longitude;

    private String name;

    private int type;

    private DeviceState state;
    private String ip;

}
