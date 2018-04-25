package com.github.ghostbusters.ghosthouse.newdevice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.ghostbusters.ghosthouse.R;

import java.util.Locale;

public class NewDeviceFragment extends Fragment {

    private OnNewDeviceListener listener = null;

    private static final String TAG = "NewDeviceFragment";

    public interface OnNewDeviceListener {
        void onNewDevice(int deviceId, String deviceName);
    }

    private class SearchDevicesTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(final Void... voids) {
            Log.d(NewDeviceFragment.TAG, "starting search for devices");
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                Log.d(NewDeviceFragment.TAG, "search thread interrupted");
            }
            this.publishProgress(50);
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                Log.d(NewDeviceFragment.TAG, "search thread interrupted");
            }
            Log.d(NewDeviceFragment.TAG, "done searching for devices");
            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... progress) {
            Log.d(NewDeviceFragment.TAG, String.format(Locale.ENGLISH,
                    "progress so far: %d%%", progress[0]));
        }

        @Override
        protected void onPostExecute(final Void devices) {
            NewDeviceFragment.this.devicesDiscovered(devices);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            this.listener = (OnNewDeviceListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnNewDeviceListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_device, container, false);
        this.setUpCallbacks(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new SearchDevicesTask().execute();
    }

    private void setUpCallbacks(final View view) {

        final EditText etDeviceName = view.findViewById(R.id.device_name_et);
        etDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence,
                                          final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence,
                                      final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                Log.d(NewDeviceFragment.TAG, String.format(Locale.ENGLISH,
                        "device name changed: %s", editable));
            }
        });

        final Spinner spAvailableDevices =
                view.findViewById(R.id.available_devices_sp);
        spAvailableDevices.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView,
                                       final View view, final int i, final long l) {
                final ArrayAdapter<String> adapter =
                        (ArrayAdapter<String>) adapterView.getAdapter();
                Log.d(NewDeviceFragment.TAG, String.format(Locale.ENGLISH, "device selected: %s",
                        adapter.getItem(i)));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                Log.d(NewDeviceFragment.TAG, "no device selected");
            }
        });

        final Button butAddDevice = view.findViewById(R.id.add_device_but);
        butAddDevice.setOnClickListener(button -> {
            final int selectedDeviceId =
                    spAvailableDevices.getSelectedItemPosition();

            Log.d(NewDeviceFragment.TAG, "add clicked");

            if (this.listener != null) {
                this.listener.onNewDevice(selectedDeviceId, String.valueOf(etDeviceName.getText()));
            }
        });
    }

    private void devicesDiscovered(final Void devices) {
        final int[] viewIdsToShow = new int[]{R.id.device_name_tv,
                R.id.device_name_et, R.id.available_devices_tv,
                R.id.available_devices_sp, R.id.add_device_but};
        final int[] viewIdsToHide = new int[]{R.id.loading_devices_pb,
                R.id.loading_devices_tv};

        final View fragmentRootView = this.getView();

        if (fragmentRootView == null) {
            return;
        }

        for (final int viewId : viewIdsToHide) {
            final View v = fragmentRootView.findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

        for (final int viewId : viewIdsToShow) {
            final View v = fragmentRootView.findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

}
