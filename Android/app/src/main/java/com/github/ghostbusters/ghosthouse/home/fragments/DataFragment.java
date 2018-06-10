package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.ghostbusters.ghosthouse.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {

	SwipeRefreshLayout mSwipeRefreshLayout;
	HttpRequestTask req;

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


	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_data, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setOnRefreshListener(this::refresh);

		return view;
	}

	private void refresh(){
		Log.d("Funciona","Funciona");
		req = (HttpRequestTask) new HttpRequestTask(mSwipeRefreshLayout).execute();
	}



	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		req = (HttpRequestTask) new HttpRequestTask(null).execute();

	}
	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		Log.d(DataFragment.class.getName(), "DATA ATTACHED");

	}

	@Override
	public void onDetach() {
		Log.d(DataFragment.class.getName(), "DATA DETACHED");
		req.cancel(true);
		super.onDetach();
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

	private class HttpRequestTask extends AsyncTask<Void, Void, List<HourDto>> {


		private SwipeRefreshLayout mSwipeRefreshLayout;

		public HttpRequestTask(SwipeRefreshLayout mSwipeRefreshLayout) {
			this.mSwipeRefreshLayout = mSwipeRefreshLayout;
		}

		@Override
		protected List<HourDto> doInBackground(Void... params) {
			try {
				Properties properties = new Properties();;
				AssetManager assetManager = getActivity().getApplicationContext().getAssets();
				InputStream inputStream = assetManager.open("config.properties");
				properties.load(inputStream);

				final String url = properties.getProperty("url");
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				ResponseEntity<DataDto> responseEntity = restTemplate.getForEntity(url, DataDto.class);
				DataDto objects = responseEntity.getBody();
				return objects.getData();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<HourDto> list) {
			if (list == null){

				Toast connectionError =
						Toast.makeText(getActivity().getApplicationContext(),
								"Error de conexión con servicio", Toast.LENGTH_SHORT);

				connectionError.show();

			}
			else {
				if (getView() == null) {
					return;
				}
				final GraphView line_graph = (GraphView) DataFragment.this.getView().findViewById(R.id.graph);

				List<DataPoint> dataPointsList = new ArrayList<>();
				for (HourDto hour : list) {
					dataPointsList.add(new DataPoint(hour.getHour(), hour.getPower()));
				}
				DataPoint[] dataPoints = new DataPoint[dataPointsList.size()];

				final LineGraphSeries<DataPoint> line_series =
						new LineGraphSeries<DataPoint>(dataPointsList.toArray(dataPoints));

				final StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);

				line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
				line_graph.removeAllSeries();

				line_graph.addSeries(line_series);

			}

			if (this.mSwipeRefreshLayout != null) {
				this.mSwipeRefreshLayout.setRefreshing(false);
			}

		}

	}


}