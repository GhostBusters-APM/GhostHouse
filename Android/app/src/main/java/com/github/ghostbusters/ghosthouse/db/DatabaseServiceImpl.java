package com.github.ghostbusters.ghosthouse.db;

import android.content.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseServiceImpl implements DatabaseService {

    private static DatabaseServiceImpl INSTANCE = null;

    private final AppDatabase db;

    private DatabaseServiceImpl(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public static DatabaseServiceImpl getInstance(Context context) {
        if (INSTANCE == null) {
            buildInstance(context);
        }

        return INSTANCE;
    }

    private static synchronized void buildInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseServiceImpl(context);
        }
    }

    @Override
    public void updateDeviceList(String userId, List<Device> devices) {
        DeviceDao deviceDao = db.deviceModel();

        Set<Integer> newDevicesIds = new HashSet<>();
        for (Device device : devices) {
            newDevicesIds.add(device.getDeviceId());
        }

        List<Device> currentDevices = deviceDao.getDevicesOfUser(userId);
        Set<Integer> currentDevicesIds = new HashSet<>();
        for (Device device : currentDevices) {
            currentDevicesIds.add(device.getDeviceId());
            if (!newDevicesIds.contains(device.getDeviceId())) {
                deviceDao.delete(device);
            }
        }


        for (Device device : devices) {
            if (currentDevicesIds.contains(device.getDeviceId())) {
                deviceDao.update(device);
            } else {
                deviceDao.insertAll(device);
            }
        }
    }

    @Override
    public void updateDevicePower(int deviceId, List<DevicePowerData> devicePowerData) {
        DevicePowerDataDao devicePowerDataDao = db.devicePowerDataDModel();

        devicePowerDataDao.deleteAll(deviceId);
        devicePowerDataDao.insertAll(devicePowerData.toArray(new DevicePowerData[devicePowerData
                .size()]));
    }
}
