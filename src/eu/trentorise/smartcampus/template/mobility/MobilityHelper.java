package eu.trentorise.smartcampus.template.mobility;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MobilityHelper {

	public static final SimpleDateFormat FORMAT_DATE_SMARTPLANNER = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_TIME_SMARTPLANNER = new SimpleDateFormat("hh:mmaa", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_DATE_UI = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	public static final SimpleDateFormat FORMAT_TIME_UI = new SimpleDateFormat("HH:mm", Locale.getDefault());

}
