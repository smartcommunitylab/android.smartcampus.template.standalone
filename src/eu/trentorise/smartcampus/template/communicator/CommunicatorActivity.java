package eu.trentorise.smartcampus.template.communicator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.pushservice.PushServiceConnector;
import eu.trentorise.smartcampus.template.Constants;
import eu.trentorise.smartcampus.template.mobility.fragments.MobilityMainFragment;

public class CommunicatorActivity extends Activity {

	private SCAccessProvider mAccessProvider = new EmbeddedSCAccessProvider();
	private PushServiceConnector pushServiceConnector = new PushServiceConnector();
	private CommunicatorConnector communicatoConnector = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpContent();
		mAccessProvider = new EmbeddedSCAccessProvider();

		try {
			// communicatoConnector = new CommunicatorConnector(
			// Constants.COMMUNICATOR_SERVICE, Constants.APPID_TEST);
		} catch (Exception e1) {
			Toast.makeText(CommunicatorActivity.this,
					"Failed to connect to Communicator Service",
					Toast.LENGTH_LONG).show();
			e1.printStackTrace();
		}

		new AsyncTask<Void, Void, Object>() {
			@Override
			protected Object doInBackground(Void... params) {
				try {
					String token = mAccessProvider.readToken(
							CommunicatorActivity.this, Constants.CLIENT_ID,
							Constants.CLIENT_SECRET);

					return token;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			protected void onPostExecute(Object res) {
				if (res != null) {
					try {
						pushServiceConnector.init(getApplicationContext(),
								String.valueOf(res), Constants.APPID,
								Constants.COMMUNICATOR_SERVICE);
					} catch (CommunicatorConnectorException e) {
						Toast.makeText(CommunicatorActivity.this,
								"Failed to connect to Push Service",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(CommunicatorActivity.this,
							"Failed to connect to Push Service",
							Toast.LENGTH_LONG).show();
					finish();
				}
			};
		}.execute();

	}

	protected void setUpContent() {

	}
}
