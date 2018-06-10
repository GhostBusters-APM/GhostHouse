package com.github.ghostbusters.ghosthouse.services.iotClient;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClient implements IotClient {

    public static final String ALIVE = "ALIVE";
    public static final String SWITCH = "SWITCH";
    public static final String REGISTER = "REGISTER";
    //private static final String DEFAULT_IP = "192.168.0.1";
    //Cuando ejecutas máquinas virtuales esta es la ip de la máquina en local
    private static final String DEFAULT_IP = "tcp://10.0.2.2:1883";

    @Override
    public MqttAndroidClient switchOn(final Context context, final String ip, final IotResponse response) {

        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, ip, "ANDROID");
        try {
            mqttAndroidClient.connect(MqttHelper.getMqttConnectionOption(), new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken asyncActionToken) {
                    try {
                        MqttHelper.publishMessage(mqttAndroidClient, "msg", 1, SWITCH);
                    } catch (final Exception e) {
                        Log.w(SWITCH, e);
                    } finally {
                        try {
                            mqttAndroidClient.disconnect();
                        } catch (final Exception e1) {
                            Log.w(SWITCH, e1);
                        }
                    }
                }

                @Override
                public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                    try {
                        Log.w(SWITCH, exception);
                        mqttAndroidClient.disconnect();
                    } catch (final Exception e1) {
                        Log.w(SWITCH, e1);
                    }
                }
            });
        } catch (final MqttException e) {
            e.printStackTrace();
            try {
                mqttAndroidClient.disconnect();
            } catch (final Exception e1) {
                Log.w(SWITCH, e1);
            }
        }
        return mqttAndroidClient;

    }

    @Override
    public MqttAndroidClient checkConnected(final Context context, final IotResponse response) {
        //sendWaitResponse(context, DEFAULT_IP, response, ALIVE, ALIVE, "alive");
        try {
            final MqttAndroidClient client = MqttHelper.getMqttClient(context, DEFAULT_IP, "Android", null);
            //MqttHelper.publishMessage(client, "msg", 1, ALIVE);
            if (!client.isConnected()) {
                client.connect();
            }
            MqttHelper.subscribe(client, ALIVE, 1, new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken iMqttToken) {
                    try {
                        if (response != null) {
                            response.response(new String(iMqttToken.getResponse().getPayload()));
                        }
                    } catch (final Exception e) {
                        try {
                            client.disconnect();
                        } catch (final MqttException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final IMqttToken iMqttToken, final Throwable throwable) {
                    if (response != null) {
                        response.response(null);
                    }
                }
            });
            return client;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public MqttAndroidClient register(final Context context, final IotResponse response) {
        return sendWaitResponse(context, DEFAULT_IP, response, REGISTER, REGISTER, "alive");
    }

    private MqttAndroidClient sendWaitResponse(final Context context, final String ip, final IotResponse response, final String tag1, final String tag2, final String message) {
        final MqttAndroidClient client = MqttHelper.getMqttClient(context, ip, "Android", null);

        try {
            MqttHelper.subscribe(client, tag2, 1, new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken iMqttToken) {
                    try {
                        MqttHelper.disconnect(client);
                        response.response(new String(iMqttToken.getResponse().getPayload(), "UTF-8"));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final IMqttToken iMqttToken, final Throwable throwable) {
                    try {
                        MqttHelper.disconnect(client);
                    } catch (final MqttException e) {
                        e.printStackTrace();
                    }
                    response.response(null);
                }
            });
            MqttHelper.publishMessage(client, message, 1, tag1);
        } catch (final Exception e) {
            try {
                MqttHelper.disconnect(client);
            } catch (final MqttException e1) {
                e1.printStackTrace();
            }
            response.response(null);
            e.printStackTrace();
        }
        return client;
    }


}
