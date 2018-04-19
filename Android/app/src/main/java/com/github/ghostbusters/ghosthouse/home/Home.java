package com.github.ghostbusters.ghosthouse.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.helper.view.BottomBarHelper;
import com.github.ghostbusters.ghosthouse.home.fragments.DataFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeDeviceFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.UserFragment;

public class Home extends AppCompatActivity {

    private static final String TAG = Home.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_home);

        final BottomNavigationView navigation = (BottomNavigationView) this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this::onNavigationBarItemSelected);
        BottomBarHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_home);

        //		Soporte para la transicion

        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_base_frame, new HomeFragment())
                .commit();


//        //instancia a la BD
//        final DevicesDbHelper mDbHelper = new DevicesDbHelper(this);
//        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (!this.checkPermissions()) {
            this.requestPermissions();
        } else {
//            getLastLocation();
        }
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        final int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Home.REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        final boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(Home.TAG, "Displaying permission rationale to provide additional context.");
            this.startLocationPermissionRequest();
        } else {
            Log.i(Home.TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            this.startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        Log.i(Home.TAG, "onRequestPermissionResult");
        if (requestCode == Home.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(Home.TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
//                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
            }
        }
    }

    private boolean onNavigationBarItemSelected(@NonNull final MenuItem item) {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final boolean result = this.changeMainView(item, transaction);
        transaction.commit();
        return result;
    }

    private boolean changeMainView(@NonNull final MenuItem item, final FragmentTransaction transaction) {
        switch (item.getItemId()) {
            case R.id.navigation_ar:
                final Intent intent = new Intent(this.getApplicationContext(), ArActivityWrapper.class);
                this.startActivity(intent);
                return true;
            case R.id.navigation_home:
                transaction.replace(R.id.home_base_frame, new HomeDeviceFragment());
                return true;
            case R.id.navigation_data:
                transaction.replace(R.id.home_base_frame, new DataFragment());
                return true;
            case R.id.navigation_user:
                transaction.replace(R.id.home_base_frame, new UserFragment());
                return true;
        }
        return false;
    }


}
