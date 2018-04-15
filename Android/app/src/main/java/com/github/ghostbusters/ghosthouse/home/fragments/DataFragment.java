package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ghostbusters.ghosthouse.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		return view;
	}


	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		new HttpRequestTask().execute();

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

	private class HttpRequestTask extends AsyncTask<Void, Void, ArrayList<HashMap>> {
		@Override
		protected ArrayList<HashMap> doInBackground(Void... params) {
			try {
				final String url = "http://192.168.0.166:8080/historic";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				ObjectMapper oMapper = new ObjectMapper();
				ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(url, Object[].class);
				Object[] objects = responseEntity.getBody();
				ArrayList<HashMap> list = new ArrayList();

				for (int i = 0; i < objects.length; i++){
					HashMap<String, Object> map = new HashMap<String, Object>();
					map = oMapper.convertValue(objects[i], HashMap.class);
					list.add(map);
				}


				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@RequiresApi(api = Build.VERSION_CODES.N)
		@Override
		protected void onPostExecute(ArrayList<HashMap> list) {
			final GraphView line_graph = (GraphView) DataFragment.this.getView().findViewById(R.id.graph);

			List<DataPoint> dataPointsList = new ArrayList<DataPoint>();
			for (int i = 0; i < list.size()-1; i++){
				String hour = list.get(i).get("hour").toString();
				String power = list.get(i).get("power").toString();

				DataPoint dataPoint = new DataPoint(Integer.parseInt(hour), Integer.parseInt(power));

				dataPointsList.add(dataPoint);
			}

			DataPoint[] dataPoints = new DataPoint[dataPointsList.size()];
			dataPoints = dataPointsList.toArray(dataPoints);

			final LineGraphSeries<DataPoint> line_series =
					new LineGraphSeries<DataPoint>(dataPoints);

			final StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);


			staticLabelsFormatter.setHorizontalLabels(new String[]{"0", "1", "2", "3", "4", "5","6","7",
					"8","9","10","11","12","13","14","15", "16","17","18","19","20","21","22",
					"23"});
			line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

			line_graph.addSeries(line_series);

	}

}


}