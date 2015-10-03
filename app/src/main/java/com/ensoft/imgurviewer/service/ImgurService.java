package com.ensoft.imgurviewer.service;

import android.net.Uri;

public class ImgurService
{
	protected String getPath( String scheme, String fullPath )
	{
		if ( fullPath.startsWith( "imgur.com" ) )
		{
			fullPath = fullPath.replace( "imgur.com", "i.imgur.com" );
		}

		String url = scheme + ":" + fullPath; //combine to get a full URI

		if ( url.endsWith( ".gif" ) || url.endsWith( ".gifv" ) )
		{
			url = url.replace( ".gifv", ".mp4" );
			url = url.replace( ".gif", ".mp4" );
		}

		if ( !url.endsWith( ".png" ) && !url.endsWith( ".jpg" ) && !url.endsWith( ".jpeg" ) && !url.endsWith( ".mp4" ) )
		{
			url += ".jpg";
		}

		return url;
	}

	public String getPath( Uri uri )
	{
		return getPath( uri.getScheme(), uri.getEncodedSchemeSpecificPart() );
	}

	public Uri getPathUri( Uri uri )
	{
		return Uri.parse( getPath( uri ) );
	}

	public boolean isVideo( Uri uri )
	{
		return isVideo( uri.toString() );
	}

	public boolean isVideo( String uri )
	{
		return uri.endsWith( ".gifv" ) || uri.endsWith( ".mp4" ) || uri.endsWith( ".avi" ) || uri.endsWith( ".flv" ) || uri.endsWith( ".mkv" );
	}
}
