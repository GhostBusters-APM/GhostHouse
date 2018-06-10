package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.home.DeviceContract;
import com.github.ghostbusters.ghosthouse.home.DevicesDbHelper;
import com.github.ghostbusters.ghosthouse.newdevice.NewDevice;
import com.github.ghostbusters.ghosthouse.services.ServiceProvider;
import com.github.ghostbusters.ghosthouse.syncservice.RemoteSyncService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 */
public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private static final String LOCAL_TEST_USERID = "local-user";

    private static final int ADD_DEVICE_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleSignInClient mGoogleSignInClient;

    private Context context;
    private DevicesDbHelper mDbHelper;

    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private String userId = null;

    boolean mIsDualPane = false;
    private MqttAndroidClient service;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        try {
            service.disconnect();
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
        context = getActivity();
        mDbHelper = new DevicesDbHelper(context);
    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
//        this.mLatitudeText = (TextView) view.findViewById((R.id.latitude_text));
//        this.mLongitudeText = (TextView) view.findViewById((R.id.longitude_text));

        return view;
    }


    private ArrayList<GhostDevice> getDevices() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        final String[] projection = {
                BaseColumns._ID,
                DeviceContract.DeviceEntry.COLUMN_NAME_USERID,
                DeviceContract.DeviceEntry.COLUMN_NAME_DEVICE,
                DeviceContract.DeviceEntry.COLUMN_NAME_LATITUDE,
                DeviceContract.DeviceEntry.COLUMN_NAME_LONGITUDE

        };

// Filter results WHERE "title" = 'My Title'
        final String selection = DeviceContract.DeviceEntry.COLUMN_NAME_USERID + " = ?";
        final String[] selectionArgs = {"My Title"};

// How you want the results sorted in the resulting Cursor
        final String sortOrder =
                DeviceContract.DeviceEntry.COLUMN_NAME_USERID + " DESC";

        final Cursor cursor = db.query(
                DeviceContract.DeviceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        final ArrayList<GhostDevice> devices = new ArrayList<GhostDevice>();
        while (cursor.moveToNext()) {
            final GhostDevice gd = new GhostDevice();
            final long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DeviceContract.DeviceEntry._ID));
            final String name = cursor.getString(cursor.getColumnIndexOrThrow(DeviceContract.DeviceEntry.COLUMN_NAME_DEVICE));
            final String userid = cursor.getString(cursor.getColumnIndexOrThrow(DeviceContract.DeviceEntry.COLUMN_NAME_USERID));
            final String lt = cursor.getString(cursor.getColumnIndexOrThrow(DeviceContract.DeviceEntry.COLUMN_NAME_LATITUDE));
            final String lg = cursor.getString(cursor.getColumnIndexOrThrow(DeviceContract.DeviceEntry.COLUMN_NAME_LONGITUDE));
            gd.setId(itemId);
            gd.setName(name);
            gd.setUserId(userid);
            gd.setLatitude(lt);
            gd.setLongitude(lg);
            devices.add(gd);
        }
        cursor.close();


        return devices;
    }

    private void printDevices(final ArrayList<GhostDevice> list) {

        for (int i = 0; i < list.size(); i++) {
            Log.d(HomeFragment.TAG, String.format(Locale.ENGLISH, "Device %s with id %d from user %s with loc (latitude: %s , longitude: %s)",
                    list.get(i).getName(),
                    list.get(i).getId(),
                    list.get(i).getUserId(),
                    list.get(i).getLatitude(),
                    list.get(i).getLongitude()
            ));
        }
        // print the number of contacts
        Log.d(TAG, String.format("Number of ghost devices: %d", list.size()));
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ArrayList<GhostDevice> devicesList = getDevices();
        printDevices(devicesList);

        final ImageButton imageView = view.findViewById(R.id.fragment_a_imageButton);

        final ImageButton add = view.findViewById(R.id.addImage);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DeviceDetailsFragment simpleFragmentB = new DeviceDetailsFragment();
                if (!mIsDualPane) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                            .addToBackStack(HomeFragment.TAG)
                            .replace(R.id.home_base_frame, simpleFragmentB)
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                            .addToBackStack(HomeFragment.TAG)
                            .replace(R.id.fragment_device_details, simpleFragmentB)
                            .commit();
                }
            }
        });


        final Switch switchButton = view.findViewById(R.id.switch1);
        switchButton.setOnCheckedChangeListener(this::onCheckedChanged);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(HomeFragment.TAG, "Add new device");
                final Intent intent = new Intent(getActivity(), NewDevice.class);
                startActivityForResult(intent, HomeFragment.ADD_DEVICE_REQUEST_CODE);
            }
        });

        final Button addButton = view.findViewById(R.id.add_but);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                RemoteSyncService.addDevice(getContext(), userId, "lala", 12.5, -3, 1,
                        true, "10.10.10.10");
            }
        });

        final Button updateButton = view.findViewById(R.id.update_but);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                RemoteSyncService.upadteDevices(getContext(), userId);
            }
        });

        final Button updatePowerButton = view.findViewById(R.id.update_power_but);
        updatePowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                RemoteSyncService.updateDevicePowerData(getContext(), userId, 1);
            }
        });

        final Button startUpdatePowerButton = view.findViewById(R.id.start_update_power_but);
        startUpdatePowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                RemoteSyncService.startDevicePowerDataUpdates(getContext(), userId, 1);
            }
        });

        final Button stopUpdatePowerButton = view.findViewById(R.id.stop_update_power_but);
        stopUpdatePowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                RemoteSyncService.stopDevicePowerDataUpdates(getContext(), userId, 1);
            }
        });
    }

    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        service = ServiceProvider.getIotClient().switchOn(getContext(), "tcp://10.0.2.2:1883", null);
    }

    private void saveInBD(final String name, final String id, final String lg, final String lt) {
        // Gets the data repository in write mode
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        final ContentValues values = new ContentValues();
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_USERID, id);
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_DEVICE, name);
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_LATITUDE, lt);
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME_LONGITUDE, lg);
        // Insert the new row, returning the primary key value of the new row
        final long newRowId = db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, values);
    }

    private void saveDevice(final String name, final String wifi, final String password) {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull final Task<Location> task) {
                            // ...
                            if (task.isSuccessful() && task.getResult() != null) {
                                final Location mLastLocation = task.getResult();

//                                HomeFragment.this.mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                        HomeFragment.this.mLatitudeLabel,
//                                        mLastLocation.getLatitude()));
//                                HomeFragment.this.mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                        HomeFragment.this.mLongitudeLabel,
//                                        mLastLocation.getLongitude()));
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
                                //guardo en la BD de SQLite
                                saveInBD(name, userId, longitude, latitutde);
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
                                    } catch (final MqttException e) {
                                        e.printStackTrace();
                                    }
                                }, json);
                            } else {
                                Log.w(HomeFragment.TAG, "getLastLocation:exception", task.getException());

                            }

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
