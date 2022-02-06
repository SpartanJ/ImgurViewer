package com.ensoft.imgurviewer.service;

import com.ensoft.imgurviewer.App;
import com.facebook.drawee.drawable.ScalingUtils;

public class ScaleTypeUtils
{
	public static ScalingUtils.ScaleType fromString( String scaleType )
	{
		switch ( scaleType )
		{
			case "FIT_XY": return ScalingUtils.ScaleType.FIT_XY;
			case "FIT_X": return ScalingUtils.ScaleType.FIT_X;
			case "FIT_Y": return ScalingUtils.ScaleType.FIT_Y;
			case "FIT_START": return ScalingUtils.ScaleType.FIT_START;
			case "FIT_END": return ScalingUtils.ScaleType.FIT_END;
			case "CENTER": return ScalingUtils.ScaleType.CENTER;
			case "CENTER_INSIDE": return ScalingUtils.ScaleType.CENTER_INSIDE;
			case "CENTER_CROP": return ScalingUtils.ScaleType.CENTER_CROP;
			case "FOCUS_CROP": return ScalingUtils.ScaleType.FOCUS_CROP;
			case "FIT_BOTTOM_START": return ScalingUtils.ScaleType.FIT_BOTTOM_START;
			case "FIT_CENTER": default: return ScalingUtils.ScaleType.FIT_CENTER;
		}
	}
	
	public static ScalingUtils.ScaleType getListViewImageScaleType()
	{
		return fromString( App.getInstance().getPreferencesService().getListViewImageScaleType() );
	}
	
	public static ScalingUtils.ScaleType getGridViewImageScaleType()
	{
		return fromString( App.getInstance().getPreferencesService().getGridViewImageScaleType() );
	}
}
