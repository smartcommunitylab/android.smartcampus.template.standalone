package eu.trentorise.smartcampus.template.mobility.fragments;

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.journey.SingleJourney;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import smartcampus.android.template.standalone.R;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.ch;
import com.google.android.gms.maps.model.LatLng;

import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.mobilityservice.MobilityPlannerService;
import eu.trentorise.smartcampus.template.Constants;
import eu.trentorise.smartcampus.template.custom.LocationHelper;
import eu.trentorise.smartcampus.template.mobility.AddressSelectActivity;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;

public class MobilityMainFragment extends Fragment {

	public static final int ADDRESS_SELECT = 1983;
	public static final String ADDRESS_SELECT_FIELD = "field";
	public static final String ADDRESS_SELECT_FIELD_FROM = "from";
	public static final String ADDRESS_SELECT_FIELD_TO = "to";
	public static final String ADDRESS_SELECT_POINT = "point";

	private LocationHelper mLocationHelper;
	private Geocoder mGeocoder;

	private Position positionFrom;
	private Position positionTo;

	private LinearLayout llFromButtons;
	private LinearLayout llFrom;
	private TextView tvFromText;
	private LinearLayout llToButtons;
	private LinearLayout llTo;
	private TextView tvToText;

	private EditText tvDate;
	private EditText tvTime;
	private Button btnSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mobility_plan, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		llFromButtons = (LinearLayout) getView().findViewById(R.id.mobility_ll_from_buttons);
		llFrom = (LinearLayout) getView().findViewById(R.id.mobility_ll_from);
		ImageButton btnFromPos = (ImageButton) getView().findViewById(R.id.mobility_btn_from_pos);
		ImageButton btnFromMap = (ImageButton) getView().findViewById(R.id.mobility_btn_from_map);
		ImageButton btnFromReset = (ImageButton) getView().findViewById(R.id.mobility_btn_from_reset);
		tvFromText = (TextView) getView().findViewById(R.id.mobility_tv_from_text);

		llToButtons = (LinearLayout) getView().findViewById(R.id.mobility_ll_to_buttons);
		llTo = (LinearLayout) getView().findViewById(R.id.mobility_ll_to);
		ImageButton btnToPos = (ImageButton) getView().findViewById(R.id.mobility_btn_to_pos);
		ImageButton btnToMap = (ImageButton) getView().findViewById(R.id.mobility_btn_to_map);
		ImageButton btnToReset = (ImageButton) getView().findViewById(R.id.mobility_btn_to_reset);
		tvToText = (TextView) getView().findViewById(R.id.mobility_tv_to_text);

		tvDate = (EditText) getView().findViewById(R.id.mobility_tv_date);
		tvTime = (EditText) getView().findViewById(R.id.mobility_tv_time);
		btnSearch = (Button) getView().findViewById(R.id.mobility_btn_search);

