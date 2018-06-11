package com.github.ghostbusters.ghosthouse.services.iotClient;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;

public interface IotClient {

    MqttAndroidClient switchOn(final Context context, final String ip, final IotResponse response, final String message);

    MqttAndroidClient checkConnected(final Context context, final IotResponse response);

    MqttAndroidClient register(Context context, final IotResponse response, String message);

    interface IotResponse {
        void response(String msg);
    }
}
