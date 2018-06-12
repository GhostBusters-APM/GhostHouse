package com.github.ghostbusters.ghosthouse.syncservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.github.ghostbusters.ghosthouse.db.AppDatabase;
import com.github.ghostbusters.ghosthouse.db.DatabaseService;
import com.github.ghostbusters.ghosthouse.db.DatabaseServiceImpl;
import com.github.ghostbusters.ghosthouse.db.Device;
import com.github.ghostbusters.ghosthouse.db.DevicePowerData;

import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class RemoteSyncService extends JobIntentService {
    private static final String TAG = RemoteSyncService.class.getSimpleName();

    private static final int JOB_ID = 1001;

    private static final String ACTION_ADD_DEVICE =
            "com.example.alfonso.myapplication.ADD_DEVICE";
    private static final String ACTION_UPDATE_DEVICES =
            "com.example.alfonso.myapplication.UPDATE_DEVICES";
    private static final String ACTION_UPDATE_DEVICE_POWER_DATA =
            "com.example.alfonso.myapplication.UPDATE_DEVICE_POWER_DATA";

    private static final String ADD_DEVICE_USERID_PARAM = "userId";
    private static final String ADD_DEVICE_NAME_PARAM = "name";
    private static final String ADD_DEVICE_LAT_PARAM = "latituide";
    private static final String ADD_DEVICE_LON_PARAM = "longitude";
    private static final String ADD_DEVICE_TYPE_PARAM = "type";
    private static final String ADD_DEVICE_STATE_PARAM = "state";
    private static final String ADD_DEVICE_IP_PARAM = "ip";

    private static final String UPDATE_DEVICES_USERID_PARAM = "userId";

    private static final String UPDATE_DEVICE_POWER_DATA_USERID_PARAM = "userId";
    private static final String UPDATE_DEVICE_POWER_DATA_DEVICEID_PARAM = "deviceId";

    /*
     * Enqueue addition of a device to the backend.
     *
     * Currently unused as registration is done by the IoT device.
     */
    public static void addDevice(Context context, String userId, String name, double lat,
                                 double lon, int type, boolean state, String ip) {
        Intent work = new Intent(context, RemoteSyncService.class);
        work.setAction(ACTION_ADD_DEVICE);
        work.putExtra(ADD_DEVICE_USERID_PARAM, userId);
        work.putExtra(ADD_DEVICE_NAME_PARAM, name);
        work.putExtra(ADD_DEVICE_LAT_PARAM, lat);
        work.putExtra(ADD_DEVICE_LON_PARAM, lon);
        work.putExtra(ADD_DEVICE_TYPE_PARAM, type);
        work.putExtra(ADD_DEVICE_STATE_PARAM, state);
        work.putExtra(ADD_DEVICE_IP_PARAM, ip);

        enqueueWork(context, work);
    }

    /*
     * Enqueue update for user devices.
     */
    public static void updateDevices(Context context, String userId) {
        Intent work = new Intent(context, RemoteSyncService.class);
        work.setAction(ACTION_UPDATE_DEVICES);
        work.putExtra(UPDATE_DEVICES_USERID_PARAM, userId);

        enqueueWork(context, work);
    }

    /*
     * Enqueue update  for power data of the device.
     */
    public static void updateDevicePowerData(Context context, String userId, int deviceId) {
        Intent work = new Intent(context, RemoteSyncService.class);
        work.setAction(ACTION_UPDATE_DEVICE_POWER_DATA);
        work.putExtra(UPDATE_DEVICE_POWER_DATA_USERID_PARAM, userId);
        work.putExtra(UPDATE_DEVICE_POWER_DATA_DEVICEID_PARAM, deviceId);

        enqueueWork(context, work);
    }

    private static PendingIntent buildDevicePowerDataUpdatesIntent(Context context,
                                                                   String userId,
                                                                   int deviceId) {
        Intent intent = new Intent(context, UpdateDevicePowerDataReceiver.class);
        intent.putExtra(UpdateDevicePowerDataReceiver.USER_ID, userId);
        intent.putExtra(UpdateDevicePowerDataReceiver.DEVICE_ID, deviceId);

        return PendingIntent.getBroadcast(context, deviceId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /*
     * Start automatic updates of power data of a device.
     */
    public static void startDevicePowerDataUpdates(Context context, String userId, int deviceId) {
        updateDevicePowerData(context, userId, deviceId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 5 * 1000,
                5 * 1000,
                buildDevicePowerDataUpdatesIntent(context, userId, deviceId));
    }

    /*
     * Stops automatic updates of power data of a device.
     */
    public static void stopDevicePowerDataUpdates(Context context, String userId, int deviceId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        alarmManager.cancel(buildDevicePowerDataUpdatesIntent(context, userId, deviceId));
    }

    private static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RemoteSyncService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_ADD_DEVICE:
                    doAddDevice(intent);
                    break;
                case ACTION_UPDATE_DEVICES:
                    doUpdateDevices(intent);
                    break;
                case ACTION_UPDATE_DEVICE_POWER_DATA:
                    doUpdateDevicePowerData(intent);
                    break;
            }
        }

    }

    private void doAddDevice(@NonNull Intent intent) {
        Log.d(TAG, "doAddDevice");

        /* Retrieve param from intent */
        String userId = intent.getStringExtra(ADD_DEVICE_USERID_PARAM);
        String name = intent.getStringExtra(ADD_DEVICE_NAME_PARAM);
        double lat = intent.getDoubleExtra(ADD_DEVICE_LAT_PARAM, 0);
        double lon = intent.getDoubleExtra(ADD_DEVICE_LON_PARAM, 0);
        int type = intent.getIntExtra(ADD_DEVICE_TYPE_PARAM, 0);
        boolean state = intent.getBooleanExtra(ADD_DEVICE_STATE_PARAM, true);
        String ip = intent.getStringExtra(ADD_DEVICE_IP_PARAM);

        /* Build request body */
        RestTemplate t = new RestTemplate();
        t.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Device d = new Device();
        d.setUserId(userId);
        d.setName(name);
        d.setLatitude(lat);
        d.setLongitude(lon);
        d.setType(type);
        d.setState(state);
        d.setIp(ip);
        HttpEntity<Device> e = new HttpEntity<>(d);

        /* Make request */
        Device d2;
        try {
            d2 = t.postForObject(getUrl("device"), e, Device.class);
        } catch (Exception ex) {
            Log.d(TAG, "could not add device", ex);
            return;
        }
        Log.d(TAG, "Device: " + d2);

        /* Enqueue update of local list of devices to get the just added device. */
        RemoteSyncService.updateDevices(getApplicationContext(), userId);
    }

    private void doUpdateDevices(@NonNull Intent intent) {
        Log.d(TAG, "doUpdateDevices");

        String userId = intent.getStringExtra(UPDATE_DEVICES_USERID_PARAM);

        String url = UriComponentsBuilder.fromHttpUrl(getUrl("device"))
                .queryParam("userId", userId)
                .toUriString();
        Log.d(TAG, "query url: " + url);

        RestTemplate t = new RestTemplate();
        t.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        Device[] devices;
        try {
            devices = t.getForObject(url, Device[].class);
        } catch (Exception ex) {
            Log.d(TAG, "could not update devices", ex);
            return;
        }

        Log.d(TAG, "number of devices: " + devices.length);

        DatabaseService dbService = DatabaseServiceImpl.getInstance(getApplicationContext());
        dbService.updateDeviceList(userId, Arrays.asList(devices));

        /* Schedule an update in 15 minutes */
        Intent updateIntent = new Intent(this, UpdateDevicesReceiver.class);
        updateIntent.putExtra(UpdateDevicesReceiver.USER_ID, userId);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent);
    }

    private void doUpdateDevicePowerData(@NonNull Intent intent) {
        Log.d(TAG, "doUpdateDevicePowerData");

        String userId = intent.getStringExtra(UPDATE_DEVICE_POWER_DATA_USERID_PARAM);
        int deviceId = intent.getIntExtra(UPDATE_DEVICE_POWER_DATA_DEVICEID_PARAM, -1);

        /*
         * Check the device is in the local database before updating.
         *
         * Adding device power data for a device that is not in a database
         * will err out with a SQLiteConstraintException when inserting the data
         * in the database.
         *
         * This shields against errors that should not happen, but should also not
         * crash the application.
         */
        Device device = AppDatabase.getInstance(this).deviceModel()
                .getDeviceByDeviceIdAndUserId(deviceId, userId);
        if (device == null) {
            Log.d(TAG, String.format(Locale.ENGLISH,
                    "trying to update power data of a device that is not in the database. userId=%s, deviceId=%d",
                    userId, deviceId));
            return;
        }

        String url = UriComponentsBuilder.fromHttpUrl(getUrl("/devicePower"))
                .queryParam("userId", userId)
                .queryParam("deviceId", deviceId)
                .toUriString();
        Log.d(TAG, "query url: " + url);

        RestTemplate t = new RestTemplate();
        t.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        DevicePowerDtoResponse[] devicePowerResponse;
        try {
            devicePowerResponse = t.getForObject(url, DevicePowerDtoResponse[].class);
        } catch (Exception ex) {
            Log.d(TAG, "could not update device power data", ex);
            return;
        }
        Log.d(TAG, "number of power events: " + devicePowerResponse.length);

        List<DevicePowerData> devicePowerDataList = new ArrayList<>(devicePowerResponse.length);
        for (DevicePowerDtoResponse devicePowerDtoResponse : devicePowerResponse) {
            DevicePowerData devicePowerData = new DevicePowerData();
            devicePowerData.setDevice(deviceId);
            devicePowerData.setDevicePowerDataId(devicePowerDtoResponse.getId());
            devicePowerData.setDate(devicePowerDtoResponse.getFrom());
            devicePowerData.setValue(devicePowerDtoResponse.getValue());
            devicePowerDataList.add(devicePowerData);
        }

        DatabaseService dbService = DatabaseServiceImpl.getInstance(getApplicationContext());
        dbService.updateDevicePower(deviceId, devicePowerDataList);
    }

    private String getUrl(String endPoint) {
        Properties properties = new Properties();
        AssetManager assetManager = getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "http://10.0.2.2:8080/" + endPoint;
        }

        String baseUrl = properties.getProperty("backendBaseUrl");
        return baseUrl + endPoint;
    }
}
