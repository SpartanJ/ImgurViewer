package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.YouPornVideo;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

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
		return new String[] { "\"quality_1080p\":\"", "\"quality_720p\":\"", "\"quality_480p\":\"", "\"quality_240p\":\"", "\"quality_180p\":\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\"," };
	}
	
	@Override
	protected String getStringMatch( String haystack, String needleStart, String needleEnds )
	{
		String match = StringUtils.getLastStringMatch( haystack, needleStart, needleEnds );
		
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
	
	@Override
	protected VideoPathSolved getVideoPathSolved()
	{
		return ( uri, pathResolverListener ) -> sendPathResolved( pathResolverListener, uri, UriUtils.guessMediaTypeFromUri( uri ), uri );
	}
}
