package com.github.ghostbusters.ghosthouse.newdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.ghostbusters.ghosthouse.R;

public class NewDevice
        extends AppCompatActivity
        implements NewDeviceFragment.OnNewDeviceListener {

    public static final String DEVICE_ID_RESULT = "device-id";
    public static final String DEVICE_NAME_RESULT = "device-name";

    private static final String TAG = "NewDevice";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_new_device);
        final Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    public void onNewDevice(final int deviceId, final String deviceName) {
        Log.d(NewDevice.TAG, String.valueOf(deviceId));
        final Intent result = new Intent();
        result.putExtra(NewDevice.DEVICE_ID_RESULT, deviceId);
        result.putExtra(NewDevice.DEVICE_NAME_RESULT, deviceName);
        this.setResult(Activity.RESULT_OK, result);

        this.finish();
    }

    /*
     * Make the up button in the toolbar emulate the back button
     * on the device.
     *
     * https://stackoverflow.com/a/39718652/8923487
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

}
