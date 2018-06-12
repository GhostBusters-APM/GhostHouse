package com.github.ghostbusters.ghosthouse.db;

import java.util.List;

public interface DatabaseService {

    void updateDeviceList(String userId, List<Device> devices);

    void updateDevicePower(int deviceId, List<DevicePowerData> devicePowerData);
}
