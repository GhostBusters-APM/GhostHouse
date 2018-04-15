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

public class NewDevice
        extends AppCompatActivity
        implements NewDeviceFragment.OnNewDeviceListener{

    public static final String DEVICE_ID_RESULT = "device-id";

    private static final String TAG = "NewDevice";

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

    }

    public void onNewDevice(int deviceId) {
        Intent result = new Intent();
        result.putExtra(DEVICE_ID_RESULT, deviceId);
        setResult(RESULT_OK, result);

        finish();
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
