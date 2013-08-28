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
package eu.trentorise.smartcampus.template.social.fragments.campus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import smartcampus.android.template.standalone.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.User;
import eu.trentorise.smartcampus.template.social.custom.UserAdapter;
import eu.trentorise.smartcampus.template.social.custom.UserAdapter.UserOptionsHandler;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;

public class CampusFragmentPeople extends Fragment {

	private static final String ARG_GROUP = "ARG_GROUP";
	ArrayAdapter<User> usersListAdapter;
	List<User> usersList = new ArrayList<User>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.people, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		ImageButton search = (ImageButton) getView().findViewById(R.id.people_search_img);
		search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new LoadUserProcessor().execute(((EditText)getView().findViewById(R.id.people_search)).getText().toString());
			}
		});
		
		ListView usersListView = (ListView) getView().findViewById(R.id.people_listview);
		Set<String> initGroups = null;
		if (getArguments() != null && getArguments().containsKey(ARG_GROUP)) {
			initGroups = Collections.singleton(getArguments().getString(ARG_GROUP));
		}
		usersListAdapter = new UserAdapter(getActivity(), R.layout.user_mp, new PeopleUserOptionsHandler(), initGroups);
		usersListView.setAdapter(usersListAdapter);
		super.onStart();
	}

	private class LoadUserProcessor extends AsyncTask<String, Void, List<BasicProfile>> {

		@Override
		protected void onPostExecute(List<BasicProfile> result) {
			usersListAdapter.clear();
			if (result != null) {
				for (BasicProfile mp : result) usersListAdapter.add(CMHelper.toUser(mp));
			}
			usersListAdapter.notifyDataSetChanged();
			
			eu.trentorise.smartcampus.template.social.custom.ViewHelper.removeEmptyListView((LinearLayout)getView().findViewById(R.id.layout_people));
			if (result == null || result.isEmpty()) {
				eu.trentorise.smartcampus.template.social.custom.ViewHelper.addEmptyListView((LinearLayout)getView().findViewById(R.id.layout_people), R.string.people_list_empty);
			}
		}

		@Override
		protected List<BasicProfile> doInBackground(String... params) {
			try {
				return CMHelper.getPeople(params[0]);
			} catch (Exception e) {
				return Collections.emptyList();
			}
		}

	}
	
	private class PeopleUserOptionsHandler implements UserOptionsHandler {

		@Override
		public void assignUserToGroups(User user, Collection<Group> groups) {
			new AssignToGroups().execute(user,groups);
		}
		
	}

	private class AssignToGroups extends AsyncTask<Object, Void, User> {

		
		@Override
		protected User doInBackground(Object... params) {
			try {
				CMHelper.assignToGroups((User)params[0], (Collection<Group>)params[1]);
				return (User)params[0];
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(User result) {
			if (result != null) {
				usersListAdapter.notifyDataSetChanged();
			}
		}

	}
	
	public static Bundle prepareArgs(String socialId) {
		Bundle b = new Bundle();
		b.putString(ARG_GROUP, socialId);
		return b;
	}
}
