package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class GenericServiceSolver extends MediaServiceSolver
{
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), null );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.isVideoUrl( uri ) || UriUtils.isAudioUrl( uri ) ||
			UriUtils.isImageUrl( uri );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
