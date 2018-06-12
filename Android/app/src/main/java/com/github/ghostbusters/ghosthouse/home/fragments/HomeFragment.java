package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.db.AppDatabase;
import com.github.ghostbusters.ghosthouse.newdevice.NewDevice;
import com.github.ghostbusters.ghosthouse.services.ServiceProvider;
import com.github.ghostbusters.ghosthouse.syncservice.RemoteSyncService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private static final String LOCAL_TEST_USERID = "local-user";

    private static final int ADD_DEVICE_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleSignInClient mGoogleSignInClient;

    private String userId = null;

    private boolean mIsDualPane = false;
    private MqttAndroidClient service;

    public HomeFragment() { }

    @Override
    public void onPause() {
        try {
            if (service!=null){
                service.disconnect();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            mIsDualPane = true;
        }

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String userId;
        if (HomeFragment.this.userId == null) {
            final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount
                    (getContext());
            if (acct != null) {
                userId = acct.getId();
            } else {
                userId = HomeFragment.LOCAL_TEST_USERID;
            }
        } else {
            userId = HomeFragment.this.userId;
        }

        /* Schedule an update of the devices when the fragment is shown */
        RemoteSyncService.upadteDevices(getContext(), userId);

        /* Set up add device button callback */
        FloatingActionButton add_device_fab = view.findViewById(R.id.add_device_fab);
        add_device_fab.setBackgroundColor(Color.BLUE);
        add_device_fab.setRippleColor(Color.WHITE);
        add_device_fab.setOnClickListener(v -> {
            Log.d(HomeFragment.TAG, "Add new device");
            final Intent intent = new Intent(getActivity(), NewDevice.class);
            startActivityForResult(intent, HomeFragment.ADD_DEVICE_REQUEST_CODE);
        });

        /* Set up RecyclerView */
        RecyclerView deviceListView = view.findViewById(R.id.devices_rv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        deviceListView.setLayoutManager(layoutManager);
        final DeviceListAdapter adapter = new DeviceListAdapter();
        deviceListView.setAdapter(adapter);

        /* Update RecyclerView contents every time data changes.
         * Registering the observer triggers an update that populates the contents
         * the 1st time.
         */
        AppDatabase.getInstance(getContext()).deviceModel()
                .getDevicesOfUserLive(userId)
                .observe(this, devices -> {
                    Log.d(TAG, "devices changed. Number of devices: " +
                            (devices == null? 0 : devices.size()));
                    adapter.setDeviceList(devices);
                });

    }

    private void saveDevice(final String name, final String wifi, final String password) {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            final Location mLastLocation = task.getResult();
                            Log.d(HomeFragment.TAG, "Localizacion:");
                            final String latitutde = String.valueOf(mLastLocation.getLatitude());
                            Log.d(HomeFragment.TAG, latitutde);
                            final String longitude = String.valueOf(mLastLocation.getLongitude());
                            Log.d(HomeFragment.TAG, longitude);
                            //obtengo el userID
                            final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                            if (acct != null) {
                                userId = acct.getId();
                            }
                            final ObjectMapper mapper = new ObjectMapper();
                            final ObjectNode objectNode1 = mapper.createObjectNode();
                            objectNode1.put("name", name);
                            objectNode1.put("userId", userId);
                            objectNode1.put("latitude", latitutde);
                            objectNode1.put("longitude", longitude);
                            objectNode1.put("ssid", wifi);
                            objectNode1.put("password", password);
                            String json;
                            try {
                                json = mapper.writeValueAsString(objectNode1);
                            } catch (final JsonProcessingException e) {
                                json = "";
                            }
                            service = ServiceProvider.getIotClient().register(getContext(), msg -> {
                                try {
                                    service.disconnect();

                                    RemoteSyncService.upadteDevices(HomeFragment.this.getContext(), userId);
                                    Thread.sleep(100);
                                } catch (final MqttException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }, json);
                        } else {
                            Log.w(HomeFragment.TAG, "getLastLocation:exception", task.getException());

                        }

                    });
        } catch (final SecurityException se) {
            se.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
        switch (requestCode) {
            case HomeFragment.ADD_DEVICE_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        final String wifi = resultData.getStringExtra(NewDevice.DEVICE_WIFI_SSID_RESULT);
                        final String password = resultData.getStringExtra(NewDevice.DEVICE_PASSWORD_RESULT);
                        final String newDeviceName = resultData.getStringExtra(NewDevice.DEVICE_NAME_RESULT);
                        saveDevice(newDeviceName, wifi, password);
                        break;
                    case RESULT_CANCELED:
                        Log.d(HomeFragment.TAG, "adding new device cancelled of failed");
                        break;
                    default:
                        Log.d(HomeFragment.TAG, "unknown result code for new device request");
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, resultData);
        }
    }

}
