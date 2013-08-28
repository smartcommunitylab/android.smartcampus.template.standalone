package eu.trentorise.smartcampus.template.mobility.fragments;

import it.sayservice.platform.smartplanner.data.message.Position;
import it.sayservice.platform.smartplanner.data.message.journey.SingleJourney;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import smartcampus.android.template.standalone.R;
import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;
import eu.trentorise.smartcampus.template.mobility.custom.LocationHelper;

public class MobilityMainFragment extends Fragment {
	private LocationHelper mLocationHelper;
	private Geocoder mGeocoder;

	private Position positionFrom;
	private Position positionTo;
	private TextView tvDate;
	private TextView tvTime;
	private Button btnSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mobility_plan, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		ImageButton btnFromPos = (ImageButton) getView().findViewById(R.id.mobility_btn_from_pos);
		ImageButton btnFromMap = (ImageButton) getView().findViewById(R.id.mobility_btn_from_map);
		ImageButton btnToPos = (ImageButton) getView().findViewById(R.id.mobility_btn_to_pos);
		ImageButton btnToMap = (ImageButton) getView().findViewById(R.id.mobility_btn_to_map);
		tvDate = (TextView) getView().findViewById(R.id.mobility_tv_date);
		tvTime = (TextView) getView().findViewById(R.id.mobility_tv_time);
		btnSearch = (Button) getView().findViewById(R.id.mobility_btn_search);

		/*
		 * FROM
		 */
		btnFromPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					List<Address> geocodedAddresses = mGeocoder.getFromLocation(mLocationHelper.getLocation().getLatitude(),
							mLocationHelper.getLocation().getLongitude(), 5);
					Address firstAddress = geocodedAddresses.get(0);
					positionFrom = new Position(firstAddress.getAddressLine(0), null, null, Double.toString(firstAddress
							.getLatitude()), Double.toString(firstAddress.getLongitude()));
				} catch (IOException e) {
					Log.e("Mobility", e.getMessage());
				}
			}
		});
		
		/*
		 * TO
		 */
		btnToPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					List<Address> geocodedAddresses = mGeocoder.getFromLocation(mLocationHelper.getLocation().getLatitude(),
							mLocationHelper.getLocation().getLongitude(), 5);
					Address firstAddress = geocodedAddresses.get(0);
					positionTo = new Position(firstAddress.getAddressLine(0), null, null, Double.toString(firstAddress
							.getLatitude()), Double.toString(firstAddress.getLongitude()));
				} catch (IOException e) {
					Log.e("Mobility", e.getMessage());
				}
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
}
