/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.TimePicker;

/**
 * {@link TimePreference} is a custom {@link Preference} which displays a dialog
 * with time value picker elements.
 * 
 * @author Mario Boehmer
 */
public class TimePreference extends Preference implements
		TimePicker.OnTimeChangedListener {

	private static final String TIME_PATTERN = "[0-2]*[0-9]-[0-5]*[0-9]";
	private TimePicker timePicker;
	private String defaultValue;
	private String result;
	private Dialog dialog;

	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultValue = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/android", "defaultValue");
		init();
	}

	public TimePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		defaultValue = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/android", "defaultValue");
		init();
	}

	private void init() {
		setPersistent(true);
		timePicker = new TimePicker(getContext());
		timePicker.setOnTimeChangedListener(this);
		timePicker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
		timePicker.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
		builder.setTitle(getTitle().toString());
		builder.setContentView(timePicker);
		builder.setPositiveButton(R.string.ok, onClickListener);
		builder.setNegativeButton(R.string.cancel, onClickListener);
		dialog = builder.create(R.layout.custom_dialog_layout);
	}

	public void onTimeChanged(TimePicker view, int hour, int minute) {
		result = hour + ":" + minute;
	}

	@Override
	protected void onClick() {
		int h = getHour();
		int m = getMinute();
		if (h >= 0 && m >= 0) {
			timePicker.setCurrentHour(h);
			timePicker.setCurrentMinute(m);
		}
		dialog.show();
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				timePicker.clearFocus();
				result = timePicker.getCurrentHour() + "-"
						+ timePicker.getCurrentMinute();
				persistString(result);
				callChangeListener(result);
				dialog.dismiss();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}

		}
	};

	/**
	 * Get the hour value (in 24 hour time format)
	 * 
	 * @return The hour value, from 0 to 23
	 */
	private int getHour() {
		String time = getPersistedString(this.defaultValue);
		if (time == null || !time.matches(TIME_PATTERN)) {
			return -1;
		}

		return Integer.valueOf(time.split("-|/")[0]);
	}

	/**
	 * Get the minute value
	 * 
	 * @return the minute value, from 0 to 59
	 */
	private int getMinute() {
		String time = getPersistedString(this.defaultValue);
		if (time == null || !time.matches(TIME_PATTERN)) {
			return -1;
		}

		return Integer.valueOf(time.split("-|/")[1]);
	}
}