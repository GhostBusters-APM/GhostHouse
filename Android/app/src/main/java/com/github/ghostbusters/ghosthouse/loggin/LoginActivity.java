package com.github.ghostbusters.ghosthouse.loggin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.home.Home;

public class LoginActivity extends AppCompatActivity {

	private static final String TAG = LoginActivity.class.getName();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);

		final Button button = (Button) this.findViewById(R.id.buttonLogIn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Log.v(LoginActivity.TAG, "Se ha pulsado el botón de LogIn");
				final Intent intent = new Intent(LoginActivity.this, Home.class);
				LoginActivity.this.startActivity(intent);
			}
		});
	}

}
