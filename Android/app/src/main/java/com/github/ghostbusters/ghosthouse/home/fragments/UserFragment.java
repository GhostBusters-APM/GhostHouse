package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.loggin.LoginActivity;

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
	public static UserFragment newInstance(final String param1, final String param2) {
		final UserFragment fragment = new UserFragment();
		final Bundle args = new Bundle();
		args.putString(UserFragment.ARG_PARAM1, param1);
		args.putString(UserFragment.ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getArguments() != null) {
			this.mParam1 = this.getArguments().getString(UserFragment.ARG_PARAM1);
			this.mParam2 = this.getArguments().getString(UserFragment.ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_user, container, false);

		final Button button = (Button) view.findViewById(R.id.buttonLogOut);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Log.v(UserFragment.TAG, "Se ha pulsado el botón de Logout");
				final Intent intent = new Intent(UserFragment.this.getActivity(), LoginActivity.class);
				UserFragment.this.startActivity(intent);
			}
		});

		final Switch switch1 = (Switch) view.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(UserFragment.TAG, "Switch activado");
				} else {
					// The toggle is disabled
					Log.v(UserFragment.TAG, "Switch desactivado");
				}
			}
		});

		final CheckBox checkbox1 = (CheckBox) view.findViewById(R.id.checkBox1);
		checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(UserFragment.TAG, "Checkbox1 activado");
				} else {
					// The toggle is disabled
					Log.v(UserFragment.TAG, "Checkbox1 desactivado");
				}
			}
		});

		final CheckBox checkbox2 = (CheckBox) view.findViewById(R.id.checkBox2);
		checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.v(UserFragment.TAG, "Checkbox2 activado");
				} else {
					// The toggle is disabled
					Log.v(UserFragment.TAG, "Checkbox2 desactivado");
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
	public void onButtonPressed(final Uri uri) {
		if (this.mListener != null) {
			this.mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			this.mListener = (OnFragmentInteractionListener) context;
		} else {
			Log.d(UserFragment.class.getName(), "USER ATTACHED");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.mListener = null;
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
