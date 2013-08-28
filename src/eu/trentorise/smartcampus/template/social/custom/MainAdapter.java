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

import smartcampus.android.template.standalone.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.template.social.fragments.campus.CampusFragmentPeople;
import eu.trentorise.smartcampus.template.social.fragments.groups.MyGroupsFragment;

/**
 * Adapter for landing page for social demo fragment
 * @author raman
 *
 */
public class MainAdapter extends BaseAdapter {
	private Context context;
	private FragmentManager fragmentManager;

	public MainAdapter(Context c) {
		this.context = c;
	}

	public MainAdapter(Context applicationContext,
			FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		this.context = applicationContext;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			holder.text = new TextView(context);
		} else{
			holder.text = (TextView) convertView;
		}
		prepareHolder(position, holder);
		return holder.text;
		
		
	}

	private void prepareHolder(final int position, ViewHolder holder) {
		holder.text.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		holder.text.setCompoundDrawablesWithIntrinsicBounds(null, context
				.getResources().getDrawable(ACTIONS[position].thumbnail),
				null, null);
		holder.text.setText(ACTIONS[position].description);
		holder.text.setTextColor(context.getResources().getColor(
				R.color.sc_dark_gray));
		holder.text.setGravity(Gravity.CENTER);
		holder.text.setOnClickListener(new CategoriesOnClickListener(position));
	}

	static class ViewHolder{
		TextView text;
	}
	
	public class CategoriesOnClickListener implements OnClickListener {
		int position;
		
		public CategoriesOnClickListener(int position) {
			this.position=position;
	}

		@Override
		public void onClick(View v) {
			// Starting transaction
			FragmentTransaction ft = fragmentManager.beginTransaction();
			Fragment fragment = (Fragment) Fragment.instantiate(
					context, ACTIONS[position].fragmentClass.getName());
			// Replacing old fragment with new one
			ft.replace(android.R.id.content, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

		}
		
	}
	@Override
	public int getCount() {
		return ACTIONS.length;
	}

	@Override
	public Object getItem(int arg0) {
		return ACTIONS[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public static class MainActionDescriptor {
		public int description;
		public int thumbnail;
		public Class<? extends Fragment> fragmentClass;

		public MainActionDescriptor(int description, int thumbnail,
				Class<? extends Fragment> fragmentClass) {
			super();
			this.description = description;
			this.thumbnail = thumbnail;
			this.fragmentClass = fragmentClass;
		}
	}

	private static MainActionDescriptor[] ACTIONS = new MainActionDescriptor[] {
			new MainActionDescriptor(R.string.a_campus, R.drawable.ic_campus, CampusFragmentPeople.class),
			new MainActionDescriptor(R.string.a_groups, R.drawable.ic_groups, MyGroupsFragment.class)
		};
}
