package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DeviceService
{
	public int getScreenOrientation( Context context )
	{
		WindowManager windowManager = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE );
		Display display = windowManager.getDefaultDisplay();

		Point size = new Point();
		display.getSize( size );

		int orientation;

		if( size.x <= size.y )
		{
			orientation = Configuration.ORIENTATION_PORTRAIT;
		}
		else
		{
			orientation = Configuration.ORIENTATION_LANDSCAPE;
		}

		return orientation;
	}

	public boolean isLandscapeOrientation( Context context )
	{
		return getScreenOrientation( context ) == Configuration.ORIENTATION_LANDSCAPE;
	}
}
