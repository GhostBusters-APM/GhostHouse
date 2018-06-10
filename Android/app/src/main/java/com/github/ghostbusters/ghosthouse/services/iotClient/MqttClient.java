package com.github.ghostbusters.ghosthouse.services.iotClient;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClient implements IotClient {

    public static final String ALIVE = "ALIVE";
    public static final String ALIVE_ACK = "ALIVE/ACK";
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
                        MqttHelper.publishMessage(mqttAndroidClient, "msg", 1, SWITCH, null);
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
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, DEFAULT_IP, "ANDROID");
        try {
            mqttAndroidClient.connect(MqttHelper.getMqttConnectionOption(), new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken asyncActionToken) {
                    try {
                        mqttAndroidClient.setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(final Throwable cause) {
                            }

                            @Override
                            public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                                if (topic.equals(ALIVE_ACK)) {
                                    response.response(new String(message.getPayload()));
                                }
                            }

                            @Override
                            public void deliveryComplete(final IMqttDeliveryToken token) {
                            }
                        });
                        MqttHelper.publishMessage(mqttAndroidClient, "msg", 1, ALIVE, null);
                        MqttHelper.subscribe(mqttAndroidClient, ALIVE_ACK, 1);
                    } catch (final Exception e) {
                        Log.w(SWITCH, e);
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
    public MqttAndroidClient register(final Context context, final IotResponse response, final String msg) {
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, DEFAULT_IP, "ANDROID");
        try {
            mqttAndroidClient.connect(MqttHelper.getMqttConnectionOption(), new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken asyncActionToken) {
                    try {
                        MqttHelper.publishMessage(mqttAndroidClient, msg, 1, REGISTER, new IMqttActionListener() {
                            @Override
                            public void onSuccess(final IMqttToken asyncActionToken) {
                                try {
                                    mqttAndroidClient.disconnect();
                                } catch (final Exception e1) {
                                    Log.w(SWITCH, e1);
                                }
                            }

                            @Override
                            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                                try {
                                    mqttAndroidClient.disconnect();
                                } catch (final Exception e1) {
                                    Log.w(SWITCH, e1);
                                }
                            }
                        });
                    } catch (final Exception e) {
                        Log.w(SWITCH, e);
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


}
