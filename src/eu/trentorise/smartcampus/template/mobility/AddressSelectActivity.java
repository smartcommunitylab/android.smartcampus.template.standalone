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
package eu.trentorise.smartcampus.template.mobility;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import eu.trentorise.smartcampus.template.mobility.fragments.MobilityMainFragment;

public class AddressSelectActivity extends Activity implements OnMapLongClickListener {

	private GoogleMap mMap = null;
	private Location myLocation = null;
	private LatLng mPoint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapcontainer);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		if (mMap == null) {
			return;
		}

		mMap.setOnMapLongClickListener(this);
		mMap.setMyLocationEnabled(true);

		mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				if (myLocation == null) {
					myLocation = location;
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(location.getLatitude(), location.getLongitude()), 15));
				}
			}
		});

		// Toast.makeText(this, R.string.address_select_toast,
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMapLongClick(LatLng point) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(100);
		mPoint = point;
		finish();
	}

	@Override
	public void finish() {
		if (mPoint != null) {
			Intent data = new Intent();
			data.putExtra(MobilityMainFragment.ADDRESS_SELECT_FIELD,
					getIntent().getStringExtra(MobilityMainFragment.ADDRESS_SELECT_FIELD));
			data.putExtra(MobilityMainFragment.ADDRESS_SELECT_POINT, mPoint);
			setResult(RESULT_OK, data);
		}
		super.finish();
	}
}
