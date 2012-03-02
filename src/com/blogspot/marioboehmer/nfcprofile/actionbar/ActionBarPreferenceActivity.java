/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile.actionbar;

import com.blogspot.marioboehmer.nfcprofile.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActionBarHelper}.
 * 
 * @author Mario Boehmer
 */
public abstract class ActionBarPreferenceActivity extends PreferenceActivity {
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
		mActionBarHelper = ActionBarHelper.createInstance(this, false);
		mActionBarHelper.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActionBarHelper.onPostCreate(savedInstanceState);
	}

	/**
	 * Base action bar-aware implementation for
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * 
	 * Note: marking menu items as invisible/visible is not currently supported.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retValue = false;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		mActionBarHelper.onCreateOptionsMenu(menu);
		retValue |= super.onCreateOptionsMenu(menu);
		return retValue;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mActionBarHelper.onOptionsItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	/** {@inheritDoc} */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		mActionBarHelper.onTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}
}
