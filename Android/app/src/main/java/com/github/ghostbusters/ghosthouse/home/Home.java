package com.github.ghostbusters.ghosthouse.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.helper.view.BottomBarHelper;
import com.github.ghostbusters.ghosthouse.home.fragments.DataFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeDeviceFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.UserFragment;
import com.github.ghostbusters.ghosthouse.loggin.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Home extends AppCompatActivity implements UserFragment.OnFragmentInteractionListener{

    private static final String TAG = Home.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final String PREFS_NAME = "GhostPrefs";
    public static final String KEY_NAME = "nightMode";
    static final String FROM_USERFRAGMENT = "fromUserFragment";
    private static final String EULA = "eula";
    private boolean fromUserFragment;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // Restore preferences
        PackageInfo versionInfo = getPackageInfo();


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isNightMode = settings.getBoolean(KEY_NAME, false);
        boolean mapLicenseAccept = settings.getBoolean(EULA+ versionInfo.versionCode, false);
        if(isNightMode){
//            setTheme(R.style.AppDarkTheme);
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        Log.d(TAG,String.format("¿Night Mode? %s", String.valueOf(isNightMode)));
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance

        this.setContentView(R.layout.activity_home);


        // Check if EULA should be displayed.
        this.setupLicense(mapLicenseAccept,versionInfo, settings);




        final BottomNavigationView navigation = (BottomNavigationView) this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this::onNavigationBarItemSelected);
        BottomBarHelper.disableShiftMode(navigation);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            fromUserFragment = savedInstanceState.getBoolean(FROM_USERFRAGMENT);
            if(fromUserFragment){
                navigation.setSelectedItemId(R.id.navigation_user);
            }
            else{
                navigation.setSelectedItemId(R.id.navigation_home);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.home_base_frame, new HomeFragment())
                        .commit();
            }
        } else {
            // Probably initialize members with default values for a new instance
            navigation.setSelectedItemId(R.id.navigation_home);
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.home_base_frame, new HomeFragment())
                    .commit();
        }



        //		Soporte para la transicion




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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean(FROM_USERFRAGMENT, fromUserFragment);
        

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public int setGhostTheme(int t) {
        Log.d(TAG,String.format("¿Theme? %s", String.valueOf(t)));
        AppCompatDelegate.setDefaultNightMode(t);
        setTheme(t);
        fromUserFragment = true;
        recreate();
//        final FragmentManager fragmentManager = this.getSupportFragmentManager();
//        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.home_base_frame, new UserFragment());
//        transaction.commit();
//        recreate();


        return t;
    }

    private void setupLicense(final boolean mapLicenseAccept, PackageInfo versionInfo, SharedPreferences settings) {
        Log.d(TAG,"Setup Licensce");
        // EULA title
        String title = getString(R.string.app_name) +" "+ versionInfo.versionName;

        // EULA text
        String message = getString(R.string.eula_string);

        if (!mapLicenseAccept) {
            Log.d(TAG,"no la he aceptado nunca");
            //user does not accept the license yet, we will open the dialog
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(EULA+versionInfo.versionCode, true);
                            editor.commit();
                            final boolean isNightMode = settings.getBoolean(KEY_NAME, false);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            signOut();
                        }
                    });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private PackageInfo getPackageInfo() {
        PackageInfo info = null;

        try {
            info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    private void signOut() {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(),gso);
                mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
