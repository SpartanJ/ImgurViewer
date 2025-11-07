package com.ensoft.imgurviewer.view.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;

public class ViewHelper
{
	private static boolean hasImmersive;
	private static boolean cachedImmersive = false;

	@SuppressLint( { "NewApi", "Deprecated" } )
	public static boolean hasImmersive( Context ctx )
	{
		if ( !cachedImmersive )
		{
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ) // Android 11+
			{
				WindowManager windowManager = (WindowManager) ctx.getSystemService( Context.WINDOW_SERVICE );
				if ( windowManager != null )
				{
					WindowInsets windowInsets = windowManager.getCurrentWindowMetrics().getWindowInsets();
					// Using systemBars() to detect if any system bar is present, which is closer
					// to the original logic's behavior.
					Insets insets = windowInsets.getInsetsIgnoringVisibility( WindowInsets.Type.systemBars() );
					hasImmersive = insets.top > 0 || insets.bottom > 0 || insets.left > 0 || insets.right > 0;
				}
				else
				{
					hasImmersive = false; // Fallback
				}
			}
			else
			{
				if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT )
				{
					hasImmersive = false;
					cachedImmersive = true;
					return false;
				}

				WindowManager windowManager = (WindowManager) ctx.getSystemService( Context.WINDOW_SERVICE );
				if (windowManager == null) {
					hasImmersive = false;
					cachedImmersive = true;
					return false;
				}

				Display d = windowManager.getDefaultDisplay();

				DisplayMetrics realDisplayMetrics = new DisplayMetrics();
				d.getRealMetrics( realDisplayMetrics );

				int realHeight = realDisplayMetrics.heightPixels;
				int realWidth = realDisplayMetrics.widthPixels;

				DisplayMetrics displayMetrics = new DisplayMetrics();
				d.getMetrics( displayMetrics );

				int displayHeight = displayMetrics.heightPixels;
				int displayWidth = displayMetrics.widthPixels;

				hasImmersive = ( realWidth > displayWidth ) || ( realHeight > displayHeight );
			}
			cachedImmersive = true;
		}

		return hasImmersive;
	}

	public static void setMargins( View v, int left, int top, int right, int bottom )
	{
		if ( null != v && v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams )
		{
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins( left, top, right, bottom );
			v.requestLayout();
		}
	}
}
