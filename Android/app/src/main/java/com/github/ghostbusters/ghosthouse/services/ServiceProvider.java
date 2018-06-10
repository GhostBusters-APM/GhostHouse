package com.github.ghostbusters.ghosthouse.services;

import com.github.ghostbusters.ghosthouse.services.iotClient.IotClient;
import com.github.ghostbusters.ghosthouse.services.iotClient.MqttClient;

public class ServiceProvider {


    private static final IotClient client = new MqttClient();

    public static IotClient getIotClient() {
        return client;
    }
}
