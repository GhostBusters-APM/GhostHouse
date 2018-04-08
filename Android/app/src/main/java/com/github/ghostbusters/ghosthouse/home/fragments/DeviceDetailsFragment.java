package com.github.ghostbusters.ghosthouse.home.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ghostbusters.ghosthouse.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class DeviceDetailsFragment extends Fragment {


	public DeviceDetailsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			this.setSharedElementEnterTransition(
					TransitionInflater.
							from(this.getContext()).
							inflateTransition(android.R.transition.move));
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_device_details, container, false);
	}
}
