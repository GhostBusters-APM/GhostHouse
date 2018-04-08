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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {

	private OnFragmentInteractionListener mListener;

	public DataFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment DataFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static DataFragment newInstance() {
		final DataFragment fragment = new DataFragment();
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getArguments() != null) {
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_data, container, false);

//		super.onCreate(savedInstanceState);
//		view.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_main);

		final GraphView line_graph = (GraphView) view.findViewById(R.id.graph);
		final LineGraphSeries<DataPoint> line_series =
				new LineGraphSeries<DataPoint>(new DataPoint[]{
						new DataPoint(0, 100),
						new DataPoint(1, 500),
						new DataPoint(2, 300),
						new DataPoint(3, 200),
						new DataPoint(4, 600)
				});

		final StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);
		staticLabelsFormatter.setHorizontalLabels(new String[]{"Jan", "Feb", "March", "April", "May"});
		line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

		line_graph.addSeries(line_series);
//		line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
//			@Override
//			public void onTap(Series series, DataPointInterface dataPoint) {
//				Toast.makeText(HomeFragment.this, "Series: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
//			}
//		});
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
			Log.d(DataFragment.class.getName(), "DATA ATTACHED");
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
