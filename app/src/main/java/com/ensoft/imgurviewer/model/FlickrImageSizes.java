package com.ensoft.imgurviewer.model;

import android.net.Uri;

public class FlickrImageSizes
{
	protected FlickrImageSize[] size;
	
	public FlickrImageSize[] getSize()
	{
		return size;
	}
	
	public Uri getUri()
	{
		return size[ size.length - 1 ].getUri();
	}
	
	public Uri getThumbnailUri()
	{
		return size[ 0 ].getUri();
	}
}
