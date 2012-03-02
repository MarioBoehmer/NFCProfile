/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile.actionbar;

import android.app.ActionBar;
import android.app.Activity;

/**
 * An extension of {@link ActionBarHelper} that provides Android 3.0-specific
 * functionality for Honeycomb tablets. It thus requires API level 11.
 * 
 * @author Mario Boehmer
 */
public class ActionBarHelperHoneycomb extends ActionBarHelper {

	protected ActionBarHelperHoneycomb(Activity activity,
			boolean showHomeBackButton) {
		super(activity);
		ActionBar actionBar = activity.getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(showHomeBackButton);
		}
	}
}