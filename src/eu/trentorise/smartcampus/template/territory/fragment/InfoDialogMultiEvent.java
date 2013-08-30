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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;

/*
 * This dialog is shown when the user click on a flag with more than one events. 
 * The events' list (data) is scrollable and picking one of these, the event's 
 * details is shown in a new fragment (InfoDialogSingleEvent). 
 * */
public class InfoDialogMultiEvent extends DialogFragment {
	private Collection<BaseDTObject> data;
	private static Context context;
	private static final String PARAM = "events";

	/*
	 * when the fragment is created, the events are taken and passed in the
	 * Bundle as parameter
	 */
	public static final InfoDialogMultiEvent newInstance(Collection<BaseDTObject> events, Context ctx) {
		InfoDialogMultiEvent fragment = new InfoDialogMultiEvent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM, new ArrayList(events));
		fragment.setArguments(bundle);
		context = ctx;
		return fragment;
	}

	/* Before the start, the fragment get the data send by param */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (data instanceof EventObject) {
			getDialog().setTitle("Event");
		}
		data = (Collection<BaseDTObject>) getArguments().getSerializable(PARAM);
		return inflater.inflate(R.layout.mapdialogmulti, container, false);
	}

	/*
	 * When the fragment start, the Listview of the layout is initialized using
	 * the Adapter and the array of events
	 */
	@Override
	public void onStart() {
		super.onStart();
		getDialog().setTitle("Events List");
		final EventsArrayAdapter adapter = new EventsArrayAdapter(context, android.R.layout.simple_list_item_1,
				new ArrayList(data));
		ListView multiEvents = (ListView) getDialog().findViewById(R.id.events);
		multiEvents.setAdapter(adapter);

		Button b = (Button) getDialog().findViewById(R.id.mapdialog_cancel);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});

	}

	/*
	 * This is the adapter and every single row shows the information of a
	 * single event
	 */
	private class EventsArrayAdapter extends ArrayAdapter<BaseDTObject> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public EventsArrayAdapter(Context context, int textViewResourceId, List<BaseDTObject> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i).getTitle(), i);
			}
		}

		/*
		 * set the data of a single row. Clicking on an element, this fragment
		 * is closed and the details of the events are shown
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.today_events_row, null);

			TextView wv = (TextView) convertView.findViewById(R.id.events_text);

			wv.setText(((BaseDTObject) (data.toArray())[position]).getTitle());

			convertView.setTag(wv);
			LinearLayout line = (LinearLayout) convertView.findViewById(R.id.events_line);
			line.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getDialog().dismiss();
					new InfoDialogSingleEvent().newInstance((BaseDTObject) (data.toArray())[position]).show(
							getActivity().getFragmentManager(), "me");
				}
			});
			return convertView;

		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position).getTitle();
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
