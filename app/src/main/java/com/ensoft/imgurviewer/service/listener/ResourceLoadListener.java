package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

public abstract class ResourceLoadListener
{
	public abstract void loadVideo( Uri uri, Uri referer );
	
	public abstract void loadImage( Uri uri, Uri thumbnail );
	
	public abstract void loadAlbum( Uri uri, Class<?> view );
}
