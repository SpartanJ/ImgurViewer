package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public abstract class ImageServiceSolver
{
	public abstract void getPath( Uri uri, final PathResolverListener pathResolverListener );

	public abstract boolean isServicePath( Uri uri );

	public boolean isVideo( Uri uri )
	{
		return isVideo( uri.toString() );
	}

	public boolean isVideo( String uri )
	{
		return uri.endsWith( ".gifv" ) || uri.endsWith( ".mp4" ) || uri.endsWith( ".avi" ) || uri.endsWith( ".flv" ) || uri.endsWith( ".mkv" ) || uri.endsWith( ".webm" );
	}
}
