package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.db.AppDatabase;
import com.github.ghostbusters.ghosthouse.db.Device;
import com.github.ghostbusters.ghosthouse.db.DevicePowerData;
import com.github.ghostbusters.ghosthouse.syncservice.RemoteSyncService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.val;


public class DataFragment extends Fragment implements AdapterView.OnItemSelectedListener {

	Spinner spinner;
	private static String LOCAL_TEST_USERID = "1";
	String userId;

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

		final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount
				(getContext());
		if (acct != null) {
			userId = acct.getId();
		} else {
			userId = DataFragment.LOCAL_TEST_USERID;
		}

		List<Device> devices = AppDatabase.getInstance(getContext()).deviceModel().getDevicesOfUser(userId);
		if (devices == null || devices.isEmpty()) {
			val toast = Toast.makeText(getContext(),"No existen dispositivos", Toast.LENGTH_SHORT);
			toast.show();
		}
		else{
			for (Device device : devices)
				RemoteSyncService.updateDevicePowerData(this.getContext(), userId, device.getDeviceId());
		}

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {


		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_data, container, false);

		List<Device> devices = AppDatabase.getInstance(getContext()).deviceModel().getDevicesOfUser(userId);

		if(devices!=null && !devices.isEmpty()) {
			// Spinner element
			Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

			Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();

			spinnerDrawable.setColorFilter(getResources().getColor(R.color.colorDark), PorterDuff.Mode.SRC_ATOP);

			// Spinner click listener
			spinner.setOnItemSelectedListener(this);

			// Spinner Drop down elements
			List<DeviceDropDown> list = new ArrayList<DeviceDropDown>();
			for (Device device : devices) {
				list.add(new DeviceDropDown(device.getDeviceId(), device.getName()));
			}

			// Creating adapter for spinner
			ArrayAdapter<DeviceDropDown> dataAdapter = new ArrayAdapter<DeviceDropDown>(this.getActivity(), android.R.layout.simple_spinner_item, list);

			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


			spinner.setSelection(0);

			// attaching data adapter to spinner
			spinner.setAdapter(dataAdapter);


			int a = devices.get(0).getDeviceId();
			List<DevicePowerData> powerData = AppDatabase.getInstance(getContext()).devicePowerDataModel()
					.getForDeviceIdAfterDate(devices.get(0).getDeviceId(), new Date(2017 - 1900, 6, 1));

			if (powerData == null || powerData.isEmpty()) {
				val toast = Toast.makeText(getContext(), "No existen datos para el dispositivo seleecionado", Toast.LENGTH_LONG);
				toast.show();
			} else {

				LineChart chart = (LineChart) view.findViewById(R.id.lineChart);

				List<Entry> entriesLineChart = new ArrayList<Entry>();
				List<BarEntry> barEntry = new ArrayList<BarEntry>();
				double max = 0;
				double min = 0;
				double media = 0;
				for (DevicePowerData data : powerData) {
					if (max == 0 && min == 0) {
						max = data.getValue();
						min = data.getValue();
					} else {
						if (data.getValue() > max) {
							max = data.getValue();
						}
						if (data.getValue() < min) {
							min = data.getValue();
						}
					}

					media = media + data.getValue();
					entriesLineChart.add(new Entry(data.getDate().getHours(), (float) data.getValue()));
					barEntry.add(new BarEntry(data.getDate().getHours(), (float) data.getValue()));
				}

				media = media / powerData.size();

				LineDataSet dataSet = new LineDataSet(entriesLineChart, "Consumption");
				dataSet.setColor(Color.rgb(0, 128, 96));


				LineData lineData = new LineData();
				lineData.addDataSet(dataSet);
				chart.setData(lineData);
				chart.getDescription().setText("Last ten hours consumption");
				chart.invalidate();

				BarDataSet barDataSet = new BarDataSet(barEntry, "Consumption");
				barDataSet.setColor(Color.rgb(0, 128, 96));

				BarChart barChart = (BarChart) view.findViewById(R.id.barChart);
				BarData barData = new BarData();
				barData.addDataSet(barDataSet);
				barChart.setData(barData);
				barChart.invalidate();

				TextView textViewMax = (TextView) view.findViewById(R.id.textViewMax);
				textViewMax.setText("Consumo máximo: " + String.valueOf(max) + " w");

				TextView textViewMin = (TextView) view.findViewById(R.id.textViewMin);
				textViewMin.setText("Consumo mínimo: " + String.valueOf(min) + " w");

				TextView textViewMedia = (TextView) view.findViewById(R.id.textViewMedia);
				textViewMedia.setText("Consumo medio: " + String.valueOf(media) + " w");
			}
		}
		return view;
	}


	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);


	}

	@Override
	public void onDetach() {

		super.onDetach();
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DeviceDropDown item = (DeviceDropDown) parent.getItemAtPosition(position);

		List<DevicePowerData> powerData = AppDatabase.getInstance(getContext()).devicePowerDataModel()
				.getForDeviceIdAfterDate(item.getDeviceId(),new Date(2017-1900,6,1));

		if(powerData == null || powerData.isEmpty()){
			val toast = Toast.makeText(getContext(),"No existen datos para el dispositivo "+item.getNombre(),Toast.LENGTH_LONG);
			toast.show();
		}
		else {

			LineChart chart = (LineChart) getView().findViewById(R.id.lineChart);

			List<Entry> entriesLineChart = new ArrayList<Entry>();
			List<BarEntry> barEntry = new ArrayList<BarEntry>();
			double max = 0;
			double min = 0;
			double media = 0;
			for (DevicePowerData data : powerData) {
				if (max == 0 && min == 0) {
					max = data.getValue();
					min = data.getValue();
				} else {
					if (data.getValue() > max) {
						max = data.getValue();
					}
					if (data.getValue() < min) {
						min = data.getValue();
					}
				}

				media = media + data.getValue();
				entriesLineChart.add(new Entry(data.getDate().getHours(), (float) data.getValue()));
				barEntry.add(new BarEntry(data.getDate().getHours(), (float) data.getValue()));
			}

			media = media / powerData.size();

			LineDataSet dataSet = new LineDataSet(entriesLineChart, "Consumption");
			dataSet.setColor(Color.rgb(0, 128, 96));


			LineData lineData = new LineData();
			lineData.addDataSet(dataSet);
			chart.setData(lineData);
			chart.getDescription().setText("Last ten hours consumption");
			chart.invalidate();

			BarDataSet barDataSet = new BarDataSet(barEntry, "Consumption");
			barDataSet.setColor(Color.rgb(0, 128, 96));

			BarChart barChart = (BarChart) getView().findViewById(R.id.barChart);
			BarData barData = new BarData();
			barData.addDataSet(barDataSet);
			barChart.setData(barData);
			barChart.invalidate();

			TextView textViewMax = (TextView) getView().findViewById(R.id.textViewMax);
			textViewMax.setText("Consumo máximo: " + String.valueOf(max) + " w");

			TextView textViewMin = (TextView) getView().findViewById(R.id.textViewMin);
			textViewMin.setText("Consumo mínimo: " + String.valueOf(min) + " w");

			TextView textViewMedia = (TextView) getView().findViewById(R.id.textViewMedia);
			textViewMedia.setText("Consumo medio: " + String.valueOf(media) + " w");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}