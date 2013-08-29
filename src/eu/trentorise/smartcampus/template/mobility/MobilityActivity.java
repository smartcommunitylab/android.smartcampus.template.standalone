package eu.trentorise.smartcampus.template.mobility;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import eu.trentorise.smartcampus.template.mobility.fragments.MobilityMainFragment;

public class MobilityActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpContent();
	}

	protected void setUpContent() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment frag = null;
		frag = new MobilityMainFragment();
		ft.replace(android.R.id.content, frag).commitAllowingStateLoss();
	}
}
