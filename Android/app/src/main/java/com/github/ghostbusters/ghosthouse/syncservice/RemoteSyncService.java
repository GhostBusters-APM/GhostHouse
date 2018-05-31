package com.github.ghostbusters.ghosthouse.syncservice;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.github.ghostbusters.ghosthouse.db.Device;

import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RemoteSyncService extends JobIntentService {
    private static final String TAG = RemoteSyncService.class.getName();

    private static final int JOB_ID = 1001;

    private static final String ACTION_ADD_DEVICE =
            "com.example.alfonso.myapplication.ADD_DEVICE";
    private static final String ACTION_UPDATE_DEVICES =
            "com.example.alfonso.myapplication.UPDATE_DEVICES";

    private static final String ADD_DEVICE_USERID_PARAM = "userId";
    private static final String ADD_DEVICE_NAME_PARAM = "name";
    private static final String ADD_DEVICE_LAT_PARAM = "latituide";
    private static final String ADD_DEVICE_LON_PARAM = "longitude";
    private static final String ADD_DEVICE_TYPE_PARAM = "type";
    private static final String ADD_DEVICE_STATE_PARAM = "state";

    private static final String UPDATE_DEVICES_USERID_PARAM = "userId";

    public static void addDevice(Context context, String userID, String name, double lat,
                                 double lon, int type, boolean state) {
        Intent work = new Intent(context, RemoteSyncService.class);
        work.setAction(ACTION_ADD_DEVICE);
        work.putExtra(ADD_DEVICE_USERID_PARAM, userID);
        work.putExtra(ADD_DEVICE_NAME_PARAM, name);
        work.putExtra(ADD_DEVICE_LAT_PARAM, lat);
        work.putExtra(ADD_DEVICE_LON_PARAM, lon);
        work.putExtra(ADD_DEVICE_TYPE_PARAM, type);
        work.putExtra(ADD_DEVICE_STATE_PARAM, state);

        enqueueWork(context, work);
    }

    public static void upadteDevices(Context context, String userID) {
        Intent work = new Intent(context, RemoteSyncService.class);
        work.setAction(ACTION_UPDATE_DEVICES);
        work.putExtra(UPDATE_DEVICES_USERID_PARAM, userID);

        enqueueWork(context, work);
    }

    private static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RemoteSyncService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (ACTION_ADD_DEVICE.equals(action)) {
            doAddDevice(intent);
        } else if (ACTION_UPDATE_DEVICES.equals(action)) {
            doUpdateDevices(intent);
        }

    }

    private void doAddDevice(@NonNull Intent intent) {
        Log.d(TAG, "doAddDevice");
        String userId = intent.getStringExtra(ADD_DEVICE_USERID_PARAM);
        String name = intent.getStringExtra(ADD_DEVICE_NAME_PARAM);
        double lat = intent.getDoubleExtra(ADD_DEVICE_LAT_PARAM, 0);
        double lon = intent.getDoubleExtra(ADD_DEVICE_LON_PARAM, 0);
        int type = intent.getIntExtra(ADD_DEVICE_TYPE_PARAM, 0);
        boolean state = intent.getBooleanExtra(ADD_DEVICE_STATE_PARAM, true);

        RestTemplate t = new RestTemplate();
        t.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Device d = new Device();
        d.setUserId(userId);
        d.setName(name);
        d.setLatitude(lat);
        d.setLongitude(lon);
        d.setType(type);
        d.setState(state);
        HttpEntity<Device> e = new HttpEntity<>(d);
        Device d2 = t.postForObject("http://10.0.2.2:8080/device", e, Device.class);

        Log.d(TAG, "Device: " + d2);
    }

    private void doUpdateDevices(@NonNull Intent intent) {
        throw new UnsupportedOperationException();
    }

}
