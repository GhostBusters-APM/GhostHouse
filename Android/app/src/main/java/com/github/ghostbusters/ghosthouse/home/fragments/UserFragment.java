package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.github.ghostbusters.ghosthouse.R;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private static final String TAG = "UserFragment";
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	public UserFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment UserFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static UserFragment newInstance(String param1, String param2) {
		UserFragment fragment = new UserFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_user, container, false);

		Button button = (Button) view.findViewById(R.id.buttonLogOut);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "Se ha pulsado el botón de Logout");
			}
		});

		Switch switch1 = (Switch) view.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(TAG, "Switch activado");
 				} else {
					// The toggle is disabled
 					Log.v(TAG, "Switch desactivado");
				}
			}
		});

		CheckBox checkbox1 = (CheckBox) view.findViewById(R.id.checkBox1);
		checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(TAG, "Checkbox1 activado");
				} else {
					// The toggle is disabled
					Log.v(TAG, "Checkbox1 desactivado");
				}
			}
		});

		CheckBox checkbox2 = (CheckBox) view.findViewById(R.id.checkBox2);
		checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(TAG, "Checkbox2 activado");
				} else {
					// The toggle is disabled
					Log.v(TAG, "Checkbox2 desactivado");
				}
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			Log.d(UserFragment.class.getName(), "USER ATTACHED");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}



}
