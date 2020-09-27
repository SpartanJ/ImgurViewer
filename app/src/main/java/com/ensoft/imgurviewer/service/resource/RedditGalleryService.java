package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class RedditGalleryService extends MediaServiceSolver
{
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return ( "reddit.com".equals( uri.getHost() ) || "www.reddit.com".equals( uri.getHost() ) ) &&
			uri.getPathSegments().size() >= 2 && uri.getPathSegments().get( 0 ).equals( "gallery" );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return true;
	}
}
