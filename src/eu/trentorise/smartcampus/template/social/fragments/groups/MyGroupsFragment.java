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
package eu.trentorise.smartcampus.template.social.fragments.groups;

import java.util.Collection;

import smartcampus.android.template.standalone.R;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.User;
import eu.trentorise.smartcampus.template.social.Constants;
import eu.trentorise.smartcampus.template.social.custom.DialogHandler;
import eu.trentorise.smartcampus.template.social.custom.UserAdapter;
import eu.trentorise.smartcampus.template.social.custom.UserAdapter.UserOptionsHandler;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;
import eu.trentorise.smartcampus.template.social.fragments.campus.CampusFragmentPeople;

/**
 * User groups viewer. Spinner to select the group plus the list of the users in the group.
 * @author raman
 *
 */
public class MyGroupsFragment extends Fragment {

	private ArrayAdapter<User> usersListAdapter;
	private Spinner myGroupsSpinner;

	private ArrayAdapter<String> dataAdapter;
	
	private Group selected = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.users, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myGroupsSpinner = (Spinner) getView().findViewById(R.id.user_spinner_mygroups);
		myGroupsSpinner.setAdapter(dataAdapter);
		
		ListView usersListView = (ListView) getView().findViewById(R.id.users_listview);
		usersListAdapter = new UserAdapter(getActivity(), R.layout.user_mp, new MyGroupsUserOptionsHandler(), null);
		usersListView.setAdapter(usersListAdapter);

		update(CMHelper.getGroups());

		myGroupsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selected = null; 
			    try {
					selected = CMHelper.getGroups().get(position);
				} catch (Exception e) {
					e.printStackTrace();
				}
				updateUserList(selected);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.gripmenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		SubMenu submenu = menu.getItem(0).getSubMenu();
		submenu.clear();		 
		submenu.add(Menu.CATEGORY_SYSTEM, R.id.mygroups_add, Menu.NONE, R.string.mygroups_add_title);
		if (selected == null && CMHelper.getGroups() != null && CMHelper.getGroups().size() > 0) selected = CMHelper.getGroups().get(0);
		if (selected != null && !selected.getName().equals(Constants.MY_PEOPLE_GROUP_NAME)) {
			submenu.add(Menu.CATEGORY_SYSTEM, R.id.mygroups_rename, 3, R.string.mygroups_rename_title);
			submenu.add(Menu.CATEGORY_SYSTEM, R.id.mygroups_delete, 2, R.string.mygroups_delete_title);
		}
		submenu.add(Menu.CATEGORY_SYSTEM, R.id.mygroups_add_person, Menu.NONE, R.string.mygroups_add_person_title);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mygroups_add:
			Dialog dialog = new MyGroupsAddDialog(getActivity(), new DialogHandler<String>() {
				@Override
				public void handleSuccess(String result) {
					new SaveGroupProcessor().execute(result);
				}
			}, 
			null);
			dialog.setTitle(R.string.mygroups_add_title);
			dialog.show();
			return true;
		case R.id.mygroups_add_person:
			FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
			Fragment fragment = new CampusFragmentPeople();
			// Replacing old fragment with new one
			ft.replace(android.R.id.content, fragment);
			fragment.setArguments(CampusFragmentPeople.prepareArgs(selected.getSocialId()));
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

			return true;
		case R.id.mygroups_delete:
			new DeleteGroupProcessor().execute(selected);
			return true;
		case R.id.mygroups_rename:
			dialog = new MyGroupsAddDialog(getActivity(), new DialogHandler<String>() {
				@Override
				public void handleSuccess(String result) {
					new SaveGroupProcessor(selected).execute(result);
				}
			}, 
			selected);
			dialog.setTitle(R.string.mygroups_rename_title);
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class SaveGroupProcessor extends AsyncTask<String, Void, Collection<Group>> {

		private Group group = new Group();

		public SaveGroupProcessor() {
			super();
		}

		public SaveGroupProcessor(Group group) {
			this.group = group;
		}

		@Override
		protected Collection<Group> doInBackground(String... params) {
			try {
				Group newGroup = new Group();
				newGroup.setSocialId(group.getSocialId());
				newGroup.setName(params[0]);
				newGroup.setUsers(group.getUsers());
				
				CMHelper.saveGroup(newGroup);
				group.setName(newGroup.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CMHelper.getGroups();
		}

		@Override
		protected void onPostExecute(Collection<Group> result) {
			update(result);
		}
	}

	public class DeleteGroupProcessor extends AsyncTask<Group, Void, Collection<Group>> {

		@Override
		protected Collection<Group> doInBackground(Group... params) {
			try {
				CMHelper.deleteGroup(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CMHelper.getGroups();
		}
		@Override
		protected void onPostExecute(Collection<Group> result) {
			selected = null;
			update(result);
		}
	}

	
	private void update(Collection<Group> result) {
		//Group selected = result == null || result.isEmpty() ? null : result.iterator().next();
		
		dataAdapter.clear(); 

		for (Group temp : CMHelper.getGroups()) {
			dataAdapter.add(temp.getName());
		}
		dataAdapter.notifyDataSetChanged();

		updateUserList(selected);
	}


	private void updateUserList(Group selected) {
		if (selected == null) {
			myGroupsSpinner.setSelection(0);
			if (CMHelper.getGroups().size() > 0) {
				selected = CMHelper.getGroups().get(0);
			}
		}
		usersListAdapter.clear();
		if (selected != null && selected.getUsers() != null) {
			for (User user : selected.getUsers()) {
				usersListAdapter.add(user);
			}
			usersListAdapter.notifyDataSetChanged();
		}
		getActivity().invalidateOptionsMenu();
	}

	private class MyGroupsUserOptionsHandler implements UserOptionsHandler {

		@Override
		public void assignUserToGroups(User user, Collection<Group> groups) {
			new AssignToGroups().execute(user, groups);
		}
	} 
	
	
	private class AssignToGroups extends AsyncTask<Object, Void, User> {

		@Override
		protected User doInBackground(Object... params) {
			try {
				CMHelper.assignToGroups((User)params[0], (Collection<Group>)params[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (User)params[0];
		}

		@Override
		protected void onPostExecute(User result) {
			Group group = CMHelper.getGroups().get(myGroupsSpinner.getSelectedItemPosition());
			updateUserList(group);
		}
		
	}

}
