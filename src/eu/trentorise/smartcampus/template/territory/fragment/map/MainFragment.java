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
package eu.trentorise.smartcampus.template.territory.fragment.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.template.Constants;
import eu.trentorise.smartcampus.template.territory.custom.data.CategoryDescriptor;
import eu.trentorise.smartcampus.template.territory.custom.data.TerritoryHelper;
import eu.trentorise.smartcampus.template.territory.fragment.InfoDialogMultiEvent;
import eu.trentorise.smartcampus.template.territory.fragment.InfoDialogSingleEvent;
import eu.trentorise.smartcampus.template.territory.fragment.map.interfaces.MapItemsHandler;
import eu.trentorise.smartcampus.template.territory.fragment.map.interfaces.MapObjectContainer;
import eu.trentorise.smartcampus.territoryservice.TerritoryService;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;
import eu.trentorise.smartcampus.territoryservice.model.ObjectFilter;

/*This is the main fragment manages the events: their receipt from the server,
 *  their addition on the map */
public class MainFragment extends MapFragment implements MapItemsHandler, OnCameraChangeListener,
		OnMarkerClickListener, MapObjectContainer {

	public static final String ARG_OBJECTS = "objects";
	public static final String ARG_EVENT_CATEGORY = "event category";

	protected GoogleMap mMap;
	private static View view;
	private String[] eventsCategories = null;
	private Collection<? extends BaseDTObject> objects;

	/*
	 * initialize all the possible categories
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CategoryDescriptor[] eventsDefault = TerritoryHelper.getEventCategoryDescriptors();
		if (eventsDefault != null) {
			List<String> eventCategory = new ArrayList<String>();
			for (CategoryDescriptor event : eventsDefault)
				eventCategory.add(event.category);
			eventsCategories = Arrays.asList(eventCategory.toArray()).toArray(
					new String[eventCategory.toArray().length]);
		}

	}

	/*
	 * inflate the layout
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.homelayout, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	/*
	 * Get and load the data
	 */
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}
		setEventCategoriesToLoad(eventsCategories);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(true);
			getSupportMap().setOnCameraChangeListener(this);
			getSupportMap().setOnMarkerClickListener(this);

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
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
	}

	private void onBaseDTObjectTap(BaseDTObject o) {
		new InfoDialogSingleEvent().newInstance(o).show(getActivity().getFragmentManager(), "me");
	}

	private void onBaseDTObjectsTap(List<BaseDTObject> list) {
		if (list == null || list.size() == 0)
			return;
		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
			return;
		} else
			new InfoDialogMultiEvent().newInstance(list, getActivity()).show(getActivity().getFragmentManager(), "me");

	}

	/*
	 * Run the AsyncTask that get the day's events
	 */
	@Override
	public void setEventCategoriesToLoad(final String... categories) {
		this.eventsCategories = categories;
		new MapLoadProcessor().execute();

	}

	private GoogleMap getSupportMap() {
		if (mMap == null) {
			if (getFragmentManager().findFragmentById(R.id.map_territory) != null
					&& getFragmentManager().findFragmentById(R.id.map_territory) instanceof MapFragment)
				mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_territory)).getMap();
			if (mMap != null)
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return mMap;
	}

	/*
	 * What happens when you click on a marker? If it's a single event open the
	 * InfoDialogSingleEvent with its details, if they are many events zoom in
	 * until it's possible else open the InfoDialogMultiEvent
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		List<BaseDTObject> list = MapManager.ClusteringHelper.getFromGridId(marker.getTitle());
		if (list == null || list.isEmpty())
			return true;

		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
		} else if (getSupportMap().getCameraPosition().zoom == getSupportMap().getMaxZoomLevel()) {
			onBaseDTObjectsTap(list);
		} else {
			MapManager.fitMapWithOverlays(list, getSupportMap());
		}
		return true;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		render(objects);
	}

	@Override
	public <T extends BaseDTObject> void addObjects(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
			this.objects = objects;
			render(objects);
			MapManager.fitMapWithOverlays(objects, getSupportMap());
		}
	}

	private void render(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			if (objects != null && getActivity() != null) {
				List<MarkerOptions> cluster = MapManager.ClusteringHelper.cluster(
						getActivity().getApplicationContext(), getSupportMap(), objects);
				MapManager.ClusteringHelper.render(getSupportMap(), cluster);
			}
		}

	}

	/*
	 * The AsyncTask that get the day's events. In the doInBackground, it's
	 * instantiated the TerritoryService and the filter. Using the service, the
	 * data are received and in the onPostExecute added to the map
	 */
	private class MapLoadProcessor extends AsyncTask<Void, Void, Collection<? extends BaseDTObject>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Collection<? extends BaseDTObject> doInBackground(Void... params) {
			TerritoryService service = new TerritoryService("https://vas-dev.smartcampuslab.it/core.territory");
			ObjectFilter filter = new ObjectFilter();

			filter.setFromTime(TerritoryHelper.getTodayMorning());

			filter.setFromTime(TerritoryHelper.getTodayEvening());

			try {
				SCAccessProvider mAccessProvider = new EmbeddedSCAccessProvider();
				List<EventObject> events = service.getEvents(filter,
						mAccessProvider.readToken(getActivity(), Constants.CLIENT_ID, Constants.CLIENT_SECRET));
				return events;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Collections.emptyList();
		}

		@Override
		protected void onPostExecute(Collection<? extends BaseDTObject> result) {
			super.onPostExecute(result);

			if (result != null) {
				addObjects(result);
			}
		}
	}
}
