/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.blogspot.marioboehmer.nfcprofile.profile.Profile;
import com.blogspot.marioboehmer.nfcprofile.profile.ProfileHelper;

/**
 * {@link NFCProfileManager} manages the switching of configured preferences
 * which are saved on tag on NFC tag detection.
 * 
 * @author Mario Boehmer
 */
public class NFCProfileManager extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_NO_TITLE, 0);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.R.color.transparent));
		// execute all settings
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String profileURLSuffix = getIntent().getData().getQuery();
				Profile profile = ProfileHelper
						.getProfileFromProfileURLSuffix(profileURLSuffix);
				// Airplane mode has preference over other wireless connections
				// so when activated others aren't toggled
				if (profile.isToggleAirplaneModeEnabled()) {
					boolean isAirplaneModeEnabled = Settings.System.getInt(
							NFCProfileManager.this.getContentResolver(),
							Settings.System.AIRPLANE_MODE_ON, 0) == 1;
					if (!isAirplaneModeEnabled) {
						Settings.System.putInt(
								NFCProfileManager.this.getContentResolver(),
								Settings.System.AIRPLANE_MODE_ON, 1);
					} else {
						Settings.System.putInt(
								NFCProfileManager.this.getContentResolver(),
								Settings.System.AIRPLANE_MODE_ON, 0);
					}
					Intent intent = new Intent(
							Intent.ACTION_AIRPLANE_MODE_CHANGED);
					intent.putExtra("state", !isAirplaneModeEnabled);
					sendBroadcast(intent);
				} else {
					if (profile.isToggleWifiEnabled()) {
						WifiManager wifimgr = (WifiManager) getSystemService(WIFI_SERVICE);
						if (wifimgr != null) {
							if (wifimgr.isWifiEnabled()) {
								wifimgr.setWifiEnabled(false);
							} else if (!wifimgr.isWifiEnabled()) {
								wifimgr.setWifiEnabled(true);
							}
						}
					}
					if (profile.isToggleBluetoothEnabled()) {
						BluetoothAdapter bluetoothAdapter = BluetoothAdapter
								.getDefaultAdapter();
						if (bluetoothAdapter != null) {
							if (bluetoothAdapter.isEnabled()) {
								bluetoothAdapter.disable();
							} else if (!bluetoothAdapter.isEnabled()) {
								bluetoothAdapter.enable();
							}
						}
					}
				}
				if (profile.isToggleRingtoneEnabled()) {
					AudioManager audiomgr = (AudioManager) getSystemService(AUDIO_SERVICE);
					if (audiomgr != null) {
						if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_SILENT)) {
							audiomgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						} else if ((audiomgr.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)) {
							audiomgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}
					}
				}
				if (profile.isSetAlarmEnabled()) {
					String alarmTime = profile.getAlarmTime();
					if (!TextUtils.isEmpty(alarmTime)
							&& alarmTime.contains("-")) {
						int hour = Integer.parseInt(alarmTime.split("-")[0]);
						int minutes = Integer.parseInt(alarmTime.split("-")[1]);
						Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
						i.putExtra(AlarmClock.EXTRA_MESSAGE, getResources()
								.getString(R.string.nfc_alarm_title));
						i.putExtra(AlarmClock.EXTRA_HOUR, hour);
						i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
						// don't use constant to support backwards compatibility
						i.putExtra("android.intent.extra.alarm.SKIP_UI", true);
						startActivity(i);
					}
				}
				if (profile.isStartExternalAppEnabled()) {
					String packageName = profile.getExternalAppPackageName();
					if (!TextUtils.isEmpty(packageName)
							&& !"0".equals(packageName)) {
						Intent i = getPackageManager()
								.getLaunchIntentForPackage(packageName);
						// intent could be null if app got deleted in the
						// meantime
						if (i != null) {
							startActivity(i);
						}
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				int toastId = R.string.nfc_profile_executed_notification;
				Toast.makeText(NFCProfileManager.this, toastId,
						Toast.LENGTH_SHORT).show();
				finish();
			};

		}.execute((Void) null);
	}
}
