package eu.trentorise.smartcampus.template.mobility.fragments;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private EditText dateEditText;
	private Calendar c;

	static DatePickerDialogFragment newInstance(EditText dateEditText) {
		DatePickerDialogFragment f = new DatePickerDialogFragment();
		f.setDateEditText(dateEditText);
		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		c = Calendar.getInstance();

		if (getDateEditText().getTag() != null) {
			c.setTime((Date) getDateEditText().getTag());
		}

		// Use the current date as the default date in the picker
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		c.set(year, month, day);
		Date date = c.getTime();
		getDateEditText().setTag(date);
		getDateEditText().setText(MobilityHelper.FORMAT_DATE_UI.format(date));
		getDialog().dismiss();
	}

	public EditText getDateEditText() {
		return dateEditText;
	}

	public void setDateEditText(EditText dateEditText) {
		this.dateEditText = dateEditText;
	}

}
