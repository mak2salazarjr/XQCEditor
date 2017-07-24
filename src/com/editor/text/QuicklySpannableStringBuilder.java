package com.editor.text;

import android.text.SpannableStringBuilder;
import android.util.Log;

public class QuicklySpannableStringBuilder extends SpannableStringBuilder {
	protected static final String TAG="QuicklySpannableStringBuilder";
	
	public QuicklySpannableStringBuilder(CharSequence source) {
		super(source);
	}
	
	@Override
	public SpannableStringBuilder replace(int start, int end, CharSequence tb,
			int tbstart, int tbend) {
		long timeStart = System.currentTimeMillis();
		
		SpannableStringBuilder builder = super.replace(start, end, tb, tbstart, tbend);
		Log.i(TAG, "replace used:"+(System.currentTimeMillis() - timeStart));
		return builder;
	}
}
