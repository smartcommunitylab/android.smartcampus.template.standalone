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

import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.template.social.custom.DialogHandler;
import eu.trentorise.smartcampus.template.social.custom.data.CMHelper;

/**
 * Dialog for creating a new group
 * @author raman
 *
 */
public class MyGroupsAddDialog extends Dialog {

	private DialogHandler<String> handler = null;
	private Group group;
	
	public MyGroupsAddDialog(Context context, DialogHandler<String> handler, Group group) {
		super(context);
		this.handler = handler;
		this.group = group;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mygroups_add_dialog);
		
		if (group != null) {
			EditText groupName = (EditText) findViewById(R.id.mygroups_add_groupname);
			groupName.setText(this.group.getName());
			Button okButton = (Button) findViewById(R.id.mygroups_add);
			okButton.setText(android.R.string.ok);
		}

		Button cancelBtn = (Button) findViewById(R.id.mygroups_add_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});

		Button okBtn = (Button) findViewById(R.id.mygroups_add);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText groupName = (EditText) findViewById(R.id.mygroups_add_groupname);
				String groupNameText = groupName.getText().toString().trim();

				List<Group> groups = CMHelper.getGroups();
				if (groups != null) {
					for (Group g : groups) {
						if (group != null && g.getSocialId() == group.getSocialId()) continue;
						if (g.getName().equals(groupNameText)) {
							Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.mygroups_name_duplicate), Toast.LENGTH_SHORT);
							toast.show();
							return;
						}
					}
				}
				
				if (groupNameText.length() > 0) {
					handler.handleSuccess(groupNameText);
					dismiss();
				} else {
					Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.mygroups_name_empty), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
}
