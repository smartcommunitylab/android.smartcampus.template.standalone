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
package eu.trentorise.smartcampus.template.mobility.fragments;

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.journey.SingleJourney;

import java.util.ArrayList;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;
import eu.trentorise.smartcampus.template.mobility.custom.ItinerariesListAdapter;

public class ItineraryChoicesFragment extends Fragment {

	private static final String ITINERARIES = "itineraries";
	private static final String JOURNEY = "journey";
	private SingleJourney singleJourney;
	private List<Itinerary> itineraries;
	private ItinerariesListAdapter adapter;

	public static ItineraryChoicesFragment newInstance(SingleJourney singleJourney, List<Itinerary> itineraries) {
		ItineraryChoicesFragment f = new ItineraryChoicesFragment();
		f.singleJourney = singleJourney;
		f.itineraries = itineraries;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		if (singleJourney != null)
			arg0.putSerializable(JOURNEY, singleJourney);
		if (itineraries != null) {
			arg0.putSerializable(ITINERARIES, new ArrayList<Itinerary>(itineraries));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(JOURNEY))
				singleJourney = (SingleJourney) savedInstanceState.get(JOURNEY);
			if (savedInstanceState.containsKey(ITINERARIES))
				itineraries = (List<Itinerary>) savedInstanceState.get(ITINERARIES);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mobility_itinerarychoices, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		TextView dateTextView = (TextView) getView().findViewById(R.id.choices_date);
		// Date dateDate = Utils.sjDateString2date(singleJourney.getDate());
		// dateTextView.setText(new SimpleDateFormat(Config.FORMAT_DATE_UI,
		// Locale.US).format(dateDate));
		dateTextView.setText(MobilityHelper.serverDate2UIDate(singleJourney.getDate()));

		TextView timeTextView = (TextView) getView().findViewById(R.id.choices_time);
		// Date timeDate =
		// Utils.sjTimeString2date(singleJourney.getDepartureTime());
		// timeTextView.setText(new SimpleDateFormat(Config.FORMAT_TIME_UI,
		// Locale.ITALY).format(timeDate));
		timeTextView.setText(MobilityHelper.serverTime2UITime(singleJourney.getDepartureTime()));

		TextView fromTextView = (TextView) getView().findViewById(R.id.choices_from);
		fromTextView.setText(singleJourney.getFrom().getName());

		TextView toTextView = (TextView) getView().findViewById(R.id.choices_to);
		toTextView.setText(singleJourney.getTo().getName());

		ListView choicesList = (ListView) getView().findViewById(R.id.choices_listView);
		LinearLayout noitems = (LinearLayout) getView().findViewById(R.id.no_items_label);
		if ((itineraries == null) || (itineraries.size() == 0)) {
			// put "empty string"
			noitems.setVisibility(View.VISIBLE);
		} else {
			noitems.setVisibility(View.GONE);
		}
		adapter = new ItinerariesListAdapter(getActivity(), R.layout.mobility_itinerarychoices_row, itineraries);
		choicesList.setAdapter(adapter);

		choicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
				Fragment fragment = ItineraryFragment.newInstance(singleJourney, adapter.getItem(position));
				// fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				fragmentTransaction.replace(android.R.id.content, fragment, ItineraryChoicesFragment.this.getTag());
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();
			}
		});
	}

	public SingleJourney getSingleJourney() {
		return singleJourney;
	}

	public List<Itinerary> getItineraries() {
		return itineraries;
	}
}
