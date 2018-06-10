package com.github.ghostbusters.ghosthouse.syncservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UpdateDevicesReceiver extends BroadcastReceiver {

    public static final String USER_ID = "userId";

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(USER_ID)) {
            final String userId = extras.getString(USER_ID);
            RemoteSyncService.upadteDevices(context, userId);
        }
    }
}
