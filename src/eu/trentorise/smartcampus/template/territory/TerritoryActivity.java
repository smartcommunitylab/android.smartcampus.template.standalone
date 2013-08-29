package eu.trentorise.smartcampus.template.territory;

import smartcampus.android.template.standalone.R;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.template.custom.LocationHelper;
import eu.trentorise.smartcampus.template.territory.fragment.MainFragment;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;
import eu.trentorise.smartcampus.territoryservice.model.POIObject;
import eu.trentorise.smartcampus.territoryservice.model.StoryObject;


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

//		initDataManagement(savedInstanceState);



	}



	

	

	private void setUpContent() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//		setSupportProgressBarIndeterminateVisibility(false);

		setContentView(R.layout.homelayout);
		 FragmentManager fragmentManager = getFragmentManager();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        MainFragment fragment = new MainFragment();
	        fragmentTransaction.add(R.id.mainlayout, fragment);
	        fragmentTransaction.commit();


	}





	


	
	
	


}
