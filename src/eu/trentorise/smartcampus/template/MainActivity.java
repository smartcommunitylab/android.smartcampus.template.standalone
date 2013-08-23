/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.template;

import smartcampus.android.template.standalone.R;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.profileservice.BasicProfileService;
import eu.trentorise.smartcampus.profileservice.ProfileServiceException;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;

/**
 * Sample Android activity. Demonstrates also the use of authentication
 * mechanism and of the libraries.
 * 
 * @author raman
 * 
 */
public class MainActivity extends Activity {

	/** Logging tag */
	private static final String TAG = "Main";

	private static final String AC_SERVICE_ADDR = "https://vas-dev.smartcampuslab.it/acService";
	private static final String PROFILE_SERVICE_ADDR = "https://vas-dev.smartcampuslab.it";

	/**
	 * Provides access to the authentication mechanism. Used to retrieve the
	 * token
	 */
	private SCAccessProvider mAccessProvider = null;
	/** Access token for the application user */
	private String mToken = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// try {
		// Constants.setAuthUrl(getApplicationContext(), AUTH_URL);
		// } catch (NameNotFoundException e1) {
		// Log.e(TAG, "problems with configuration.");
		// finish();
		// }

		findViewById(R.id.file_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								FileActivity.class);
						MainActivity.this.startActivity(intent);
					}
				});

		// Initialize the access provider
		mAccessProvider = new EmbeddedSCAccessProvider();

		try {
			if (!mAccessProvider.login(this, Constants.CLIENT_ID,
					Constants.CLIENT_SECRET, null)) {
				// user is already registered. Proceed requesting the token
				// and the related steps if needed
				new LoadUserDataFromProfileServiceTask().execute();
				Log.i(TAG, "Already authenticated");

			}
		} catch (AACException e) {
			Log.e(TAG, "Failed to login: " + e.getMessage());
			// handle the failure, e.g., notify the user and close the app.
		}
		// if the authentication is necessary, use the provided operations to
		// retrieve the token: no restriction on the preferred account type
		// try {
		// mToken = mAccessProvider.getAuthToken(this, null);
		// if (mToken != null) {
		// // read user data
		// UserData data = mAccessProvider.readUserData(this, null);
		// showUserIdFromAccountData(data);
		// // access the user data from the AC service remotely
		// new LoadUserDataFromACServiceTask().execute(mToken);
		// // access the basic user profile data remotely
		// new LoadUserDataFromProfileServiceTask().execute(mToken);
		// }
		// } catch (OperationCanceledException e) {
		// Log.e(TAG, "Login cancelled.");
		// finish();
		// } catch (AuthenticatorException e) {
		// Log.e(TAG, "Login failed: " + e.getMessage());
		// finish();
		// } catch (IOException e) {
		// Log.e(TAG, "Login ended with error: " + e.getMessage());
		// finish();
		// }
	}

	private void showUserIdFromAccountData(BasicProfile profile) {
		TextView tv = (TextView) findViewById(R.id.user_id);
		if (profile != null) {
			tv.setText(profile.getUserId());
		} else {
			tv.setText("UNDEFINED!");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// check the result of the authentication
		if (requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
			// authentication successful
			if (resultCode == RESULT_OK) {
				// read the user token
				// try {
				// mToken = mAccessProvider.readToken(MainActivity.this,
				// Constants.CLIENT_ID, Constants.CLIENT_SECRET);
				// } catch (AACException e) {
				// Log.e(TAG, "Error retrieving user token", e);
				// }
				// mToken = data.getExtras().getString(
				// AccountManager.KEY_AUTHTOKEN);
				Log.i(TAG, "Authentication successfull");

				// showUserIdFromAccountData(bp);
				// new LoadUserDataFromACServiceTask().execute(mToken);
				new LoadUserDataFromProfileServiceTask().execute();
				// authentication cancelled by user
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
				Log.i(TAG, "Authentication cancelled");
				// authentication failed
			} else {
				String error = data.getExtras().getString(
						AccountManager.KEY_AUTH_FAILED_MESSAGE);
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
				Log.i(TAG, "Authentication failed: " + error);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// protected class LoadUserDataFromACServiceTask extends
	// AsyncTask<String, Void, UserData> {
	//
	// @Override
	// protected UserData doInBackground(String... params) {
	// try {
	// return new ACService(AC_SERVICE_ADDR).getUserByToken(params[0]);
	// } catch (SecurityException e) {
	// Log.e(TAG, "Security Exception: " + e.getMessage());
	// } catch (AcServiceException e) {
	// Log.e(TAG, "Service Exception: " + e.getMessage());
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(UserData data) {
	// TextView attrs = (TextView) findViewById(R.id.attributes);
	// if (data != null) {
	// StringBuilder builder = new StringBuilder();
	// if (data.getAttributes() != null) {
	// for (int i = 0; i < data.getAttributes().size(); i++) {
	// Attribute a = data.getAttributes().get(i);
	// builder.append(a.getKey());
	// builder.append('=');
	// builder.append(a.getValue());
	// builder.append('\n');
	// }
	// attrs.setText(builder.toString());
	// } else {
	// attrs.setText("-");
	// }
	// } else {
	// attrs.setText("UNDEFINED!");
	// }
	// showUserIdFromAccountData(data);
	// }
	// }

	protected class LoadUserDataFromProfileServiceTask extends
			AsyncTask<String, Void, BasicProfile> {

		@Override
		protected BasicProfile doInBackground(String... params) {
			try {
				String mToken = mAccessProvider.readToken(MainActivity.this,
						Constants.CLIENT_ID, Constants.CLIENT_SECRET);
				BasicProfileService service = new BasicProfileService(
						Constants.AUTH_URL);
				return service.getBasicProfile(mToken);
			} catch (SecurityException e) {
				Log.e(TAG, "Security Exception: " + e.getMessage());
			} catch (ProfileServiceException e) {
				Log.e(TAG, "Profile Service Exception: " + e.getMessage());
			} catch (AACException e) {
				Log.e(TAG, "AAC Exception: " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(BasicProfile data) {
			showUserIdFromAccountData(data);
			TextView name = (TextView) findViewById(R.id.name);
			if (data != null) {
				name.setText(data.getName() + " " + data.getSurname());
			} else {
				name.setText("UNDEFINED!");
			}
		}
	}

}
