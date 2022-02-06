package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.model.MediaType;

public abstract class ResourceLoadListener
{
	public abstract void loadVideo( Uri uri, MediaType mediaType, Uri referer );
	
	public abstract void loadImage( Uri uri, Uri thumbnail );
	
	public abstract void loadAlbum( Uri uri, Class<?> view );
	
	public abstract void loadFailed( Uri uri, String error );
}
