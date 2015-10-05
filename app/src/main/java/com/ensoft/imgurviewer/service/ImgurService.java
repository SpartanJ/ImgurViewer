package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.interfaces.ImgurPathResolverListener;

public class ImgurService
{
	public static final String TAG = ImgurService.class.getCanonicalName();
	public static final String IMGUR_DOMAIN = "imgur.com";
	public static final String IMGUR_IMAGE_DOMAIN = "i.imgur.com";
	public static final String IMGUR_MOBILE_DOMAIN = "m.imgur.com";
	public static final String IMGUR_API_URL = "https://api.imgur.com/3";

	protected void getFirstImage( final Uri url, final ImgurPathResolverListener imgurPathResolverListener )
	{
		new ImgurAlbumService().getAlbum( url, new ImgurAlbumResolverListener()
		{
			@Override
			public void onAlbumResolved( ImgurAlbum album )
			{
				Uri uri = Uri.parse( album.getImage( 0 ).getLink() );

				imgurPathResolverListener.onPathResolved( uri, getThumbnailPath( uri ) );
			}

			@Override
			public void onError( String error )
			{
				imgurPathResolverListener.onPathError( error );
			}
		} );
	}

	protected Uri processPath( Uri uri )
	{
		String url = uri.toString();

		if ( url.contains( "//" + IMGUR_DOMAIN ) )
		{
			url = url.replace( "//" + IMGUR_DOMAIN, "//" + IMGUR_IMAGE_DOMAIN );
		}
		else if ( url.contains( "//www." + IMGUR_DOMAIN ) )
		{
			url = url.replace( "//www." + IMGUR_DOMAIN, "//" + IMGUR_IMAGE_DOMAIN );
		}
		else if ( url.contains( "//" + IMGUR_MOBILE_DOMAIN ) )
		{
			url = url.replace( "//" + IMGUR_MOBILE_DOMAIN, "//" + IMGUR_IMAGE_DOMAIN );
		}

		if ( url.endsWith( ".gif" ) || url.endsWith( ".gifv" ) )
		{
			url = url.replace( ".gifv", ".mp4" );
			url = url.replace( ".gif", ".mp4" );
		}

		if ( !url.endsWith( ".png" ) && !url.endsWith( ".jpg" ) && !url.endsWith( ".jpeg" ) && !url.endsWith( ".mp4" ) )
		{
			url += ".jpg";
		}

		return Uri.parse( url );
	}

	public Uri getThumbnailPath( Uri uri )
	{
		String fixedUri = processPath( uri ).toString();

		int pos = fixedUri.lastIndexOf( "." );

		String path = fixedUri.substring( 0, pos );
		String ext = fixedUri.substring( pos );

		return Uri.parse( path + "s" + ext );
	}

	public void getPath( Uri uri, ImgurPathResolverListener imgurPathResolverListener )
	{
		if ( new ImgurAlbumService().isImgurAlbum( uri ) )
		{
			getFirstImage( uri, imgurPathResolverListener );
		}
		else
		{
			Uri fixedUri = processPath( uri );
			imgurPathResolverListener.onPathResolved( fixedUri, getThumbnailPath( fixedUri ) );
		}
	}

	public void getPathUri( Uri uri, ImgurPathResolverListener imgurPathResolverListener )
	{
		getPath( uri, imgurPathResolverListener );
	}

	public boolean isVideo( Uri uri )
	{
		return isVideo( uri.toString() );
	}

	public boolean isVideo( String uri )
	{
		return uri.endsWith( ".gifv" ) || uri.endsWith( ".mp4" ) || uri.endsWith( ".avi" ) || uri.endsWith( ".flv" ) || uri.endsWith( ".mkv" );
	}

	public boolean isImgurPath( Uri uri )
	{
		return uri.toString().contains( "imgur.com" );
	}
}
