/*   Copyright 2012 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

import com.blogspot.marioboehmer.nfcprofile.actionbar.ActionBarActivity;

/**
 * {@link InfoActivity} for displaying application information about used
 * resources etc.
 * 
 * @author Mario Boehmer
 */
public class InfoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);
		((TextView) findViewById(R.id.info_text)).setText(Html
				.fromHtml(getString(R.string.info_text)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.info).setVisible(false);
		return true;
	}
}
