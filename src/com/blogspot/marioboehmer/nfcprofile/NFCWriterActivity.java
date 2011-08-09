/*   Copyright 2011 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * {@link NFCWriterActivity} writes an URI Intent trigger for the NFC profile to
 * a NFC tag.
 * 
 * @author Mario Boehmer
 */
public class NFCWriterActivity extends Activity {

	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private NfcAdapter mAdapter;
	private PendingIntent pendingIntent;
	private static final String URI = "nfcprofile://com.blogspot.marioboehmer/dayandnight";
	private TextView nfcWriterMessage;
	private ProgressBar progressbar;
	private static final byte NO_PREFIX = 0x00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_writer_layout);
		nfcWriterMessage = (TextView) findViewById(R.id.nfc_writer_message);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		intentFiltersArray = new IntentFilter[] { ndef };
		techListsArray = new String[][] { new String[] { Ndef.class.getName() } };
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			finish();
		}
	}

	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
		progressbar.setVisibility(View.GONE);
	}

	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, pendingIntent,
				intentFiltersArray, techListsArray);
		progressbar.setVisibility(View.VISIBLE);
		nfcWriterMessage.setText(R.string.nfc_writer_waiting);
	}

	public void onNewIntent(Intent intent) {
		new AsyncTask<Intent, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Intent... params) {
				boolean success = false;
				try {
					success = writeUriToTag(params[0], URI);
				} catch (IOException e) {
				} catch (FormatException e) {
				}
				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);
				progressbar.setVisibility(View.GONE);
				MediaPlayer mediaPlayer = null;
				if (success) {
					mediaPlayer = MediaPlayer.create(NFCWriterActivity.this,
							R.raw.success);
					nfcWriterMessage.setText(R.string.nfc_writer_success);
				} else {
					mediaPlayer = MediaPlayer.create(NFCWriterActivity.this,
							R.raw.error);
					nfcWriterMessage.setText(R.string.nfc_writer_error);
				}
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.start();
				new Handler().postDelayed(finishRunnable, 3000);
			}
		}.execute(intent);

	}

	Runnable finishRunnable = new Runnable() {

		@Override
		public void run() {
			NFCWriterActivity.this.finish();
		}
	};

	private boolean writeUriToTag(Intent intent, String uri)
			throws IOException, FormatException {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Ndef ndef = Ndef.get(tag);
			final byte[] data = concatByteArrays(new byte[] { NO_PREFIX },
					uri.getBytes(Charset.forName("UTF_8")));
			NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], data);
			try {
				NdefRecord[] records = { record };
				NdefMessage message = new NdefMessage(records);
				ndef.connect();
				ndef.writeNdefMessage(message);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	private byte[] concatByteArrays(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
