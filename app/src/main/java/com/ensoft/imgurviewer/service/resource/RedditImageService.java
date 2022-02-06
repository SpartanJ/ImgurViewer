package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class RedditImageService extends MediaServiceSolver
{
	public static final String REDDIT_IMAGE_DOMAIN = "i.redd.it";
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), uri );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( REDDIT_IMAGE_DOMAIN ) &&
			null != uri.getLastPathSegment() && (
				( uri.getLastPathSegment().endsWith( ".gif" ) && !"mp4".equals( uri.getQueryParameter( "format" ) ) ) ||
				uri.getLastPathSegment().endsWith( ".jpg" ) || uri.getLastPathSegment().endsWith( ".png" ) ||
				uri.getLastPathSegment().endsWith( ".jpeg" ) || uri.getLastPathSegment().endsWith( ".webp" )
		);
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
