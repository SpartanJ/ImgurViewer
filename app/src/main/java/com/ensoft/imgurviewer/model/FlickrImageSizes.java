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
		for ( int i = size.length - 1; i >= 0; i-- )
		{
			FlickrImageSize current = size[i];
			
			if ( "photo".equals( current.getMedia() ) )
				return current.getUri();
		}
		
		return Uri.EMPTY;
	}
	
	public Uri getThumbnailUri()
	{
		return size[ 0 ].getUri();
	}
}
