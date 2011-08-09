/*   Copyright 2011 Mario Böhmer
 *
 *   Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) 
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://creativecommons.org/licenses/by-nc-sa/3.0/
 */
package com.blogspot.marioboehmer.nfcprofile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * {@link OutlinedTextView} is a custom {@link TextView} element which draws the
 * text with an outline.
 * 
 * @author Mario Boehmer
 */
public class OutlinedTextView extends TextView {

	private float xTextOffset;
	private float yTextOffset;
	private Paint mTextPaint;
	private Paint mTextPaintOutline;
	private String text;

	public OutlinedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initTextViewOutline();
	}

	public OutlinedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTextViewOutline();
	}

	public OutlinedTextView(Context context) {
		super(context);
		initTextViewOutline();
	}

	private void initTextViewOutline() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(super.getTextSize());
		mTextPaint.setColor(getContext().getResources().getColor(
				R.color.light_blue));
		mTextPaint.setStyle(Paint.Style.FILL);

		mTextPaintOutline = new Paint();
		mTextPaintOutline.setAntiAlias(true);
		mTextPaintOutline.setTextSize(super.getTextSize());
		mTextPaintOutline.setColor(getContext().getResources().getColor(
				R.color.white));
		mTextPaintOutline.setStyle(Paint.Style.STROKE);
		mTextPaintOutline.setStrokeWidth(4);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		text = super.getText().toString();
		xTextOffset = (getWidth() / 2) - (getPaint().measureText(text) / 2);
		yTextOffset = (getHeight() / 2) - (getPaint().ascent() / 2);
		canvas.drawText(text, xTextOffset, yTextOffset, mTextPaintOutline);
		canvas.drawText(text, xTextOffset, yTextOffset, mTextPaint);
	}
}
