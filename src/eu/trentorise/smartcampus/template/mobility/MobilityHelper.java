package eu.trentorise.smartcampus.template.mobility;

import it.sayservice.platform.smartplanner.data.message.RType;
import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.otpbeans.Route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import smartcampus.android.template.standalone.R;
import android.content.Context;
import android.widget.ImageView;

public class MobilityHelper {

	public static final SimpleDateFormat FORMAT_DATE_SMARTPLANNER = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_TIME_SMARTPLANNER = new SimpleDateFormat("hh:mmaa", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_DATE_UI = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_TIME_UI = new SimpleDateFormat("HH:mm", Locale.getDefault());

	public static final TType[] TTYPES_ALLOWED = new TType[] { TType.TRANSIT, TType.CAR, TType.WALK };
	public static final RType RTYPE_DEFAULT = RType.fastest;

	public static String serverDate2UIDate(CharSequence dateStr) {
		try {
			Date date = dateStr != null ? MobilityHelper.FORMAT_DATE_SMARTPLANNER.parse(dateStr.toString()) : new Date();
			return MobilityHelper.FORMAT_DATE_UI.format(date);
		} catch (ParseException e) {
			return MobilityHelper.FORMAT_DATE_UI.format(new Date());
		}
	}

	public static String serverTime2UITime(CharSequence timeStr) {
		try {
			Date time = timeStr != null ? MobilityHelper.FORMAT_TIME_SMARTPLANNER.parse(timeStr.toString()) : new Date();
			return MobilityHelper.FORMAT_TIME_UI.format(time);
		} catch (ParseException e) {
			return MobilityHelper.FORMAT_TIME_UI.format(new Date());
		}
	}

	public static String getTTypeUIString(Context ctx, TType tType) {
		String uis = null;

		switch (tType) {
		case TRANSIT:
			uis = ctx.getString(R.string.ttype_transit);
			break;
		case CAR:
			uis = ctx.getString(R.string.ttype_car);
			break;
		// case BICYCLE:
		// uis = ctx.getString(R.string.ttype_bicycle);
		// break;
		// // case SHAREDBIKE:
		// // uis = ctx.getString(R.string.ttype_sharedbike);
		// // break;
		// case SHAREDBIKE_WITHOUT_STATION:
		// uis = ctx.getString(R.string.ttype_sharedbike_wo_station);
		// break;
		// case CARWITHPARKING:
		// uis = ctx.getString(R.string.ttype_car_w_parking);
		// break;
		// // case SHAREDCAR:
		// // uis = ctx.getString(R.string.ttype_sharedcar);
		// // break;
		// case SHAREDCAR_WITHOUT_STATION:
		// uis = ctx.getString(R.string.ttype_sharedcar_wo_station);
		// break;
		// case BUS:
		// uis = ctx.getString(R.string.ttype_bus);
		// break;
		// case TRAIN:
		// uis = ctx.getString(R.string.ttype_train);
		// break;
		case WALK:
			uis = ctx.getString(R.string.ttype_walk);
			break;
		// case GONDOLA:
		// uis = ctx.getString(R.string.ttype_gondola);
		// break;
		// case SHUTTLE:
		// uis = ctx.getString(R.string.ttype_shuttle);
		// break;
		default:
			break;
		}

		return uis;
	}

	public static ImageView getImageByTType(Context ctx, TType tType) {
		ImageView imgv = new ImageView(ctx);

		switch (tType) {
		case BICYCLE:
			imgv.setImageResource(R.drawable.ic_mt_bicycle);
			break;
		case CAR:
			imgv.setImageResource(R.drawable.ic_mt_car);
			break;
		case BUS:
			imgv.setImageResource(R.drawable.ic_mt_bus);
			break;
		case WALK:
			imgv.setImageResource(R.drawable.ic_mt_foot);
			break;
		case TRAIN:
			imgv.setImageResource(R.drawable.ic_mt_train);
			break;
		case TRANSIT:
			imgv.setImageResource(R.drawable.ic_mt_bus);
			break;
		default:
		}

		return imgv;
	}

	public static Comparator<Route> getRouteComparator() {
		Comparator<Route> comparator = new Comparator<Route>() {
			public int compare(Route o1, Route o2) {
				String s1 = o1.getRouteShortName();
				String s2 = o2.getRouteShortName();

				int thisMarker = 0;
				int thatMarker = 0;
				int s1Length = s1.length();
				int s2Length = s2.length();

				while (thisMarker < s1Length && thatMarker < s2Length) {
					String thisChunk = getChunk(s1, s1Length, thisMarker);
					thisMarker += thisChunk.length();

					String thatChunk = getChunk(s2, s2Length, thatMarker);
					thatMarker += thatChunk.length();

					// If both chunks contain numeric characters, sort them
					// numerically
					int result = 0;
					if (Character.isDigit(thisChunk.charAt(0)) && Character.isDigit(thatChunk.charAt(0))) {
						// Simple chunk comparison by length.
						int thisChunkLength = thisChunk.length();
						result = thisChunkLength - thatChunk.length();
						// If equal, the first different number counts
						if (result == 0) {
							for (int i = 0; i < thisChunkLength; i++) {
								result = thisChunk.charAt(i) - thatChunk.charAt(i);
								if (result != 0) {
									return result;
								}
							}
						}
					} else {
						result = thisChunk.compareTo(thatChunk);
					}

					if (result != 0)
						return result;
				}

				return s1Length - s2Length;
			}
		};

		return comparator;
	}

	private static String getChunk(String s, int slength, int marker) {
		StringBuilder chunk = new StringBuilder();
		char c = s.charAt(marker);
		chunk.append(c);
		marker++;
		if (Character.isDigit(c)) {
			while (marker < slength) {
				c = s.charAt(marker);
				if (!Character.isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		} else {
			while (marker < slength) {
				c = s.charAt(marker);
				if (Character.isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		}
		return chunk.toString();
	}

}
