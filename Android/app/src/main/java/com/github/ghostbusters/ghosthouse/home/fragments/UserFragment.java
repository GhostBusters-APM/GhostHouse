package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.loggin.LoginActivity;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 */
public class UserFragment extends Fragment {
	private static final String TAG = "UserFragment";

	public UserFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_user, container, false);

		final Button button = (Button) view.findViewById(R.id.buttonLogOut);
		button.setOnClickListener(v -> {
			Log.v(UserFragment.TAG, "Se ha pulsado el botón de Logout");
			final Intent intent = new Intent(UserFragment.this.getActivity(), LoginActivity.class);
			UserFragment.this.startActivity(intent);
		});

		final Switch switch1 = (Switch) view.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				// The toggle is enabled
				Log.v(UserFragment.TAG, "Switch activado");
			} else {
				// The toggle is disabled
				Log.v(UserFragment.TAG, "Switch desactivado");
			}
		});

		final CheckBox checkbox1 = (CheckBox) view.findViewById(R.id.checkBox1);
		checkbox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				// The toggle is enabled
				Log.v(UserFragment.TAG, "Checkbox1 activado");
			} else {
				// The toggle is disabled
				Log.v(UserFragment.TAG, "Checkbox1 desactivado");
			}
		});

		final CheckBox checkbox2 = (CheckBox) view.findViewById(R.id.checkBox2);
		checkbox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				// The toggle is enabled
				Log.v(UserFragment.TAG, "Checkbox2 activado");
			} else {
				// The toggle is disabled
				Log.v(UserFragment.TAG, "Checkbox2 desactivado");
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		Log.d(UserFragment.class.getName(), "USER ATTACHED");
	}

}
