package eu.trentorise.smartcampus.template.territory;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import eu.trentorise.smartcampus.template.territory.fragment.map.MainFragment;

public class TerritoryActivity extends Activity {
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tag", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpContent();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void setUpContent() {
		setContentView(R.layout.homelayout);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		MainFragment fragment = new MainFragment();
		fragmentTransaction.add(android.R.id.content, fragment);
		fragmentTransaction.commit();

	}

}
