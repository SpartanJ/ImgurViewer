package com.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
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

	public static int getNavigationBarHeight( Context context )
	{
		Resources resources = context.getResources();

		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

		if ( resourceId > 0 )
		{
			return resources.getDimensionPixelSize(resourceId);
		}

		return 0;
	}

	public static int getStatusBarHeight( Context context )
	{
		Resources resources = context.getResources();

		int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );

		if ( resourceId > 0 )
		{
			return resources.getDimensionPixelSize(resourceId);
		}

		return 0;
	}
}
