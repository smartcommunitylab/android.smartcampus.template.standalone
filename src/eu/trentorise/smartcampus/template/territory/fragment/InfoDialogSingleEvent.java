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



public class InfoDialogSingleEvent extends DialogFragment {
	private BaseDTObject data;

	public InfoDialogSingleEvent() {
	}



	public static final InfoDialogSingleEvent newInstance( BaseDTObject o)
	 {
		InfoDialogSingleEvent fragment = new InfoDialogSingleEvent();
	     Bundle bundle = new Bundle();   
	     bundle.putSerializable("BaseDTOObject", o);
	     fragment.setArguments(bundle);
	     return fragment ;
	 }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			if (data instanceof EventObject) {
			getDialog().setTitle("Event");
		}
		data = (BaseDTObject) getArguments().getSerializable("BaseDTOObject");
		return inflater.inflate(R.layout.mapdialogsingle, container, false);
	}

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
