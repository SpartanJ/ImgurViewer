package com.ensoft.imgurviewer.view.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class ViewHelper
{
	private static boolean hasImmersive;
	private static boolean cachedImmersive = false;
	
	@SuppressLint( "NewApi" )
	public static boolean hasImmersive( Context ctx )
	{
		if ( !cachedImmersive )
		{
			if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT )
			{
				hasImmersive = false;
				cachedImmersive = true;
				return false;
			}
			
			Display d = ( (WindowManager) ctx.getSystemService( Context.WINDOW_SERVICE ) ).getDefaultDisplay();
			
			DisplayMetrics realDisplayMetrics = new DisplayMetrics();
			d.getRealMetrics( realDisplayMetrics );
			
			int realHeight = realDisplayMetrics.heightPixels;
			int realWidth = realDisplayMetrics.widthPixels;
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			d.getMetrics( displayMetrics );
			
			int displayHeight = displayMetrics.heightPixels;
			int displayWidth = displayMetrics.widthPixels;
			
			hasImmersive = ( realWidth > displayWidth ) || ( realHeight > displayHeight );
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
