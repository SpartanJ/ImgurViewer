package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

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
	
	public static boolean isAudioUrl( Uri uri )
	{
		if ( null != uri )
		{
			List<String> pathSegments = uri.getPathSegments();
			
			if ( null != pathSegments && pathSegments.size() > 0 )
			{
				String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
				
				return lastPathSegment.endsWith( ".ogg" ) ||
					lastPathSegment.endsWith( ".mp3" ) ||
					lastPathSegment.endsWith( ".wav" ) ||
					lastPathSegment.endsWith( ".3gp" ) ||
					lastPathSegment.endsWith( ".m4a" ) ||
					lastPathSegment.endsWith( ".opus" ) ||
					lastPathSegment.endsWith( ".wma" ) ||
					lastPathSegment.endsWith( ".flac" );
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
	
	public static boolean isContentUri( Uri uri )
	{
		return "content".equals( uri.getScheme() );
	}
	
	public static boolean isFileUri( Uri uri )
	{
		return "file".equals( uri.getScheme() );
	}
	
	public static Uri contentUriToFileUri( Context context, @NonNull Uri uri )
	{
		String filePath = null;
		
		try
		{
			if ( isContentUri( uri ) )
			{
				Cursor cursor = context.getContentResolver().query( uri, new String[]{ android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null );
				
				if ( null != cursor && cursor.moveToFirst() )
				{
					filePath = "file://" + cursor.getString( 0 );
					
					cursor.close();
				}
			}
			else
			{
				filePath = "file://" + uri.getPath();
			}
			
			return Uri.parse( filePath );
		}
		catch ( Exception e )
		{
			Log.e( UriUtils.class.getCanonicalName(), "contentUriToFileUri: " + e.toString() );
		}
		
		return null;
	}
}
