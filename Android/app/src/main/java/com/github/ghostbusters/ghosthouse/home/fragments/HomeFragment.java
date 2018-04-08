package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.newdevice.NewDevice;

import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 */
public class HomeFragment extends Fragment {
	public static final String TAG = HomeFragment.class.getSimpleName();

	private static final int ADD_DEVICE_REQUEST_CODE = 1;


	public HomeFragment() {
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
		final View view = inflater.inflate(R.layout.fragment_home, container, false);

		return view;
	}


	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		final ImageButton imageView = (ImageButton) view.findViewById(R.id.fragment_a_imageButton);
		final ImageButton add = (ImageButton) view.findViewById(R.id.addImage);

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final DeviceDetailsFragment simpleFragmentB = new DeviceDetailsFragment();
				HomeFragment.this.getActivity().getSupportFragmentManager()
						.beginTransaction()
						.addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
						.addToBackStack(HomeFragment.TAG)
						.replace(R.id.home_base_frame, simpleFragmentB)
						.commit();
			}
		});

		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Log.d(HomeFragment.TAG, "Add new device");
				final Intent intent = new Intent(HomeFragment.this.getActivity(), NewDevice.class);
				HomeFragment.this.startActivityForResult(intent, HomeFragment.ADD_DEVICE_REQUEST_CODE);
			}
		});


	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
		switch (requestCode) {
			case HomeFragment.ADD_DEVICE_REQUEST_CODE:
				switch (resultCode) {
					case RESULT_OK:
						final int newDeviceId = resultData.getIntExtra(NewDevice.DEVICE_ID_RESULT, -1);
						Log.d(HomeFragment.TAG, String.format(Locale.ENGLISH, "new device added with id: %d",
								newDeviceId));
						break;
					case RESULT_CANCELED:
						Log.d(HomeFragment.TAG, "adding new device cancelled of failed");
						break;
					default:
						Log.d(HomeFragment.TAG, "unknown result code for new device request");
						break;
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, resultData);
		}
	}

}
