package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

import java.util.List;

public abstract class MediaServiceSolver
{
	public abstract void getPath( Uri uri, final PathResolverListener pathResolverListener );
	
	public abstract boolean isServicePath( Uri uri );
	
	public boolean isVideo( Uri uri )
	{
		return UriUtils.isVideoUrl( uri );
	}
	
	public boolean isVideo( String uri )
	{
		return UriUtils.isVideoUrl( uri );
	}
	
	public abstract boolean isGallery( Uri uri );
}
