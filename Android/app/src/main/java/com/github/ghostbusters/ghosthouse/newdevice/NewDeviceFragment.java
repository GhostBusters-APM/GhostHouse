package com.github.ghostbusters.ghosthouse.newdevice;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.github.ghostbusters.ghosthouse.services.ServiceProvider;

import java.util.Locale;

public class NewDeviceFragment extends Fragment {

    private OnNewDeviceListener listener = null;

    private static final String TAG = "NewDeviceFragment";

    public interface OnNewDeviceListener {
        void onNewDevice(int deviceId, String deviceName);
    }

    private class SearchDevicesTask extends AsyncTask<Void, Integer, Void> {

        private Boolean alive = false;

        @Override
        protected Void doInBackground(final Void... voids) {
            Log.d(NewDeviceFragment.TAG, "starting search for devices");
            ServiceProvider.getIotClient().checkConnected(getContext(), msg -> alive = true);
            while (!alive) {
                try {
                    Thread.sleep(2000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... progress) {
            Log.d(NewDeviceFragment.TAG, String.format(Locale.ENGLISH,
                    "progress so far: %d%%", progress[0]));
        }

        @Override
        protected void onPostExecute(final Void devices) {
            devicesDiscovered(devices);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            listener = (OnNewDeviceListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnNewDeviceListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_device, container, false);
        setUpCallbacks(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new SearchDevicesTask().execute();
    }

    private void setUpCallbacks(final View view) {

        final EditText etDeviceName = view.findViewById(R.id.device_name_et);

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
        butAddDevice.setBackgroundColor(Color.BLUE);
        butAddDevice.setTextColor(Color.WHITE);
        butAddDevice.setOnClickListener(button -> {
            final int selectedDeviceId =
                    spAvailableDevices.getSelectedItemPosition();

            Log.d(NewDeviceFragment.TAG, "add clicked");

            if (listener != null) {
                listener.onNewDevice(selectedDeviceId, String.valueOf(etDeviceName.getText()));
            }
        });
    }

    private void devicesDiscovered(final Void devices) {
        final int[] viewIdsToShow = new int[]{R.id.device_name_tv,
                R.id.device_name_et, R.id.available_devices_tv,
                R.id.available_devices_sp, R.id.add_device_but};
        final int[] viewIdsToHide = new int[]{R.id.loading_devices_pb,
                R.id.loading_devices_tv};

        final View fragmentRootView = getView();

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
