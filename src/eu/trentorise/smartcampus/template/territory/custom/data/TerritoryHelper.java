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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import smartcampus.android.template.standalone.R;

public class TerritoryHelper {
	private final static String TAG = "CategoryHelper";
	public static final String CATEGORY_TYPE_EVENTS = "events";
	public static final String CATEGORY_TODAY = "Today";

	/*
	 * just one generic category could be extend with new categories
	 */
	public static CategoryDescriptor[] EVENT_CATEGORIES = new CategoryDescriptor[] {

	new CategoryDescriptor(R.drawable.ic_marker_e_generic, R.drawable.ic_e_other, "Generic",
			R.string.categories_event_concert) };
	private static Map<String, String> categoryMapping = new HashMap<String, String>();
	private static Map<String, CategoryDescriptor> descriptorMap = new LinkedHashMap<String, CategoryDescriptor>();

	static {
		for (CategoryDescriptor event : EVENT_CATEGORIES) {
			descriptorMap.put(event.category, event);
		}

	}

	public static int getMapIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).map_icon;
		return R.drawable.ic_marker_e_generic;
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

	public static Long getTodayMorning() {
		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date.getTimeInMillis();
	}

	public static Long getTodayEvening() {
		Calendar date = new GregorianCalendar();
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);
		date.set(Calendar.MILLISECOND, 999);
		return date.getTimeInMillis();
	}
	
	

}
