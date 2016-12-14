package com.vas.vassdk.util;

import android.content.Context;
import android.util.TypedValue;

public class ScreenUtil {
	
	public static int toDip(Context context, float paramFloat) {
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paramFloat, context.getResources().getDisplayMetrics());
	}
	
	public static int toSp(Context context, float paramFloat) {
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, paramFloat, context.getResources().getDisplayMetrics());
	}
	
}