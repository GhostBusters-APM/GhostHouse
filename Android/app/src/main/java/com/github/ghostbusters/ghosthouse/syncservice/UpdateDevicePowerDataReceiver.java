package com.github.ghostbusters.ghosthouse.syncservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class UpdateDevicePowerDataReceiver extends BroadcastReceiver {

    public static final String USER_ID = "userId";
    public static final String DEVICE_ID = "deviceId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(UpdateDevicePowerDataReceiver.class.getName(), "onReceive");
        Bundle extras = intent.getExtras();
        if (extras != null &&
                extras.containsKey(USER_ID) &&
                extras.containsKey(DEVICE_ID)) {
            String userId = extras.getString(USER_ID);
            int deviceId = extras.getInt(DEVICE_ID);
            RemoteSyncService.updateDevicePowerData(context, userId, deviceId);
        }
    }
}
