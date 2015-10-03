package com.helpers;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class MetricsHelper
{
	public static int dpToPx( Context context, float dpSize )
	{
		return (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dpSize, context.getResources().getDisplayMetrics() );
	}

	public static Point getDisplaySize( WindowManager windowManager )
	{
		Point windowSize = new Point();

		if ( null != windowManager )
			windowManager.getDefaultDisplay().getSize(windowSize);

		return windowSize;
	}

	public static String getDensityAbbr( Context context )
	{
		switch ( context.getResources().getDisplayMetrics().densityDpi )
		{
			case DisplayMetrics.DENSITY_LOW: return "ldpi";
			case DisplayMetrics.DENSITY_MEDIUM: return "mdpi";
			case DisplayMetrics.DENSITY_HIGH: return "hdpi";
			case DisplayMetrics.DENSITY_XHIGH: return "xhdpi";
			case DisplayMetrics.DENSITY_XXHIGH: return "xxhdpi";
			case DisplayMetrics.DENSITY_XXXHIGH: return "xxxhdpi";
		}

		return "xhdpi";
	}

	public static String[] getDensities()
	{
		return new String[] { "ldpi", "mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi" };
	}
}
