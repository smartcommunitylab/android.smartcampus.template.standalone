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
package eu.trentorise.smartcampus.template.mobility.fragments;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import eu.trentorise.smartcampus.template.mobility.MobilityHelper;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	private EditText timeEditText;
	private Calendar c;

	static TimePickerDialogFragment newInstance(EditText timeEditText) {
		TimePickerDialogFragment f = new TimePickerDialogFragment();
		f.setTimeEditText(timeEditText);
		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		c = Calendar.getInstance();

		if (getTimeEditText().getTag() != null) {
			c.setTime((Date) getTimeEditText().getTag());
		}

		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		Date date = c.getTime();
		getTimeEditText().setTag(date);
		getTimeEditText().setText(MobilityHelper.FORMAT_TIME_UI.format(date));
		getDialog().dismiss();
	}

	private EditText getTimeEditText() {
		return timeEditText;
	}

	private void setTimeEditText(EditText timeEditText) {
		this.timeEditText = timeEditText;
	}

}
