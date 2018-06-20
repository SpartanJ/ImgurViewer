package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.text.TextUtils;

import com.ensoft.imgurviewer.model.MediaType;

import java.util.List;

public class UriUtils
{
	public static Uri getUriMatch( String haystack, String needleStart, String needleEnds )
	{
		String uriString = StringUtils.getStringMatch( haystack, needleStart, needleEnds );
		
		return !TextUtils.isEmpty( uriString ) ? Uri.parse( uriString ) : null;
	}
	
	public static boolean uriMatchesDomain( Uri uri, String domain )
	{
		return uriMatchesDomain( uri, domain, "" );
	}
	
	public static boolean uriMatchesDomain( Uri uri, String domain, String path )
	{
		String uriStr = uri.toString();
		
		return ( uriStr.startsWith( "https://" + domain + path ) ||
			uriStr.startsWith( "http://" + domain + path ) ||
			uriStr.startsWith( "https://www." + domain + path ) ||
			uriStr.startsWith( "http://www." + domain + path ) ||
			uriStr.startsWith( "http://m." + domain + path ) ||
			uriStr.startsWith( "https://m." + domain + path ) );
	}
	
	public static boolean isVideoUrl( Uri uri )
	{
		if ( null != uri )
		{
			List<String> pathSegments = uri.getPathSegments();
			
			if ( null != pathSegments && pathSegments.size() > 0 )
			{
				String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
				
				return lastPathSegment.endsWith( ".gifv" ) ||
					lastPathSegment.endsWith( ".mp4" ) ||
					lastPathSegment.endsWith( ".avi" ) ||
					lastPathSegment.endsWith( ".flv" ) ||
					lastPathSegment.endsWith( ".mkv" ) ||
					lastPathSegment.endsWith( ".webm" ) ||
					lastPathSegment.endsWith( ".m3u8" ) ||
					lastPathSegment.endsWith( ".mpd" ) ||
					lastPathSegment.endsWith( ".ism" ) ||
					( lastPathSegment.endsWith( ".gif" ) && uri.toString().contains( "fm=mp4" ) );
			}
		}
		
		return false;
	}
	
	public static boolean isVideoUrl( String uri )
	{
		return isVideoUrl( Uri.parse( uri ) );
	}
	
	public static MediaType guessMediaTypeFromUri( Uri uri )
	{
		List<String> pathSegments = null != uri ? uri.getPathSegments() : null;
		
		if ( null != pathSegments && pathSegments.size() > 0 )
		{
			String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
			
			if ( lastPathSegment.endsWith( ".m3u8" ) )
			{
				return MediaType.STREAM_HLS;
			}
			else if ( lastPathSegment.endsWith( ".mpd" ) )
			{
				return MediaType.STREAM_DASH;
			}
			else if ( lastPathSegment.endsWith( ".ism" ) )
			{
				return MediaType.STREAM_SS;
			}
		}
		
		return isVideoUrl( uri ) ? MediaType.VIDEO_MP4 : MediaType.IMAGE;
	}
}
