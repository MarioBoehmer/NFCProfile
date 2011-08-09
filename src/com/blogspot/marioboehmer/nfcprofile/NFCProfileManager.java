/*   Copyright 2011 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

/**
 * {@link NFCProfileManager} manages the switching of configured preferences on
 * NFC tag detection.
 * 
 * @author Mario Boehmer
 */
public class NFCProfileManager extends Activity {

	private SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_NO_TITLE, 0);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.R.color.transparent));
		SharedPreferences preferences = getSharedPreferences(
				"com.blogspot.marioboehmer.nfcprofile_preferences",
				MODE_PRIVATE);
		boolean isDayTime = true;
		try {
			Date daytime = parser.parse(preferences.getString(
					"daytime_preference", "06:00"));
			Date nighttime = parser.parse(preferences.getString(
					"night_time_preference", "18:00"));
			Date currentDate = new Date();
			String currentTimeFormat = currentDate.getHours() + ":"
					+ currentDate.getMinutes();
			Date currentTime = parser.parse(currentTimeFormat);

			isDayTime = currentTime.after(daytime)
					&& currentTime.before(nighttime);
		} catch (ParseException e) {
		}
		boolean manageWifi = preferences.getBoolean("wifi_preference", false);
		if (manageWifi) {
			WifiManager wifimgr = (WifiManager) getSystemService(WIFI_SERVICE);
			if (wifimgr.isWifiEnabled() && !isDayTime) {
				wifimgr.setWifiEnabled(false);
			} else if (!wifimgr.isWifiEnabled() && isDayTime) {
				wifimgr.setWifiEnabled(true);
			}
		}
		boolean manageRinger = preferences.getBoolean("ringer_preference",
				false);
		if (manageRinger) {
			AudioManager audiomgr = (AudioManager) getSystemService(AUDIO_SERVICE);
			if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
					&& !isDayTime) {
				audiomgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			} else if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
					&& isDayTime) {
				audiomgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
		}
		boolean manageAlarm = preferences.getBoolean("alarm_preference", false);
		if (manageAlarm && !isDayTime) {
			String time = preferences.getString("alarm_time_preference", null);
			if (!TextUtils.isEmpty(time) && time.contains(":")) {
				int hour = Integer.parseInt(time.split(":")[0]);
				int minutes = Integer.parseInt(time.split(":")[1]);
				Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
				i.putExtra(AlarmClock.EXTRA_MESSAGE,
						getResources().getString(R.string.nfc_alarm_title));
				i.putExtra(AlarmClock.EXTRA_HOUR, hour);
				i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
				startActivity(i);
			}
		}
		int toastId = isDayTime ? R.string.toggle_notification_day_time
				: R.string.toggle_notification_night_time;
		Toast.makeText(this, toastId, Toast.LENGTH_SHORT).show();
		finish();
	}
}
