package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ghostbusters.ghosthouse.R;

/**
 * A fragment that contain this fragment must implement the
 * {@link ARFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ARFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ARFragment extends Fragment {

	private OnFragmentInteractionListener mListener;

	public ARFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment ARFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ARFragment newInstance() {
		final ARFragment fragment = new ARFragment();
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_ar, container, false);
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
			Log.d(ARFragment.class.getName(), "AR ATTACHED");
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
