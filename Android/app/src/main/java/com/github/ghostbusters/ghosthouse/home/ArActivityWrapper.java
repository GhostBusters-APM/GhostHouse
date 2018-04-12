package com.github.ghostbusters.ghosthouse.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.ghostbusters.ghosthouse.unity.UnityPlayerActivity;

public class ArActivityWrapper extends AppCompatActivity {

	@Override
	/**
	 * Actividad creada porque al cerrar UnityPlayerActivity tambien se cierra la actividad que la abre
	 */
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = new Intent(this.getApplicationContext(), UnityPlayerActivity.class);
		this.startActivity(intent);
	}
}
