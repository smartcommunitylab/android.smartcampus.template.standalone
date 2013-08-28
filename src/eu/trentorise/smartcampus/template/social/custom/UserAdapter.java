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
package eu.trentorise.smartcampus.template.social.custom;

import java.util.Collection;
import java.util.Set;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.User;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;
import eu.trentorise.smartcampus.template.social.fragments.groups.MyGroupsAddToDialog;

/**
 * User list adapter. On associated '...' button click opens a dialog to 
 * add/remove the user to/from groups.
 * @author raman
 *
 */
public class UserAdapter extends ArrayAdapter<User> {

	Activity context;
	int layoutResourceId;
	private UserOptionsHandler handler;
	private Set<String> initGroups;

	public UserAdapter(Activity context, int layoutResourceId, UserOptionsHandler handler, Set<String> initGroups) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.handler = handler;
		this.initGroups = initGroups;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DataHolder holder = null;

		User user_mp = (User) getItem(position);

		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DataHolder();
			holder.user_mp_more = (Button) row.findViewById(R.id.user_mp_more);
			holder.user_mp_name = (TextView) row
					.findViewById(R.id.user_mp_name);
			holder.user_mp_surname = (TextView) row
					.findViewById(R.id.user_mp_surname);
			row.setTag(holder);
		} else {
			holder = (DataHolder) row.getTag();
		}

		holder.user_mp_more
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final User user = (User) v.getTag();
						createUserOptionsDialog(user);
					}
				});
		holder.user_mp_more.setText(R.string.user_mp_more_new);
		holder.user_mp_more.setBackgroundColor(context.getResources()
				.getColor(R.color.sc_gray));

		holder.user_mp_more.setTag(user_mp);

		holder.user_mp_name.setText(user_mp.getName()); // name
		holder.user_mp_surname.setText(user_mp.getSurname()); // surname
		return row;
	}

	static class DataHolder {
		Button user_mp_more;
		TextView user_mp_name;
		TextView user_mp_surname;
	}

	/**
	 * the callback interface upon user-groups association
	 * @author raman
	 *
	 */
	public interface UserOptionsHandler {
		void assignUserToGroups(User user, Collection<Group> groups);
	}
	
	/**
	 * Start {@link MyGroupsAddToDialog} dialog to associate user to groups.
	 * @param user
	 */
	private void createUserOptionsDialog(final User user) {
		Set<String> groups = CMHelper.getUserGroups(user);
		if (initGroups != null)  groups.addAll(initGroups);
		Dialog myGroupsDlg = new MyGroupsAddToDialog(context,new DialogHandler<Collection<Group>>() {
			@Override
			public void handleSuccess(Collection<Group> result) {
				handler.assignUserToGroups(user, result);
			}
		}, groups);
		myGroupsDlg.show();
	} 
	
}
