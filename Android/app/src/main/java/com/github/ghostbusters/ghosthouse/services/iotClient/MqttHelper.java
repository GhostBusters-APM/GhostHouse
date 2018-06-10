package com.github.ghostbusters.ghosthouse.services.iotClient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MqttHelper {

    private static final String TAG = MqttHelper.class.getName();

    public static void publishMessage(@NonNull final MqttAndroidClient client,
                                      @NonNull final String msg, final int qos, @NonNull final String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        final MqttMessage message = new MqttMessage(encodedPayload);
        message.setId(5866);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }

    public static MqttAndroidClient getMqttClient(final Context context, final String brokerUrl, final String clientId, final IMqttActionListener listener) {
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        try {
            final IMqttToken token = mqttAndroidClient.connect(MqttHelper.getMqttConnectionOption());
            token.setActionCallback(listener);
        } catch (final MqttException e) {
            e.printStackTrace();
        }
        return mqttAndroidClient;
    }


    public static MqttConnectOptions getMqttConnectionOption() {
        final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setUserName("username");
        //mqttConnectOptions.setPassword("password".toCharArray());
        return mqttConnectOptions;
    }

    public static DisconnectedBufferOptions getDisconnectedBufferOptions() {
        final DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(true);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }


    public static void subscribe(@NonNull final MqttAndroidClient client,
                                 @NonNull final String topic, final int qos, final IMqttActionListener listener) throws MqttException {
        final IMqttToken token = client.subscribe(topic, qos);
        token.setActionCallback(listener);
    }


    public static void unSubscribe(@NonNull final MqttAndroidClient client,
                                   @NonNull final String topic) throws MqttException {

        final IMqttToken token = client.unsubscribe(topic);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken iMqttToken) {
                Log.d(MqttHelper.TAG, "UnSubscribe Successfully " + topic);
            }

            @Override
            public void onFailure(final IMqttToken iMqttToken, final Throwable throwable) {
                Log.e(MqttHelper.TAG, "UnSubscribe Failed " + topic);
            }
        });
    }

    public static void disconnect(@NonNull final MqttAndroidClient client)
            throws MqttException {
        final IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken iMqttToken) {
                Log.d(MqttHelper.TAG, "Successfully disconnected");
            }

            @Override
            public void onFailure(final IMqttToken iMqttToken, final Throwable throwable) {
                Log.d(MqttHelper.TAG, "Failed to disconnected " + throwable.toString());
            }
        });
    }
}
