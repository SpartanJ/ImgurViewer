package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.StringUtils;

public class Tube8Service extends RedTubeService
{
	@Override
	public String getDomain()
	{
		return "tube8.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "\"quality_2160p\":\"", "\"quality_1440p\":\"", "\"quality_1080p\":\"", "\"quality_720p\":\"", "\"quality_480p\":\"", "\"quality_240p\":\"", "\"quality_180p\":\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\"," };
	}
	
	protected String getStringMatch( String haystack, String needleStart, String needleEnds )
	{
		String match = StringUtils.getStringMatch( haystack, needleStart, needleEnds );
		
		if ( "false".equals( match ) )
		{
			return "";
		}
		
		return match;
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
