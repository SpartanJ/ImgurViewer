package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.ensoft.imgurviewer.App;

import java.net.URLDecoder;

public class FlickrAlbumImage
{
	public String title;
	
	public String description;
	
	public FlickrAlbumImageSizesData sizes;
	
	public String getImage()
	{
		String url = null;
		
		ThumbnailSize thumbnailSize = App.getInstance().getPreferencesService().thumbnailSizeOnGallery();
		
		if ( thumbnailSize == ThumbnailSize.SMALL_SQUARE && null != sizes.data.sq )
			url = sizes.data.sq.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.BIG_SQUARE && null != sizes.data.q )
			url = sizes.data.q.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.SMALL_THUMBNAIL && null != sizes.data.s )
			url = sizes.data.s.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.MEDIUM_THUMBNAIL && null != sizes.data.m )
			url = sizes.data.m.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.LARGE_THUMBNAIL && null != sizes.data.l )
			url = sizes.data.l.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.HUGE_THUMBNAIL && null != sizes.data.h )
			url = sizes.data.h.data.displayUrl;
		else if ( thumbnailSize == ThumbnailSize.FULL_IMAGE && null != sizes.data.k )
			url = sizes.data.k.data.displayUrl;
		
		return ( url != null ) ? "https:" + url : null;
	}
	
	public String getFullSizeImage()
	{
		return null != sizes.data.k ?  "https:" + sizes.data.k.data.displayUrl : null;
	}
	
	public Uri getThumbnail()
	{
		String url = null;
		
		if ( sizes.data.t != null )
			url = sizes.data.t.data.displayUrl;
		
		if ( null == url && sizes.data.s != null )
			url = sizes.data.s.data.displayUrl;
		
		if ( null == url && null != sizes.data.sq )
			url = sizes.data.sq.data.displayUrl;
		
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
