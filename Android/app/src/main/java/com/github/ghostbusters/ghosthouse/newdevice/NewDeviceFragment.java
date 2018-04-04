package com.github.ghostbusters.ghosthouse.newdevice;

import android.content.Context;
import android.content.Intent;
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
        void onNewDevice(int deviceId);
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnNewDeviceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnNewDeviceListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_device, container, false);
        setUpCallbacks(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new SearchDevicesTask().execute();
    }

    private void setUpCallbacks(View view) {

        EditText etDeviceName = view.findViewById(R.id.device_name_et);
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
                view.findViewById(R.id.available_devices_sp);
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

        Button butAddDevice = view.findViewById(R.id.add_device_but);
        butAddDevice.setOnClickListener(button -> {
            int selectedDeviceId =
                    spAvailableDevices.getSelectedItemPosition();

            Log.d(TAG, "add clicked");

            if (listener != null) {
                listener.onNewDevice(selectedDeviceId);
            }
        });
    }

    private void devicesDiscovered(Void devices) {
        int[] viewIdsToShow = new int[]{R.id.device_name_tv,
                R.id.device_name_et, R.id.available_devices_tv,
                R.id.available_devices_sp, R.id.add_device_but};
        int[] viewIdsToHide = new int[]{R.id.loading_devices_pb,
                R.id.loading_devices_tv};

        View fragmentRootView = getView();

        if (fragmentRootView == null) {
            return;
        }

        for (int viewId : viewIdsToHide) {
            View v = fragmentRootView.findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

        for (int viewId : viewIdsToShow) {
            View v = fragmentRootView.findViewById(viewId);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

}
