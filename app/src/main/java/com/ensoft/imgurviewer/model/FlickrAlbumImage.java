package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.ensoft.imgurviewer.App;

import java.net.URLDecoder;

public class FlickrAlbumImage
{
	public String title;
	
	public String description;
	
	public FlickrAlbumImageSizes sizes;
	
	public String getImage()
	{
		String url = null;
		
		ThumbnailSize thumbnailSize = App.getInstance().getPreferencesService().thumbnailSizeOnGallery();
		
		if ( thumbnailSize == ThumbnailSize.SMALL_SQUARE && null != sizes.sq )
			url = sizes.sq.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.BIG_SQUARE && null != sizes.q )
			url = sizes.q.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.SMALL_THUMBNAIL && null != sizes.s )
			url = sizes.s.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.MEDIUM_THUMBNAIL && null != sizes.m )
			url = sizes.m.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.LARGE_THUMBNAIL && null != sizes.l )
			url = sizes.l.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.HUGE_THUMBNAIL && null != sizes.h )
			url = sizes.h.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.FULL_IMAGE && null != sizes.k )
			url = sizes.k.displayUrl;
		
		return ( url != null ) ? "https:" + url : null;
	}
	
	public String getFullSizeImage()
	{
		return null != sizes.k ?  "https:" + sizes.k.displayUrl : null;
	}
	
	public Uri getThumbnail()
	{
		String url = null;
		
		if ( sizes.t != null )
			url = sizes.t.displayUrl;
		
		if ( null == url && sizes.s != null )
			url = sizes.s.displayUrl;
		
		if ( null == url && null != sizes.sq )
			url = sizes.sq.displayUrl;
		
		return null != url ? Uri.parse( "https:" + url ) : null;
	}
	
	public String getTitle()
	{
		try
		{
			return null != title ? URLDecoder.decode( title, "UTF-8" ) : null;
		}
		catch ( Exception ignored )
		{
			return null;
		}
	}
	
	public String getDescription()
	{
		try
		{
			return null != description ? URLDecoder.decode( description, "UTF-8" ) : null;
		}
		catch ( Exception ignored )
		{
			return null;
		}
	}
}
