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
package eu.trentorise.smartcampus.template.mobility.custom;

import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Leg;
import it.sayservice.platform.smartplanner.data.message.TType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;

public class ItinerariesListAdapter extends ArrayAdapter<Itinerary> {

	Context context;
	int layoutResourceId;
	List<Itinerary> itineraries;

	public ItinerariesListAdapter(Context context, int layoutResourceId, List<Itinerary> itineraries) {
		super(context, layoutResourceId, itineraries);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.itineraries = itineraries;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RowHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.timeFrom = (TextView) row.findViewById(R.id.it_time_from);
			holder.line = (FrameLayout) row.findViewById(R.id.it_line);
			holder.timeTo = (TextView) row.findViewById(R.id.it_time_to);
			holder.transportTypes = (LinearLayout) row.findViewById(R.id.it_transporttypes);
			holder.alert = (ImageView) row.findViewById(R.id.it_alert);

			row.setTag(holder);
		} else {
			holder = (RowHolder) row.getTag();
		}

		Itinerary itinerary = itineraries.get(position);

		// time from
		Date timeFrom = new Date(itinerary.getStartime());
		String timeFromString = MobilityHelper.FORMAT_TIME_UI.format(timeFrom);
		holder.timeFrom.setText(timeFromString);

		// time to
		Date timeTo = new Date(itinerary.getEndtime());
		String timeToString = MobilityHelper.FORMAT_TIME_UI.format(timeTo);
		holder.timeTo.setText(timeToString);

		// line between times
		holder.line.addView(new LineDrawView(getContext()));

		// transport types & alerts
		boolean hasAlerts = false;

		List<TType> transportTypesList = new ArrayList<TType>();
		for (Leg l : itinerary.getLeg()) {
			if (!transportTypesList.contains(l.getTransport().getType())) {
				transportTypesList.add(l.getTransport().getType());
			}

			if ((!l.getAlertDelayList().isEmpty() || !l.getAlertParkingList().isEmpty() || !l.getAlertStrikeList().isEmpty())
					&& !hasAlerts) {
				hasAlerts = true;
			}
		}

		holder.transportTypes.removeAllViews();
		for (TType t : transportTypesList) {
			ImageView imgv = MobilityHelper.getImageByTType(getContext(), t);
			if (imgv.getDrawable() != null) {
				holder.transportTypes.addView(imgv);
			}
		}

		if (hasAlerts) {
			holder.alert.setVisibility(View.VISIBLE);
		} else {
			holder.alert.setVisibility(View.GONE);
		}

		return row;
	}

	static class RowHolder {
		TextView timeFrom;
		FrameLayout line;
		TextView timeTo;
		LinearLayout transportTypes;
		ImageView alert;
	}

	public class LineDrawView extends View {
		Paint paint = new Paint();

		public LineDrawView(Context context) {
			super(context);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(2);
		}

		@Override
		public void onDraw(Canvas canvas) {
			int w = canvas.getWidth();
			int h = canvas.getHeight();

			canvas.drawLine(w / 2, 0, w / 2, h, paint);
			// canvas.drawCircle(50, 0, 5, paint);
			// canvas.drawCircle(50, 50, 5, paint);
		}
	}

}
