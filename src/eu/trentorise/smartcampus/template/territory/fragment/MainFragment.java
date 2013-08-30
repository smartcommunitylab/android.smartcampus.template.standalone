package eu.trentorise.smartcampus.template.territory.fragment;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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
import eu.trentorise.smartcampus.template.territory.custom.data.TerritoryHelper;
import eu.trentorise.smartcampus.template.territory.custom.data.TerritoryHelper.CategoryDescriptor;
import eu.trentorise.smartcampus.territoryservice.TerritoryService;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;
import eu.trentorise.smartcampus.territoryservice.model.ObjectFilter;

public class MainFragment extends MapFragment implements MapItemsHandler, OnCameraChangeListener,
		OnMarkerClickListener, MapObjectContainer {

	public static final String ARG_OBJECTS = "objects";
	public static final String ARG_EVENT_CATEGORY = "event category";

	protected GoogleMap mMap;
	private static View view;
	private String[] eventsCategories = null;
	private String[] eventsNotTodayCategories = null;
	private Collection<? extends BaseDTObject> objects;

	@Override
	public void onStart() {
		super.onStart();
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);

		initView();
	}

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

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}

		if (getArguments() != null && getArguments().containsKey(ARG_OBJECTS)) {
			eventsCategories = null;
			List<BaseDTObject> list = (List<BaseDTObject>) getArguments().getSerializable(ARG_OBJECTS);
			new AsyncTask<List<BaseDTObject>, Void, List<BaseDTObject>>() {
				@Override
				protected List<BaseDTObject> doInBackground(List<BaseDTObject>... params) {
					return params[0];
				}

				@Override
				protected void onPostExecute(List<BaseDTObject> result) {
					addObjects(result);
				}
			}.execute(list);
		}

		else if (getArguments() != null && getArguments().containsKey(ARG_EVENT_CATEGORY)) {
			setEventCategoriesToLoad(getArguments().getString(ARG_EVENT_CATEGORY));
		}

		if (eventsCategories != null) {
			setEventCategoriesToLoad(eventsCategories);
		}
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
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
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
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem item = menu.add(Menu.CATEGORY_SYSTEM, R.id.menu_item_show_events_layers, 1, "Events");
		item.setIcon(R.drawable.ic_menu_events);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_item_show_events_layers) {
			Dialog eventsDialog = MapLayerDialogHelper.createEventsDialog(getActivity(), this, "Events",
					eventsCategories);
			eventsDialog.show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
		}
		else new InfoDialogMultiEvent().newInstance(list,getActivity()).show(getActivity().getFragmentManager(), "me");

	}

	@Override
	public void setEventCategoriesToLoad(final String... categories) {
		this.eventsCategories = categories;
		this.eventsNotTodayCategories = categories;

		/* actually only event or poi at the same time */
		new MapLoadProcessor().execute();

	}

	private boolean isTodayIncluded() {
		List<String> categoriesNotToday = new ArrayList<String>();
		boolean istodayincluded = false;
		if (eventsCategories.length > 0)
			for (int i = 0; i < eventsCategories.length; i++) {
				if (eventsCategories[i].contains("Today")) {

					istodayincluded = true;
				} else
					categoriesNotToday.add(eventsCategories[i]);

			}
		eventsNotTodayCategories = categoriesNotToday.toArray(new String[categoriesNotToday.size()]);
		return istodayincluded;
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

	private class MapLoadProcessor extends AsyncTask<Void, Void, Collection<? extends BaseDTObject>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Collection<? extends BaseDTObject> doInBackground(Void... params) {
			TerritoryService service = new TerritoryService("https://vas-dev.smartcampuslab.it/core.territory");
			ObjectFilter filter = new ObjectFilter();
//			// today    
			Calendar date = new GregorianCalendar();
//			// reset hour, minutes, seconds and millis
//			date.set(Calendar.HOUR_OF_DAY, 0);
//			date.set(Calendar.MINUTE, 0);
//			date.set(Calendar.SECOND, 0);
//			date.set(Calendar.MILLISECOND, 0);
//			filter.setFromTime(date.getTimeInMillis());

			// next day
//			date.set(Calendar.HOUR_OF_DAY, 23);
//			date.set(Calendar.MINUTE, 59);
//			date.set(Calendar.SECOND, 59);
//			date.set(Calendar.MILLISECOND, 999);
//			filter.setToTime(date.getTimeInMillis());
			filter.setFromTime(date.getTimeInMillis());
			filter.setToTime(null);
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
			 Object[] resultarray = result.toArray();
			for (int i=0;i<resultarray.length;i++)
			{
				Log.v("", " from:"+((BaseDTObject)resultarray[i]).getFromTime()+" to:"+((BaseDTObject)resultarray[i]).getToTime());
			}
			if (result != null) {
				addObjects(result);
			}
		}
	}
}
