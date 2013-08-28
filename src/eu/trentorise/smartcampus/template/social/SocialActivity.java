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
package eu.trentorise.smartcampus.template.social;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;
import eu.trentorise.smartcampus.template.social.fragments.MainFragment;

public class SocialActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize the access provider
		CMHelper.init(getApplicationContext());
		new LoadTask().execute();
		setUpContent();
	}

	protected void setUpContent() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment frag = null;
		frag = new MainFragment();
		ft.replace(android.R.id.content, frag).commitAllowingStateLoss();

	}
	
	protected class LoadTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				CMHelper.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

}
