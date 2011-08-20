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
import android.bluetooth.BluetoothAdapter;
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
	private boolean isDayTime = true;
	private boolean manageWifi;
	private boolean manageBluetooth;
	private boolean manageRinger;
	private boolean manageAlarm;
	private String alarmTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_NO_TITLE, 0);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.R.color.transparent));
		readPreferences();
		if (manageWifi) {
			WifiManager wifimgr = (WifiManager) getSystemService(WIFI_SERVICE);
			if (wifimgr != null) {
				if (wifimgr.isWifiEnabled() && !isDayTime) {
					wifimgr.setWifiEnabled(false);
				} else if (!wifimgr.isWifiEnabled() && isDayTime) {
					wifimgr.setWifiEnabled(true);
				}
			}
		}
		if (manageBluetooth) {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (bluetoothAdapter != null) {
				if (bluetoothAdapter.isEnabled() && !isDayTime) {
					bluetoothAdapter.disable();
				} else if (!bluetoothAdapter.isEnabled() && isDayTime) {
					bluetoothAdapter.enable();
				}
			}
		}
		if (manageRinger) {
			AudioManager audiomgr = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (audiomgr != null) {
				if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
						&& !isDayTime) {
					audiomgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				} else if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
						&& isDayTime) {
					audiomgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		}
		if (manageAlarm && !isDayTime) {
			if (!TextUtils.isEmpty(alarmTime) && alarmTime.contains(":")) {
				int hour = Integer.parseInt(alarmTime.split(":")[0]);
				int minutes = Integer.parseInt(alarmTime.split(":")[1]);
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

	private void readPreferences() {
		SharedPreferences preferences = getSharedPreferences(
				"com.blogspot.marioboehmer.nfcprofile_preferences",
				MODE_PRIVATE);
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
		manageWifi = preferences.getBoolean("wifi_preference", false);
		manageBluetooth = preferences.getBoolean("bluetooth_preference", false);
		manageRinger = preferences.getBoolean("ringer_preference", false);
		manageAlarm = preferences.getBoolean("alarm_preference", false);
		alarmTime = preferences.getString("alarm_time_preference", null);
	}
}
