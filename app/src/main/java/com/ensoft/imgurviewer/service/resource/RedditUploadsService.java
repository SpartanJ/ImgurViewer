package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class RedditUploadsService extends MediaServiceSolver
{
	public static final String TAG = RedditUploadsService.class.getCanonicalName();
	public static final String REDDITUPLOADS_DOMAIN = "i.reddituploads.com";
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), uri );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( REDDITUPLOADS_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
