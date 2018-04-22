package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.TypeConverter;

public class DeviceStateConverter {
    @TypeConverter
    public static DeviceState toDeviceState(int deviceStateValue) {
        return DeviceState.getByStateValue(deviceStateValue);
    }

    @TypeConverter
    public static int toDeviceStateString(DeviceState deviceState) {
        return deviceState.getStateValue();
    }
}
