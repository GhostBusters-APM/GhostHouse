package com.github.ghostbusters.ghosthouse.wifiservice;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.ghostbusters.ghosthouse.R;


public class WifiService extends Service {
    BroadcastReceiver mReceiver;
    Boolean conectado = false;

    public WifiService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, CONNECTIVITY_SERVICE)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("WifiService")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // get an instance of the receiver in your service
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mReceiver = new WifiReceiver();
        registerReceiver(mReceiver, filter);

        Log.d("WifiService","onCreate");
        Toast.makeText(getApplicationContext(), "Servicio iniciado", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d("WifiService","onDestroy");
    }

    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("WifiReceiver","onReceive");
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected()) {
                conectado = true;
                Toast.makeText(getApplicationContext(), "Wifi: Conectado", Toast.LENGTH_LONG).show();
            } else if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && !netInfo.isConnected()) {
                Toast.makeText(getApplicationContext(), "Wifi: Desconectado", Toast.LENGTH_LONG).show();
                conectado = false;
            } else {
                if (conectado)
                    conectado = false;
                Toast.makeText(getApplicationContext(), "Wifi: Desconectado", Toast.LENGTH_LONG).show();
            }
        }
    };
}
