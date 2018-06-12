package com.github.ghostbusters.ghosthouse.helper.unity;

import android.content.Context;
import android.widget.Toast;

import com.github.ghostbusters.ghosthouse.services.ServiceProvider;
import com.github.ghostbusters.ghosthouse.services.iotClient.IotClient;

public class ArPlugin {

    public void call(final ArPluginCallback callback, final Context context, final boolean on) {
        Toast.makeText(context, on ? "ON" : "OFF", Toast.LENGTH_SHORT);
        ServiceProvider.getIotClient().switchOn(context, "192.168.0.19", new IotClient.IotResponse() {
            @Override
            public void response(final String msg) {

            }
        }, on ? "ON" : "OFF");
        // Do something
        callback.onSuccess("onSuccess");
        // Do something horrible
        callback.onError("onError");
    }
}
