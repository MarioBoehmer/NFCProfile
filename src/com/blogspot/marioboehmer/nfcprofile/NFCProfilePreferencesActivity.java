/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.blogspot.marioboehmer.nfcprofile.actionbar.ActionBarPreferenceActivity;

/**
 * {@link NFCProfilePreferencesActivity} displays the preferences which can be
 * configured to be switched on NFC tag detection.
 * 
 * @author Mario Boehmer
 */
public class NFCProfilePreferencesActivity extends ActionBarPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.nfc_preferences);
		setContentView(R.layout.custom_preference_layout);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// check if NFC adapter is enabled
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				NfcAdapter nfcAdapter = NfcAdapter
						.getDefaultAdapter(getApplicationContext());
				if (nfcAdapter != null) {
					return nfcAdapter.isEnabled();
				}
				return false;
			}

			protected void onPostExecute(Boolean enabled) {
				if (!enabled) {
					CustomDialog.Builder nfcDisabledDialogBuilder = new CustomDialog.Builder(
							NFCProfilePreferencesActivity.this);
					TextView nfcDisabledMessage = new TextView(
							NFCProfilePreferencesActivity.this);
					nfcDisabledMessage.setTextAppearance(
							NFCProfilePreferencesActivity.this,
							R.style.text_medium);
					nfcDisabledMessage.setText(R.string.nfc_disabled_message);
					nfcDisabledMessage.setPadding(10, 10, 10, 10);
					nfcDisabledDialogBuilder.setContentView(nfcDisabledMessage);
					nfcDisabledDialogBuilder
							.setTitle(R.string.title_dialog_nfc_off);
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
					nfcDisabledDialogBuilder.create(
							R.layout.custom_dialog_layout).show();
				}
			};

		}.execute((Void) null);

		// populate app list for preference
		new AsyncTask<Void, Void, SingleChoiceListPreference>() {

			@Override
			protected SingleChoiceListPreference doInBackground(Void... params) {
				PackageManager packageManager = getPackageManager();
				SingleChoiceListPreference preference = (SingleChoiceListPreference) getPreferenceScreen()
						.findPreference("external_app_package_name");
				List<ApplicationInfo> applicationsInfos = packageManager
						.getInstalledApplications(PackageManager.GET_META_DATA);
				String[] applicationNames = new String[applicationsInfos.size()];
				String[] packageNames = new String[applicationsInfos.size()];
				for (int x = 0; x < applicationsInfos.size(); x++) {
					applicationNames[x] = applicationsInfos.get(x)
							.loadLabel(packageManager).toString();
					packageNames[x] = applicationsInfos.get(x).packageName;
				}
				preference.setEntries(applicationNames);
				preference.setEntryValues(packageNames);
				return preference;
			}

			protected void onPostExecute(SingleChoiceListPreference result) {
				result.init();
			};

		}.execute((Void) null);

		new AsyncTask<Void, Void, Boolean>() {

			private final static String ASK_FOR_RATING = "askForRating";

			@Override
			protected Boolean doInBackground(Void... params) {
				SharedPreferences sharedPreferences = getSharedPreferences(
						getString(R.string.shared_preferences_name),
						MODE_PRIVATE);
				int tagWrittenCount = sharedPreferences
						.getInt(getString(R.string.tag_written_count_preference_key),
								0);
				boolean askForRating = sharedPreferences.getBoolean(
						ASK_FOR_RATING, true);
				if (tagWrittenCount >= 3 && askForRating) {
					return true;
				} else {
					return false;
				}
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					CustomDialog.Builder askForRatingDialogBuilder = new CustomDialog.Builder(
							NFCProfilePreferencesActivity.this);
					askForRatingDialogBuilder
							.setTitle(R.string.title_dialog_ask_for_rating);
					OnClickListener onClickListener = new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							SharedPreferences sharedPreferences = getSharedPreferences(
									getString(R.string.shared_preferences_name),
									MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();
							editor.putBoolean(ASK_FOR_RATING, false);
							editor.commit();
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Intent marketIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("https://market.android.com/details?id=com.blogspot.marioboehmer.nfcprofile"));
								startActivity(marketIntent);
							}

						}
					};
					askForRatingDialogBuilder.setPositiveButton(R.string.yes,
							onClickListener);
					askForRatingDialogBuilder.setNegativeButton(R.string.no,
							onClickListener);
					TextView askForRatingMessage = new TextView(
							NFCProfilePreferencesActivity.this);
					askForRatingMessage.setTextAppearance(
							NFCProfilePreferencesActivity.this,
							R.style.text_medium);
					askForRatingMessage
							.setText(R.string.ask_for_rating_message);
					askForRatingMessage.setPadding(10, 10, 10, 10);
					askForRatingDialogBuilder
							.setContentView(askForRatingMessage);
					askForRatingDialogBuilder.create(
							R.layout.custom_dialog_layout).show();

				}
			};

		}.execute((Void) null);
	}
}
