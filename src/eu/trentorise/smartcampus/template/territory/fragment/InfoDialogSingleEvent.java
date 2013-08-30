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
package eu.trentorise.smartcampus.template.territory.fragment;

import smartcampus.android.template.standalone.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;

/*
 * This dialog is shown when the user click on a flag with more than one events. 
 * The events' list (data) is scrollable and picking one of these, the event's 
 * details is shown in a new fragment (InfoDialogSingleEvent). 
 * */

public class InfoDialogSingleEvent extends DialogFragment {
	private BaseDTObject data;
	private static final String PARAM = "event";

	/*
	 * when the fragment is created, the event is taken and passed in the Bundle
	 * as parameter
	 */

	public static final InfoDialogSingleEvent newInstance(BaseDTObject o) {
		InfoDialogSingleEvent fragment = new InfoDialogSingleEvent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM, o);
		fragment.setArguments(bundle);
		return fragment;
	}

	/* Before the start, the fragment get the data send by param */

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (data instanceof EventObject) {
			getDialog().setTitle("Event");
		}
		data = (BaseDTObject) getArguments().getSerializable(PARAM);
		return inflater.inflate(R.layout.mapdialogsingle, container, false);
	}

	/*
	 * When the fragment start, the detail's event is created using html and
	 * shown in the DialogFragment
	 */
	@Override
	public void onStart() {
		super.onStart();
		TextView msg = (TextView) getDialog().findViewById(R.id.mapdialog_msg);
		getDialog().setTitle("Event Detail");

		if (data instanceof EventObject) {
			EventObject event = (EventObject) data;
			String msgText = "";
			msgText += "<h2>";
			msgText += event.getTitle();
			msgText += "</h2><br/><p>";
			if (event.getType() != null) {
				msgText += "<p>";
				msgText += event.getDescription();
				msgText += "</p><br/>";
			}
			msgText += "<p>" + event.getTiming() + "</p>";

			msg.setText(Html.fromHtml(msgText));
		}

		msg.setMovementMethod(new ScrollingMovementMethod());

		Button b = (Button) getDialog().findViewById(R.id.mapdialog_cancel);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});

	}
}
