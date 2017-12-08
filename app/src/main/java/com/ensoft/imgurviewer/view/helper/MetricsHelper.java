package com.ensoft.imgurviewer.view.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class MetricsHelper
{
	public static int dpToPx( Context context, float dpSize )
	{
		return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dpSize, context.getResources().getDisplayMetrics() );
	}
	
	public static int getNavigationBarWidth( Context context )
	{
		if ( !ViewHelper.hasImmersive( context ) )
		{
			return 0;
		}
		
		Resources resources = context.getResources();
		
		int resourceId = resources.getIdentifier( "navigation_bar_width", "dimen", "android" );
		
		if ( resourceId > 0 )
		{
			return resources.getDimensionPixelSize( resourceId );
		}
		
		return 0;
	}
	
	public static int getNavigationBarHeight( Context context )
	{
		if ( !ViewHelper.hasImmersive( context ) )
		{
			return 0;
		}
		
		Resources resources = context.getResources();
		
		int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
		
		if ( resourceId > 0 )
		{
			return resources.getDimensionPixelSize( resourceId );
		}
		
		return 0;
	}
	
	public static int getStatusBarHeight( Context context )
	{
		Resources resources = context.getResources();
		
		int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
		
		if ( resourceId > 0 )
		{
			return resources.getDimensionPixelSize( resourceId );
		}
		
		return 0;
	}
}
