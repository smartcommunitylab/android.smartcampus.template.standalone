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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.template.social.custom.CheckListAdapter;
import eu.trentorise.smartcampus.template.social.custom.CheckListAdapter.CheckListItem;
import eu.trentorise.smartcampus.template.social.custom.DialogHandler;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;

public class MyGroupsAddToDialog extends Dialog {

	private DialogHandler<Collection<Group>> groupAssignmentHandler = null;
	private CheckListAdapter listAdatpter = null;
	private Activity context = null;
	private Set<String> selectedGroups = null;
	
	public MyGroupsAddToDialog(Activity context, DialogHandler<Collection<Group>> groupAssignmentHandler, Set<String> selected) {
		super(context);
		this.groupAssignmentHandler = groupAssignmentHandler;
		this.context = context;
		this.selectedGroups = selected;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checklist_dialog);

		final ListView checkListView = (ListView) findViewById(R.id.checklist_listView);

		setTitle(R.string.mygroups_addto_options_title);

		listAdatpter = new CheckListAdapter(context, getLayoutInflater(), R.layout.checklist_row);
		// adapter
		checkListView.setAdapter(listAdatpter);

		// buttons
		Button cancelBtn = (Button) findViewById(R.id.checklist_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

		Button okBtn = (Button) findViewById(R.id.checklist_ok);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Set<String> selected = new HashSet<String>();
				for (int i = 0; i < checkListView.getChildCount(); i++) {
					View listChild = checkListView.getChildAt(i);
					CheckBox checkbox = (CheckBox) listChild.findViewById(R.id.checklist_checkBox);
					TextView textview = (TextView) listChild.findViewById(R.id.checklist_textView);
					if (checkbox.isChecked()) {
						selected.add(textview.getText().toString());
					}
				}
				List<Group> result = new ArrayList<Group>();
				for (Group g : CMHelper.getGroups()) {
					if (selected.contains(g.getName())) result.add(g); 
				}
				groupAssignmentHandler.handleSuccess(result);
				
				dismiss();
			}
		});
		
		for (Group g : CMHelper.getGroups()) {
			CheckListItem item = new CheckListItem();
			item.text = g.getName();
			if (selectedGroups != null && selectedGroups.contains(g.getSocialId())) {
				item.checked = true;
			}
			listAdatpter.add(item);
		}
		listAdatpter.notifyDataSetChanged();
	}
}
