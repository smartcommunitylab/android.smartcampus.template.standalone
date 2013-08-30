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

import it.sayservice.platform.smartplanner.data.message.Leg;
import it.sayservice.platform.smartplanner.data.message.LegGeometery;

import java.util.ArrayList;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class LegMapActivity extends Activity implements OnMarkerClickListener {

	public static final String ACTIVE_POS = "aPOS";
	public static final String POLYLINES = "polylines";
	public static final String LEGS = "legs";
	public static final String DATE = "date";

	private List<String> polylines;
	private int activePos;
	private int index;

	private GoogleMap mMap = null;
	private List<List<LatLng>> legsPoints = new ArrayList<List<LatLng>>();

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (polylines != null) {
			outState.putSerializable(POLYLINES, new ArrayList<String>(polylines));
		}
		outState.putInt(ACTIVE_POS, activePos);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapcontainer);

		if (savedInstanceState != null) {
			polylines = (List<String>) savedInstanceState.getSerializable(POLYLINES);
			activePos = savedInstanceState.getInt(ACTIVE_POS);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (getSupportMap() == null)
			return;

		getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
		getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		getSupportMap().setOnMarkerClickListener(this);

		// show my location
		getSupportMap().setMyLocationEnabled(true);

		if (getIntent() != null) {
			List<Leg> legs = (List<Leg>) getIntent().getSerializableExtra(LEGS);
			if (legs != null) {
				polylines = legs2polylines(legs);
			}
			activePos = getIntent().getIntExtra(ACTIVE_POS, -1);
		}

		if (getSupportMap() != null) {
			getSupportMap().clear();
			setPath(polylines, activePos);
			adaptMap(getSupportMap());
			draw(getSupportMap());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(false);
			getSupportMap().setOnCameraChangeListener(null);
			getSupportMap().setOnMarkerClickListener(null);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return true;
	}

	private GoogleMap getSupportMap() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		return mMap;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	private List<String> legs2polylines(List<Leg> legs) {
		List<String> polylines = new ArrayList<String>();

		for (Leg leg : legs) {
			LegGeometery lg = leg.getLegGeometery();
			String polyline = lg.getPoints();
			polylines.add(polyline);
		}

		return polylines;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/*
	 * UTILITIES FOR LEGS
	 */
	private void setPath(List<String> polylines, int index) {
		for (String polyline : polylines) {
			List<LatLng> legPoints = decodePolyline(polyline);
			legsPoints.add(legPoints);
		}

		this.index = index;
	}

	private List<LatLng> decodePolyline(String encoded) {
		List<LatLng> polyline = new ArrayList<LatLng>();

		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			if (index >= len) {
				break;
			}
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			polyline.add(p);
		}

		return polyline;
	}

	private void adaptMap(GoogleMap map) {
		double minLatitude = 81;
		double maxLatitude = (-81);
		double minLongitude = 181;
		double maxLongitude = (-181);

		List<LatLng> points = new ArrayList<LatLng>();

		// zoom on active leg
		if (index >= 0 && index < legsPoints.size()) {
			points.addAll(legsPoints.get(index));
		} else if (index == -1) {
			// zoom on start
			points.add(legsPoints.get(0).get(0));
		} else if (index == legsPoints.size()) {
			// zoom on stop
			List<LatLng> legPoints = legsPoints.get(legsPoints.size() - 1);
			points.add(legPoints.get(legPoints.size() - 1));
		} else {
			// zoom on all itinerary
			for (List<LatLng> list : legsPoints) {
				points.addAll(list);
			}
		}

		for (LatLng point : points) {
			double latitude = point.latitude;
			double longitude = point.longitude;

			if (latitude != 0 && longitude != 0) {
				minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
				maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;
				minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
				maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
			}
		}

		if (points.size() == 1) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 18), 1, null);
		} else {
			LatLng northeast = new LatLng(maxLatitude, maxLongitude);
			LatLng southwest = new LatLng(minLatitude, minLongitude);
			LatLngBounds llb = LatLngBounds.builder().include(southwest).include(northeast).build();

			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(llb, displaymetrics.widthPixels, displaymetrics.widthPixels, 16));
		}
	}

	private boolean draw(GoogleMap map) {
		for (int i = 0; i < legsPoints.size(); i++) {
			// default
			int color = getApplicationContext().getResources().getColor(R.color.path);
			if (i < index) {
				// past
				color = getApplicationContext().getResources().getColor(R.color.path_done);
			} else if (i == index) {
				// actual
				color = getApplicationContext().getResources().getColor(R.color.path_active);
			}

			List<LatLng> legPoints = legsPoints.get(i);
			if (i != index) {
				drawPath(map, legPoints, color);
			}

			// markers
			// start
			if (i == 0) {
				LatLng startLatLng = legPoints.get(0);
				map.addMarker(new MarkerOptions().position(startLatLng).icon(
						BitmapDescriptorFactory.fromResource(R.drawable.ic_start)));
			}

			// stop
			if (i == (legsPoints.size() - 1)) {
				LatLng stopLatLng = legPoints.get(legPoints.size() - 1);
				map.addMarker(new MarkerOptions().position(stopLatLng).icon(
						BitmapDescriptorFactory.fromResource(R.drawable.ic_stop)));
			}
		}
		if (index == -1)// show start leg
			drawPath(map, legsPoints.get(index + 1), getApplicationContext().getResources().getColor(R.color.path_active));
		else if (index == legsPoints.size())// show end leg
			drawPath(map, legsPoints.get(legsPoints.size() - 1),
					getApplicationContext().getResources().getColor(R.color.path_active));
		else
			drawPath(map, legsPoints.get(index), getApplicationContext().getResources().getColor(R.color.path_active));

		return true;
	}

	private void drawPath(GoogleMap map, List<LatLng> points, int color) {
		// int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(6);

		PolylineOptions po = new PolylineOptions().addAll(points).width(6).color(color);
		Polyline pl = map.addPolyline(po);
		pl.setVisible(true);
	}

}
