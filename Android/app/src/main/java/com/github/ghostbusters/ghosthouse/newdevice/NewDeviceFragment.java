package com.github.ghostbusters.ghosthouse.newdevice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.services.ServiceProvider;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NewDeviceFragment extends Fragment {

    private OnNewDeviceListener listener = null;

    private static final String TAG = "NewDeviceFragment";
    private Spinner spAvailableDevices;
    private BroadcastReceiver broadcastReceiver;

    public interface OnNewDeviceListener {
        void onNewDevice(String name, String wifiName, String wifiPassword);
    }

    private class SearchDevicesTask extends AsyncTask<Void, Integer, Void> {

        private Boolean alive = false;

        @Override
        protected Void doInBackground(final Void... voids) {
            Log.d(NewDeviceFragment.TAG, "starting search for devices");
            final MqttAndroidClient conected = ServiceProvider.getIotClient().checkConnected(getContext(), msg -> alive = true);
            while (!alive) {
                try {
                    Thread.sleep(2000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                conected.close();
            } catch (final Exception e) {

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
        if (broadcastReceiver != null) {
            try {
                getActivity().unregisterReceiver(broadcastReceiver);
            } catch (final Exception e) {

            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        final EditText etDeviceWifiPassword = view.findViewById(R.id.device_password_et);
        spAvailableDevices = view.findViewById(R.id.available_devices_sp);

        final TextView tvWifiEnable = view.findViewById(R.id.wifi_enabled_tv);
        ConnectivityManager conMan = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        ImageView mImgCheck = view.findViewById(R.id.check_image);
        ImageView mImgCross = view.findViewById(R.id.check_cross);
        mImgCheck.setVisibility(ImageView.INVISIBLE);
        mImgCross.setVisibility(ImageView.INVISIBLE);
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected()) {
            tvWifiEnable.setText(getText(R.string.wifi_enabled));
            mImgCheck.setVisibility(ImageView.VISIBLE);
            ((Animatable) mImgCheck.getDrawable()).start();

        }
        getWifiData(view);
        final Button butAddDevice = view.findViewById(R.id.add_device_but);
        butAddDevice.setBackgroundColor(Color.BLUE);
        butAddDevice.setTextColor(Color.WHITE);
        butAddDevice.setOnClickListener(button -> {
            final String selectedWifi =
                    spAvailableDevices.getSelectedItem().toString();

            Log.d(NewDeviceFragment.TAG, "add clicked");

            if (listener != null) {
                final String password = String.valueOf(etDeviceWifiPassword.getText());
                listener.onNewDevice(String.valueOf(etDeviceName.getText()), selectedWifi, password);
            }
        });
    }

    private void getWifiData(View view) {

        ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
        final WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            wifi.setWifiEnabled(true);

            TextView tvWifiEnable = view.findViewById(R.id.wifi_enabled_tv);
            ImageView mImgCheck = view.findViewById(R.id.check_image);
            tvWifiEnable.setText(getText(R.string.wifi_activated));
            mImgCheck.setVisibility(ImageView.VISIBLE);
            ((Animatable) mImgCheck.getDrawable()).start();
        }
        getWifiResults(wifi);
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(final Context c, final Intent intent) {
//                getWifiResults(wifi);
//                getContext().unregisterReceiver(broadcastReceiver);
//                broadcastReceiver = null;
//            }
//        };
//        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        wifi.startScan();
    }

    private void getWifiResults(final WifiManager wifi) {
        final List<ScanResult> scanResults = wifi.getScanResults();

        int index = 0;
        final Set<String> results = new HashSet<String>();
        for (final ScanResult s : scanResults) {
            results.add(s.SSID);
        }

        final String[] arraySpinner = new String[results.size()];
        int i = 0;
        for (String ssid : results){
            arraySpinner[i++]=ssid;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        spAvailableDevices.setAdapter(adapter);
    }

    private void devicesDiscovered(final Void devices) {
        final int[] viewIdsToShow = new int[]{R.id.device_name_tv,
                R.id.device_name_et, R.id.available_devices_tv,
                R.id.available_devices_sp, R.id.add_device_but, R.id.device_password_et, R.id.device_password_tv};
        final int[] viewIdsToHide = new int[]{R.id.loading_devices_pb,
                R.id.loading_devices_tv, R.id.wifi_enabled_tv, R.id.check_cross, R.id.check_image};

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
