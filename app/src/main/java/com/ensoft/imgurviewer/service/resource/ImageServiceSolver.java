package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.PathResolverListener;

import java.util.List;

public abstract class ImageServiceSolver
{
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
					( lastPathSegment.endsWith( ".gif" ) && uri.toString().contains( "fm=mp4" ) );
			}
		}
		
		return false;
	}
	
	public static boolean isVideoUrl( String uri )
	{
		return isVideoUrl( Uri.parse( uri ) );
	}
	
	public abstract void getPath( Uri uri, final PathResolverListener pathResolverListener );
	
	public abstract boolean isServicePath( Uri uri );
	
	public boolean isVideo( Uri uri )
	{
		return isVideoUrl( uri );
	}
	
	public boolean isVideo( String uri )
	{
		return isVideoUrl( uri );
	}
	
	public abstract boolean isGallery( Uri uri );
}