		/*
		 * FROM
		 */
		btnFromPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					positionFrom = location2position();
					if (positionFrom != null) {
						tvFromText.setText(positionFrom.getName());
						llFromButtons.setVisibility(View.GONE);
						llFrom.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(getActivity(), R.string.mobility_plan_wait_location, Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {
					Log.e("Mobility", e.getMessage());
				}
			}
		});

		btnFromMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AddressSelectActivity.class);
				i.putExtra(MobilityMainFragment.ADDRESS_SELECT_FIELD, MobilityMainFragment.ADDRESS_SELECT_FIELD_FROM);
				startActivityForResult(i, ADDRESS_SELECT);
			}
		});

		btnFromReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvFromText.setText("");
				llFrom.setVisibility(View.GONE);
				llFromButtons.setVisibility(View.VISIBLE);
			}
		});

		/*
		 * TO
		 */
		btnToPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					positionTo = location2position();
					if (positionTo != null) {
						tvToText.setText(positionTo.getName());
						llToButtons.setVisibility(View.GONE);
						llTo.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(getActivity(), R.string.mobility_plan_wait_location, Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {
					Log.e("Mobility", e.getMessage());
				}
			}
		});

		btnToMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AddressSelectActivity.class);
				i.putExtra(MobilityMainFragment.ADDRESS_SELECT_FIELD, MobilityMainFragment.ADDRESS_SELECT_FIELD_TO);
				startActivityForResult(i, ADDRESS_SELECT);
			}
		});

		btnToReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvToText.setText("");
				llTo.setVisibility(View.GONE);
				llToButtons.setVisibility(View.VISIBLE);
			}
		});

		/*
		 * DATE AND TIME
		 */
		Date now = new Date(System.currentTimeMillis());
		tvDate.setTag(now);
		tvDate.setText(MobilityHelper.FORMAT_DATE_UI.format(now));
		tvTime.setTag(now);
		tvTime.setText(MobilityHelper.FORMAT_TIME_UI.format(now));

		tvDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment datePickerDialogFragment = DatePickerDialogFragment.newInstance(tvDate);
				datePickerDialogFragment.show(getFragmentManager(), "datePicker");
			}
		});

		tvTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(tvTime);
				timePickerDialogFragment.show(getFragmentManager(), "timePicker");
			}
		});

		/*
		 * OPTIONS
		 */
		final TableLayout tTypesTableLayout = (TableLayout) getView().findViewById(R.id.transporttypes_table);
		tTypesTableLayout.removeAllViews(); // prevents duplications
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tTypesTableLayout.setShrinkAllColumns(true);

		TableRow tableRow = new TableRow(getActivity());
		for (int tCounter = 0; tCounter < MobilityHelper.TTYPES_ALLOWED.length; tCounter++) {
			TType tType = MobilityHelper.TTYPES_ALLOWED[tCounter];

			// if (tCounter % 2 == 0) {
			// tableRow = new TableRow(getActivity());
			// tableRow.setGravity(Gravity.CENTER_VERTICAL);
			// tableRow.setLayoutParams(params);
			// }

			CheckBox cb = new CheckBox(getActivity());
			cb.setText(MobilityHelper.getTTypeUIString(getActivity(), tType));
			// cb.setTextColor(getActivity().getResources().getColor(android.R.color.black));
			cb.setTag(tType);

			if (tCounter == 0) {
				cb.setChecked(true);
			}

			tableRow.addView(cb);

			if (tableRow.getChildCount() == 3 || (tCounter + 1 == MobilityHelper.TTYPES_ALLOWED.length)) {
				tTypesTableLayout.addView(tableRow);

				tableRow = new TableRow(getActivity());
				tableRow.setGravity(Gravity.CENTER_VERTICAL);
				tableRow.setLayoutParams(params);
			}
		}

		/*
		 * Search button
		 */
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// build SingleJourney
				SingleJourney sj = new SingleJourney();

				// points
				sj.setFrom(positionFrom);
				sj.setTo(positionTo);

				// date and time in views' getTag()
				sj.setDate(MobilityHelper.FORMAT_DATE_SMARTPLANNER.format((Date) tvDate.getTag()));
				sj.setDepartureTime(MobilityHelper.FORMAT_TIME_SMARTPLANNER.format((Date) tvTime.getTag()));

				List<TType> tTypes = new ArrayList<TType>();
				for (int i = 0; i < tTypesTableLayout.getChildCount(); i++) {
					TableRow row = (TableRow) tTypesTableLayout.getChildAt(i);
					for (int j = 0; j < row.getChildCount(); j++) {
						CheckBox checkBox = (CheckBox) row.getChildAt(j);
						if (checkBox.isChecked()) {
							tTypes.add((TType) checkBox.getTag());
						}
					}
				}

				sj.setTransportTypes(tTypes.toArray(new TType[] {}));
				sj.setRouteType(MobilityHelper.RTYPE_DEFAULT);

				new PlanSingleJourneyAsyncTask().execute(sj);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mLocationHelper == null) {
			mLocationHelper = new LocationHelper(getActivity());
		}
		mLocationHelper.start();

		if (mGeocoder == null) {
			mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
		}
	}

	@Override
	public void onPause() {
		if (mLocationHelper != null) {
			mLocationHelper.stop();
		}

		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ADDRESS_SELECT == requestCode && data != null && data.getStringExtra(ADDRESS_SELECT_FIELD) != null) {
			try {
				if (data.getStringExtra(ADDRESS_SELECT_FIELD).equals(ADDRESS_SELECT_FIELD_FROM)) {
					positionFrom = point2position((LatLng) data.getParcelableExtra(ADDRESS_SELECT_POINT));
					if (positionFrom != null) {
						tvFromText.setText(positionFrom.getName());
						llFromButtons.setVisibility(View.GONE);
						llFrom.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(getActivity(), R.string.mobility_plan_wait_location, Toast.LENGTH_SHORT).show();
					}
				} else if (data.getStringExtra(ADDRESS_SELECT_FIELD).equals(ADDRESS_SELECT_FIELD_TO)) {
					positionTo = point2position((LatLng) data.getParcelableExtra(ADDRESS_SELECT_POINT));
					if (positionTo != null) {
						tvToText.setText(positionTo.getName());
						llToButtons.setVisibility(View.GONE);
						llTo.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(getActivity(), R.string.mobility_plan_wait_location, Toast.LENGTH_SHORT).show();
					}
				}
			} catch (IOException e) {
				Log.e("Mobility", e.getMessage());
			}
		}
	}

	private Position location2position() throws IOException {
		/*
		 * This method uses the native Android Geocoder to obtain an address
		 * providing only latitude and longitude. It takes by default the
		 * current position!
		 */
		Position position = null;

		if (mLocationHelper.getLocation() != null) {
			List<Address> geocodedAddresses = mGeocoder.getFromLocation(mLocationHelper.getLocation().getLatitude(),
					mLocationHelper.getLocation().getLongitude(), 5);
			Address firstAddress = geocodedAddresses.get(0);
			String name = "";
			for (int i = 0; i < firstAddress.getMaxAddressLineIndex(); i++) {
				name += firstAddress.getAddressLine(i);
				if ((i + 1) < firstAddress.getMaxAddressLineIndex()) {
					name += ", ";
				}
			}
			position = new Position(name, null, null, Double.toString(firstAddress.getLongitude()),
					Double.toString(firstAddress.getLatitude()));
		}

		return position;
	}

	private Position point2position(LatLng point) throws IOException {
		/*
		 * This method uses the native Android Geocoder to obtain an address
		 * providing only latitude and longitude. It takes a LatLng point as
		 * input!
		 */
		Position position = null;

		if (point != null) {
			List<Address> geocodedAddresses = mGeocoder.getFromLocation(point.latitude, point.longitude, 5);
			Address firstAddress = geocodedAddresses.get(0);
			String name = "";
			for (int i = 0; i < firstAddress.getMaxAddressLineIndex(); i++) {
				name += firstAddress.getAddressLine(i);
				if ((i + 1) < firstAddress.getMaxAddressLineIndex()) {
					name += ", ";
				}
			}
			position = new Position(name, null, null, Double.toString(firstAddress.getLongitude()),
					Double.toString(firstAddress.getLatitude()));
		}

		return position;
	}

	/*
	 * AsyncTasks
	 */
	private class PlanSingleJourneyAsyncTask extends AsyncTask<SingleJourney, Void, List<Itinerary>> {
		/*
		 * This AsyncTask uses the MobilityPlannerService functionalities from
		 * mobilityservice.client: after MobilityPlannerService initialization you have to get the user token and provide it with 
		 */

		private SingleJourney singleJourney;

		@Override
		protected List<Itinerary> doInBackground(SingleJourney... params) {
			singleJourney = params[0];

			try {
				MobilityPlannerService plannerService = new MobilityPlannerService(Constants.MOBILITY_SERVICE);
				String token = new EmbeddedSCAccessProvider().readToken(getActivity(), Constants.CLIENT_ID,
						Constants.CLIENT_SECRET);
				List<Itinerary> itineraries = plannerService.planSingleJourney(singleJourney, token);
				return itineraries;
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), e.getMessage());
			}

			return Collections.emptyList();
		}

		@Override
		protected void onPostExecute(List<Itinerary> result) {
			FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
			Fragment fragment = ItineraryChoicesFragment.newInstance(singleJourney, result);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.replace(android.R.id.content, fragment, fragment.getTag());
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
		}
	}

}
