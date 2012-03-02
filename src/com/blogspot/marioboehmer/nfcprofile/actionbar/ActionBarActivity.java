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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.blogspot.marioboehmer.nfcprofile.NFCProfilePreferencesActivity;
import com.blogspot.marioboehmer.nfcprofile.R;

/**
 * {@link ActionBarActivity} delegates common functionality to a helper class
 * which refers to the correct android version implementation. It is used for
 * all activities other than the preference {@link Activity} which extends
 * another special activity.
 * 
 * @author Mario Boehmer
 */
public abstract class ActionBarActivity extends Activity {
	private ActionBarHelper mActionBarHelper;

	protected ActionBarHelper getActionBarHelper() {
		return mActionBarHelper;
	}

	/** {@inheritDoc} */
	@Override
	public MenuInflater getMenuInflater() {
		return mActionBarHelper.getMenuInflater(super.getMenuInflater());
	}

	/** {@inheritDoc} */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mActionBarHelper = ActionBarHelper.createInstance(this, true);
		mActionBarHelper.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActionBarHelper.onPostCreate(savedInstanceState);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retValue = false;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		mActionBarHelper.onCreateOptionsMenu(menu);
		menu.findItem(R.id.write_to_tag).setVisible(false);
		retValue |= super.onCreateOptionsMenu(menu);
		return retValue;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!mActionBarHelper.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this,
						NFCProfilePreferencesActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/** {@inheritDoc} */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		mActionBarHelper.onTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}
}
