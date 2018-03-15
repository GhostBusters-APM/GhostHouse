package com.github.ghostbusters.ghosthouse.newdevice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.ghostbusters.ghosthouse.R;

import java.util.Locale;

public class NewDevice extends AppCompatActivity {

    public static final String DEVICE_ID_RESULT = "device-id";

    private static final String TAG = "NewDevice";

    private class SearchDevicesTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "starting search for devices");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "search thread interrupted");
            }
            publishProgress(50);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "search thread interrupted");
            }
            Log.d(TAG, "done searching for devices");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, String.format(Locale.ENGLISH,
                    "progress so far: %d%%", progress[0]));
        }

        @Override
        protected void onPostExecute(Void devices) {
            devicesDiscovered(devices);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        new SearchDevicesTask().execute();

        setUpCallbacks();
    }

    private void setUpCallbacks() {

        EditText etDeviceName = findViewById(R.id.device_name_et);
        etDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, String.format(Locale.ENGLISH,
                        "device name changed: %s", editable));
            }
        });

        final Spinner spAvailableDevices =
                findViewById(R.id.available_devices_sp);
        spAvailableDevices.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int i, long l) {
                ArrayAdapter<String> adapter =
                        (ArrayAdapter<String>) adapterView.getAdapter();
                Log.d(TAG, String.format(Locale.ENGLISH, "device selected: %s",
                        adapter.getItem(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "no device selected");
            }
        });

        Button butAddDevice = findViewById(R.id.add_device_but);
        butAddDevice.setOnClickListener(view -> {
            int selectedDeviceId =
                    spAvailableDevices.getSelectedItemPosition();
            Intent result = new Intent();
            result.putExtra(DEVICE_ID_RESULT, selectedDeviceId);
            setResult(RESULT_OK, result);

            Log.d(TAG, "add clicked, finishing activity");

            finish();
        });
    }

    private void devicesDiscovered(Void devices) {
        int[] viewIdsToShow = new int[]{R.id.device_name_tv,
                R.id.device_name_et, R.id.available_devices_tv,
                R.id.available_devices_sp, R.id.add_device_but};
        int[] viewIdsToHide = new int[]{R.id.loading_devices_pb,
                R.id.loading_devices_tv};

        for (int viewId : viewIdsToHide) {
            View v = findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

        for (int viewId : viewIdsToShow) {
            View v = findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }


    /*
     * Make the up button in the toolbar emulate the back button
     * on the device.
     *
     * https://stackoverflow.com/a/39718652/8923487
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

}
