/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile.profile;

import android.content.SharedPreferences;

/**
 * A helper class which provides operations for converting {@link Profile}
 * domain objects.
 * 
 * @author Mario Boehmer
 */
public class ProfileHelper {

	private final static int ENABLE_TOGGLE_WIFI = 0;
	private final static int ENABLE_TOGGLE_BLUETOOTH = 1;
	private final static int ENABLE_TOGGLE_RINGTONE = 2;
	private final static int ENABLE_TOGGLE_AIRPLANE_MODE = 3;
	private final static int ENABLE_SET_ALARM = 4;
	private final static int ALARM_TIME = 5;
	private final static int ENABLE_START_EXTERNAL_APP = 6;
	private final static int EXTERNAL_APP_PACKAGE_NAME = 7;
	private final static String DISABLED = "0";
	private final static String ENABLED = "1";

	private final static String VALUE_SEPARATOR = "+";
	private final static String PREFERENCE_SEPARATOR = "|";

	public static Profile getProfileFromProfileURLSuffix(String profileURLSuffix) {
		Profile profile = new Profile();
		String[] preferences = profileURLSuffix.split("\\"
				+ PREFERENCE_SEPARATOR);
		for (String preference : preferences) {
			String[] keyValuePair = preference.split("\\" + VALUE_SEPARATOR);
			int key = Integer.parseInt(keyValuePair[0]);
			String value = keyValuePair[1];
			switch (key) {
			case ENABLE_TOGGLE_WIFI:
				if (ENABLED.equals(value)) {
					profile.setToggleWifiEnabled(true);
				} else {
					profile.setToggleWifiEnabled(false);
				}
				break;

			case ENABLE_TOGGLE_BLUETOOTH:
				if (ENABLED.equals(value)) {
					profile.setToggleBluetoothEnabled(true);
				} else {
					profile.setToggleBluetoothEnabled(false);
				}
				break;

			case ENABLE_TOGGLE_RINGTONE:
				if (ENABLED.equals(value)) {
					profile.setToggleRingtoneEnabled(true);
				} else {
					profile.setToggleRingtoneEnabled(false);
				}
				break;

			case ENABLE_TOGGLE_AIRPLANE_MODE:
				if (ENABLED.equals(value)) {
					profile.setToggleAirplaneModeEnabled(true);
				} else {
					profile.setToggleAirplaneModeEnabled(false);
				}
				break;
			case ENABLE_SET_ALARM:
				if (ENABLED.equals(value)) {
					profile.setSetAlarmEnabled(true);
				} else {
					profile.setSetAlarmEnabled(false);
				}
				break;
			case ALARM_TIME:
				if (!DISABLED.equals(value)) {
					profile.setAlarmTime(value);
				}
				break;
			case ENABLE_START_EXTERNAL_APP:
				if (ENABLED.equals(value)) {
					profile.setStartExternalAppEnabled(true);
				} else {
					profile.setStartExternalAppEnabled(false);
				}
				break;
			case EXTERNAL_APP_PACKAGE_NAME:
				if (!DISABLED.equals(value)) {
					profile.setExternalAppPackageName(value);
				}
				break;
			}
		}
		return profile;
	}

	public static String getProfileURLSuffix(Profile profile) {
		StringBuilder profileURLSuffixBuilder = new StringBuilder();
		// WIFI
		profileURLSuffixBuilder.append(ENABLE_TOGGLE_WIFI);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isToggleWifiEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// BLUETOOTH
		profileURLSuffixBuilder.append(ENABLE_TOGGLE_BLUETOOTH);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isToggleBluetoothEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// RINGTONE
		profileURLSuffixBuilder.append(ENABLE_TOGGLE_RINGTONE);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isToggleRingtoneEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// AIRPLANE MODE
		profileURLSuffixBuilder.append(ENABLE_TOGGLE_AIRPLANE_MODE);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isToggleAirplaneModeEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// SET ALARM
		profileURLSuffixBuilder.append(ENABLE_SET_ALARM);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isSetAlarmEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// ALARM TIME
		profileURLSuffixBuilder.append(ALARM_TIME);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isSetAlarmEnabled()) {
			profileURLSuffixBuilder.append(profile.getAlarmTime());
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// EXTERNAL APP
		profileURLSuffixBuilder.append(ENABLE_START_EXTERNAL_APP);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isStartExternalAppEnabled()) {
			profileURLSuffixBuilder.append(ENABLED);
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		profileURLSuffixBuilder.append(PREFERENCE_SEPARATOR);
		// EXTERNAL APP PACKAGE NAME
		profileURLSuffixBuilder.append(EXTERNAL_APP_PACKAGE_NAME);
		profileURLSuffixBuilder.append(VALUE_SEPARATOR);
		if (profile.isStartExternalAppEnabled()) {
			profileURLSuffixBuilder.append(profile.getExternalAppPackageName());
		} else {
			profileURLSuffixBuilder.append(DISABLED);
		}
		return profileURLSuffixBuilder.toString();
	}

	public static Profile getProfileFromPreferences(
			SharedPreferences preferences) {
		Profile profile = new Profile();
		profile.setToggleWifiEnabled(preferences.getBoolean("wifi_preference",
				false));
		profile.setToggleBluetoothEnabled(preferences.getBoolean(
				"bluetooth_preference", false));
		profile.setToggleAirplaneModeEnabled(preferences.getBoolean(
				"airplane_mode_preference", false));
		profile.setToggleRingtoneEnabled(preferences.getBoolean(
				"ringer_preference", false));
		profile.setSetAlarmEnabled(preferences.getBoolean("alarm_preference",
				false));
		profile.setAlarmTime(preferences
				.getString("alarm_time_preference", "0"));
		profile.setStartExternalAppEnabled(preferences.getBoolean(
				"start_external_app_preference", false));
		profile.setExternalAppPackageName(preferences.getString(
				"external_app_package_name", "0"));
		return profile;
	}
}
