package eu.trentorise.smartcampus.template.mobility.fragments;

import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.journey.SingleJourney;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import smartcampus.android.template.standalone.R;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.trentorise.smartcampus.template.custom.LocationHelper;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;

public class MobilityMainFragment extends Fragment {
	private LocationHelper mLocationHelper;
	private Geocoder mGeocoder;

	private Position positionFrom;
	private Position positionTo;
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

		final LinearLayout llFromButtons = (LinearLayout) getView().findViewById(R.id.mobility_ll_from_buttons);
		final LinearLayout llFrom = (LinearLayout) getView().findViewById(R.id.mobility_ll_from);
		ImageButton btnFromPos = (ImageButton) getView().findViewById(R.id.mobility_btn_from_pos);
		ImageButton btnFromMap = (ImageButton) getView().findViewById(R.id.mobility_btn_from_map);
		ImageButton btnFromReset = (ImageButton) getView().findViewById(R.id.mobility_btn_from_reset);
		final TextView tvFromText = (TextView) getView().findViewById(R.id.mobility_tv_from_text);

		final LinearLayout llToButtons = (LinearLayout) getView().findViewById(R.id.mobility_ll_to_buttons);
		final LinearLayout llTo = (LinearLayout) getView().findViewById(R.id.mobility_ll_to);
		ImageButton btnToPos = (ImageButton) getView().findViewById(R.id.mobility_btn_to_pos);
		ImageButton btnToMap = (ImageButton) getView().findViewById(R.id.mobility_btn_to_map);
		ImageButton btnToReset = (ImageButton) getView().findViewById(R.id.mobility_btn_to_reset);
		final TextView tvToText = (TextView) getView().findViewById(R.id.mobility_tv_to_text);

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

		// TODO: select from using the map

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

		// TODO: select from using the map

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
		 * Search button
		 */
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// build SingleJourney
				SingleJourney sj = new SingleJourney();

				sj.setFrom(positionFrom);
				sj.setTo(positionTo);

				// date and time in views' getTag()
				sj.setDate(MobilityHelper.FORMAT_DATE_SMARTPLANNER.format((Date) tvDate.getTag()));
				sj.setDepartureTime(MobilityHelper.FORMAT_TIME_SMARTPLANNER.format((Date) tvTime.getTag()));

				// TODO
				// sj.setTransportTypes((TType[])
				// userPrefsHolder.getTransportTypes());
				// sj.setRouteType(userPrefsHolder.getRouteType());
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

	private Position location2position() throws IOException {
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
			position = new Position(name, null, null, Double.toString(firstAddress.getLatitude()), Double.toString(firstAddress
					.getLongitude()));
		}

		return position;
	}
}
