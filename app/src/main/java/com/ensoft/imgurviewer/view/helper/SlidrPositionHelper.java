package com.ensoft.imgurviewer.view.helper;

import com.r0adkll.slidr.model.SlidrPosition;

public class SlidrPositionHelper
{
	public static SlidrPosition fromString( String position )
	{
		return SlidrPosition.values()[ Integer.valueOf( position ) ];
	}
}
