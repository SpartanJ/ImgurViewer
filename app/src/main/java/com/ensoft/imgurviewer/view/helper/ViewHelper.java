package com.ensoft.imgurviewer.view.helper;

import android.view.View;
import android.view.ViewGroup;

public class ViewHelper
{
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
