package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

public class Tube8Service extends RedTubeService
{
	@Override
	public String getDomain()
	{
		return "tube8.com";
	}
	
	@Override
	protected String parseReferer( Uri referer )
	{
		String url = referer.toString();
		
		if ( !url.contains( "/embed/" ) )
		{
			return url.replace( "tube8.com/", "tube8.com/embed/" );
		}
		
		return url;
	}
}
