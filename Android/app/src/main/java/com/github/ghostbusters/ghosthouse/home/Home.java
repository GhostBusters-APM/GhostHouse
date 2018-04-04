package com.github.ghostbusters.ghosthouse.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.helper.view.BottomBarHelper;
import com.github.ghostbusters.ghosthouse.home.fragments.ARFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.DataFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeDeviceFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.HomeFragment;
import com.github.ghostbusters.ghosthouse.home.fragments.UserFragment;

public class Home extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(this::onNavigationBarItemSelected);
		BottomBarHelper.disableShiftMode(navigation);
		navigation.setSelectedItemId(R.id.navigation_home);

		//		Soporte para la transicion

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.home_base_frame, HomeFragment.newInstance("param1","param2"))
				.commit();

	}

	private boolean onNavigationBarItemSelected(@NonNull MenuItem item) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		boolean result = changeMainView(item, transaction);
		transaction.commit();
		return result;
	}

	private boolean changeMainView(@NonNull MenuItem item, FragmentTransaction transaction) {
		switch (item.getItemId()) {
			case R.id.navigation_ar:
				transaction.replace(R.id.home_base_frame, new ARFragment());
				return true;
			case R.id.navigation_home:
				transaction.replace(R.id.home_base_frame, new HomeDeviceFragment());
				return true;
			case R.id.navigation_data:
				transaction.replace(R.id.home_base_frame, new DataFragment());
				return true;
			case R.id.navigation_user:
				transaction.replace(R.id.home_base_frame, new UserFragment());
				return true;
		}
		return false;
	}


}
