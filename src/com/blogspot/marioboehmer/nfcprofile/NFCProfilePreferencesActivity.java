/*   Copyright 2011 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * {@link NFCProfilePreferencesActivity} displays the preferences which can be
 * configured to be switched on NFC tag detection.
 * 
 * @author Mario Boehmer
 */
public class NFCProfilePreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.nfc_preferences);
		setContentView(R.layout.custom_preference_layout);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter();
				if (nfcAdapter != null) {
					return nfcAdapter.isEnabled();
				}
				return false;
			}

			protected void onPostExecute(Boolean enabled) {
				if (!enabled) {
					AlertDialog.Builder nfcDisabledDialogBuilder = new AlertDialog.Builder(
							NFCProfilePreferencesActivity.this);
					nfcDisabledDialogBuilder
							.setMessage(R.string.nfc_disabled_message);
					nfcDisabledDialogBuilder.setCancelable(true);
					OnClickListener onClicklistener = new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Intent systemSettingsIntent = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(systemSettingsIntent);
							}
						}
					};
					nfcDisabledDialogBuilder.setPositiveButton(R.string.yes,
							onClicklistener);
					nfcDisabledDialogBuilder.setNegativeButton(R.string.no,
							onClicklistener);
					nfcDisabledDialogBuilder.show();
				}
			};

		}.execute((Void[]) null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.feedback) {
			Intent feedbackMailIntent = new Intent(Intent.ACTION_SEND);
			feedbackMailIntent.setType("plain/text");
			feedbackMailIntent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { getString(R.string.feedback_mail_address) });
			feedbackMailIntent.putExtra(Intent.EXTRA_SUBJECT,
					getString(R.string.feedback));
			feedbackMailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n"
					+ getSystemInfo());
			startActivity(feedbackMailIntent);
		} else if (item.getItemId() == R.id.info) {
			Intent infoTextIntent = new Intent(getApplicationContext(),
					InfoActivity.class);
			startActivity(infoTextIntent);
		}
		return true;
	}

	private String getSystemInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("---------- System Info ----------");
		builder.append("\n");
		builder.append("OS Version: ");
		builder.append(Build.VERSION.RELEASE);
		builder.append("\n");
		builder.append("OS Api Level: ");
		builder.append(Build.VERSION.SDK_INT);
		builder.append("\n");
		builder.append("Manufacturer: ");
		builder.append(Build.MANUFACTURER);
		builder.append("\n");
		builder.append("Model: ");
		builder.append(Build.MODEL);
		builder.append("\n");
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_ACTIVITIES);
			builder.append("App VersionCode: ");
			builder.append(packageInfo.versionCode);
			builder.append("\n");
			builder.append("App VersionName: ");
			builder.append(packageInfo.versionName);
			builder.append("\n");
		} catch (Exception e) {
		}
		builder.append("---------- System Info ----------");
		return builder.toString();
	}
}
