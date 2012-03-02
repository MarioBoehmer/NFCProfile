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

/**
 * An extension of {@link ActionBarHelper} that provides Android 4.0-specific
 * functionality for IceCreamSandwich devices. It thus requires API level 14.
 * 
 * @author Mario Boehmer
 */
public class ActionBarHelperICS extends ActionBarHelperHoneycomb {
	protected ActionBarHelperICS(Activity activity, boolean showHomeBackButton) {
		super(activity, showHomeBackButton);
	}
}