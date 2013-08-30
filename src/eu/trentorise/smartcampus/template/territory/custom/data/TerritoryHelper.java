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
package eu.trentorise.smartcampus.template.territory.custom.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import smartcampus.android.template.standalone.R;



public class TerritoryHelper {
	private final static String TAG = "CategoryHelper";
	private static final String POI_NONCATEGORIZED = "Other place";
	private static final String EVENT_NONCATEGORIZED = "Other event";
	private static final String STORY_NONCATEGORIZED = "Other story";

	public static final String CATEGORY_TYPE_EVENTS = "events";


	public static final String CATEGORY_TODAY = "Today";
	public static final String CATEGORY_MY = "My";

	public static CategoryDescriptor EVENTS_TODAY = new CategoryDescriptor(R.drawable.ic_marker_e_generic,
			R.drawable.ic_e_other, CATEGORY_TODAY, R.string.categories_event_today);



	public static CategoryDescriptor[] EVENT_CATEGORIES = new CategoryDescriptor[] {
			/* 1 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Concerts",
					R.string.categories_event_concert),
			/* 2 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Happy hours",
					R.string.categories_event_happyhour),
			/* 3 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Movies",
					R.string.categories_event_movie),
			/* 4 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Parties",
					R.string.categories_event_party),
			/* 5 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Seminars",
					R.string.categories_event_seminar),
			/* 6 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Theaters",
					R.string.categories_event_theater),
			/* 7 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Exhibitions",
					R.string.categories_event_exhibition),
			/* 8 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Family",
					R.string.categories_event_family),
			/* 9 */new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, EVENT_NONCATEGORIZED,
					R.string.categories_event_generic), };



	private static Map<String, String> categoryMapping = new HashMap<String, String>();

	private static Map<String, CategoryDescriptor> descriptorMap = new LinkedHashMap<String, TerritoryHelper.CategoryDescriptor>();
	static {
		for (CategoryDescriptor event : EVENT_CATEGORIES) {
			descriptorMap.put(event.category, event);
		}

		for (String s : descriptorMap.keySet()) {
			categoryMapping.put(s, s);
		}
		// custom categories for events
		categoryMapping.put("Dances", "Theaters");
		// custom categories for POIs
		categoryMapping.put("biblioteca", "Libraries");
		categoryMapping.put("museo", "Museums");
		categoryMapping.put("esposizione", "Museums");
		categoryMapping.put("arte", "Museums");
		categoryMapping.put("luogo", POI_NONCATEGORIZED);
		categoryMapping.put("ufficio", "Offices");
		categoryMapping.put("sala", POI_NONCATEGORIZED);
		categoryMapping.put("teatro", "Theater");
		categoryMapping.put("musica", "Theater");
		categoryMapping.put("universita", "University");
		categoryMapping.put("bar", "Drink");
		categoryMapping.put("ristorante", "Food");
		categoryMapping.put("Lodging", "Accomodation");
		categoryMapping.put("Other", POI_NONCATEGORIZED);
		categoryMapping.put("ou", POI_NONCATEGORIZED);
	}

	public static String[] getAllCategories(Set<String> set) {
		List<String> result = new ArrayList<String>();
		for (String key : categoryMapping.keySet()) {
			if (set.contains(categoryMapping.get(key))) {
				if (key.equals(EVENT_NONCATEGORIZED) || key.equals(POI_NONCATEGORIZED) || key.equals(STORY_NONCATEGORIZED)) {

					result.add(null);
				}
				result.add(key);
				// set.remove(categoryMapping.get(key));
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public static String getMainCategory(String category) {
		return categoryMapping.get(category);
	}

	public static int getMapIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).map_icon;
		return R.drawable.ic_marker_e_generic;
	}

	public static int getIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).thumbnail;
		return R.drawable.ic_e_other;
	}

	public static class CategoryDescriptor {
		public int map_icon;
		public int thumbnail;
		public String category;
		public int description;

		public CategoryDescriptor(int map_icon, int thumbnail, String category, int description) {
			super();
			this.map_icon = map_icon;
			this.thumbnail = thumbnail;
			this.category = category;
			this.description = description;
		}
	}



	public static CategoryDescriptor[] getEventCategoryDescriptors() {
		return EVENT_CATEGORIES;
	}



	public static String[] getEventCategories() {
		String[] res = new String[EVENT_CATEGORIES.length];
		for (int i = 0; i < EVENT_CATEGORIES.length; i++) {
			res[i] = EVENT_CATEGORIES[i].category;
		}
		return res;
	}


	

}
