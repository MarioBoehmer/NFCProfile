/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile.actionbar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.blogspot.marioboehmer.nfcprofile.InfoActivity;
import com.blogspot.marioboehmer.nfcprofile.NFCWriterActivity;
import com.blogspot.marioboehmer.nfcprofile.R;

/**
 * This helper class is responsible for loading the correct ActionBarActivity
 * for the corresponding android version.
 * 
 * Three implementations of this class are {@link ActionBarHelperBase} for a
 * pre-Honeycomb version of the action bar, and {@link ActionBarHelperHoneycomb}
 * and {@link ActionBarHelperICS}, which uses the built-in ActionBar features in
 * Android 3.0 and later.
 * 
 * @author Mario Boehmer
 */
public abstract class ActionBarHelper {
	protected Activity mActivity;

	/**
	 * Factory method for creating {@link ActionBarHelper} objects for a given
	 * activity. Depending on which device the app is running, either a basic
	 * helper or Honeycomb-specific or ICS-specific helper will be returned.
	 */
	public static ActionBarHelper createInstance(Activity activity,
			boolean showHomeBackButton) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return new ActionBarHelperICS(activity, showHomeBackButton);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return new ActionBarHelperHoneycomb(activity, showHomeBackButton);
		} else {
			return new ActionBarHelperBase(activity);
		}
	}

	protected ActionBarHelper(Activity activity) {
		mActivity = activity;
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onCreate(android.os.Bundle)}.
	 */
	public void onCreate(Bundle savedInstanceState) {
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onPostCreate(android.os.Bundle)}.
	 */
	public void onPostCreate(Bundle savedInstanceState) {
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.write_to_tag) {
			Intent writeToTagIntent = new Intent(mActivity,
					NFCWriterActivity.class);
			mActivity.startActivity(writeToTagIntent);
			return true;
		} else if (item.getItemId() == R.id.feedback) {
			try {
				Intent feedbackMailIntent = new Intent(Intent.ACTION_SEND);
				feedbackMailIntent.setType("plain/text");
				feedbackMailIntent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { mActivity
								.getString(R.string.feedback_mail_address) });
				feedbackMailIntent.putExtra(Intent.EXTRA_SUBJECT,
						mActivity.getString(R.string.feedback));
				feedbackMailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n"
						+ getSystemInfo());
				mActivity.startActivity(feedbackMailIntent);
			} catch (ActivityNotFoundException e) {
				// dismiss if no app can handle intent
			}
			return true;
		} else if (item.getItemId() == R.id.info) {
			Intent infoTextIntent = new Intent(
					mActivity.getApplicationContext(), InfoActivity.class);
			mActivity.startActivity(infoTextIntent);
			return true;
		}
		return false;
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
			PackageInfo packageInfo = mActivity.getPackageManager()
					.getPackageInfo(mActivity.getPackageName(),
							PackageManager.GET_ACTIVITIES);
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

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onTitleChanged(CharSequence, int)}.
	 */
	protected void onTitleChanged(CharSequence title, int color) {
	}

	/**
	 * Returns a {@link MenuInflater} for use when inflating menus. The
	 * implementation of this method in {@link ActionBarHelperBase} returns a
	 * wrapped menu inflater that can read action bar metadata from a menu
	 * resource pre-Honeycomb.
	 */
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return superMenuInflater;
	}
}