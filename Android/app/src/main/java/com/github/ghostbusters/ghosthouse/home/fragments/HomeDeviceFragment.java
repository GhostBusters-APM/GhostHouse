package com.github.ghostbusters.ghosthouse.home.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ghostbusters.ghosthouse.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeDeviceFragment extends Fragment {

	public HomeDeviceFragment() {
		// Required empty public constructor
	}

	public static HomeDeviceFragment newInstance(final String param1, final String param2) {
		final HomeDeviceFragment fragment = new HomeDeviceFragment();
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.main_layout, container, false);
		return view;
	}

}
