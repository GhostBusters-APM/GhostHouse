package com.github.ghostbusters.ghosthouse.loggin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.home.Home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

//        final int check = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (check == ConnectionResult.SUCCESS) {
//            Log.i(LoginActivity.TAG, "Google Play Services SUCCESS");
//        } else if (check == ConnectionResult.SERVICE_MISSING) {
//            Log.i(LoginActivity.TAG, "Google Play Services SERVICE_MISSING");
//        } else if (check == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
//            Log.i(LoginActivity.TAG, "Google Play Services SERVICE_VERSION_UPDATE_REQUIRED");
//        } else if (check == ConnectionResult.SERVICE_DISABLED) {
//            Log.i(LoginActivity.TAG, "Google Play Services SERVICE_DISABLED");
//        } else if (check == ConnectionResult.SERVICE_INVALID) {
//            Log.i(LoginActivity.TAG, "Google Play Services SERVICE_INVALID");
//        } else {
//            Log.i(LoginActivity.TAG, "Google Play Services returned an unknown value");
//        }

        final Button login = this.findViewById(R.id.buttonLogIn);
        this.findViewById(R.id.buttonLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.v(LoginActivity.TAG, "Se ha pulsado el botón de LogIn");
                final Intent intent = new Intent(LoginActivity.this, Home.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        // Set the dimensions of the sign-in button.
        final SignInButton signInButton = this.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        this.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            private void signIn() {
                //abre el modal de seleccion de cuenta
                final Intent signInIntent = LoginActivity.this.mGoogleSignInClient.getSignInIntent();
                LoginActivity.this.startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN);
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        //empieza el flujo del login
                        this.signIn();
                        break;
                    // ...
                }
            }
        });

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        this.updateUI(account);
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LoginActivity.TAG, "Request: " + requestCode);
        Log.d(LoginActivity.TAG, "Result: " + resultCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == LoginActivity.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.handleSignInResult(task);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(final Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            this.updateUI(account);
        } catch (final ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LoginActivity.TAG, "signInResult:failed code=" + e.getStatusCode());
            this.updateUI(null);
        }
    }
    // [END handleSignInResult]

    private void updateUI(@Nullable final GoogleSignInAccount account) {
        if (account != null) {
            Log.v(LoginActivity.TAG, "Se ha pulsado el botón de LogIn");
            final Intent intent = new Intent(LoginActivity.this, Home.class);
            LoginActivity.this.startActivity(intent);
        } else {
            this.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

}
