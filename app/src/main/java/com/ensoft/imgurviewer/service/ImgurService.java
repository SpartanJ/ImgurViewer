package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.interfaces.ImgurGalleryResolverListener;
import com.ensoft.imgurviewer.service.interfaces.PathResolverListener;

public class ImgurService
{
	public static final String IMGUR_DOMAIN = "imgur.com";
	public static final String IMGUR_IMAGE_DOMAIN = "i.imgur.com";
	public static final String IMGUR_MOBILE_DOMAIN = "m.imgur.com";
	public static final String IMGUR_API_URL = "https://api.imgur.com/3";

	protected void getFirstAlbumImage( final Uri url, final PathResolverListener pathResolverListener )
	{
		new ImgurAlbumService().getAlbum( url, new ImgurAlbumResolverListener()
		{
			@Override
			public void onAlbumResolved( ImgurAlbum album )
			{
				Uri uri = Uri.parse( album.getImage( 0 ).getLink() );

				pathResolverListener.onPathResolved( uri, getThumbnailPath( uri ) );
			}

			@Override
			public void onError( String error )
			{
				pathResolverListener.onPathError( error );
			}
		} );
	}

	protected void getFirstGalleryImage( final Uri url, final PathResolverListener pathResolverListener )
	{
		new ImgurGalleryService().getGallery( url, new ImgurGalleryResolverListener()
		{
			@Override
			public void onAlbumResolved( ImgurAlbum album )
			{
				Uri uri = Uri.parse( album.getImage( 0 ).getLink() );

				pathResolverListener.onPathResolved( uri, getThumbnailPath( uri ) );
			}

			@Override
			public void onImageResolved( ImgurImage image )
			{
				Uri uri = image.getLinkUri();

				pathResolverListener.onPathResolved( uri, getThumbnailPath( uri ) );
			}

			@Override
			public void onError( String error )
			{
				pathResolverListener.onPathError( error );
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

		if ( url.contains( "/r/" ) )
		{
			url = url.substring( 0, url.indexOf( "/r/" ) ) + url.substring( url.lastIndexOf( "/" ) );
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

	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		if ( new ImgurAlbumService().isImgurAlbum( uri ) )
		{
			getFirstAlbumImage( uri, pathResolverListener );
		}
		else if ( new ImgurGalleryService().isImgurGallery( uri ) )
		{
			getFirstGalleryImage( uri, pathResolverListener );
		}
		else
		{
			Uri fixedUri = processPath( uri );

			pathResolverListener.onPathResolved( fixedUri, getThumbnailPath( fixedUri ) );
		}
	}

	public boolean isVideo( Uri uri )
	{
		return isVideo( uri.toString() );
	}

	public boolean isVideo( String uri )
	{
		return uri.endsWith( ".gifv" ) || uri.endsWith( ".mp4" ) || uri.endsWith( ".avi" ) || uri.endsWith( ".flv" ) || uri.endsWith( ".mkv" ) || uri.endsWith( ".webm" );
	}

	public boolean isImgurPath( Uri uri )
	{
		return uri.toString().contains( IMGUR_DOMAIN );
	}

	public String getImageId( Uri uri )
	{
		return getImageId( uri.toString() );
	}

	public String getImageId( String uri )
	{
		int index = uri.lastIndexOf( "/" );

		if ( -1 != index )
		{
			return uri.substring( index + 1 );
		}

		return null;
	}

	public boolean isMultiImageUri( Uri uri )
	{
		if ( isImgurPath( uri ) && !new ImgurAlbumService().isImgurAlbum( uri ) && !new ImgurGalleryService().isImgurGallery( uri ) )
		{
			String id = getImageId( uri );

			if ( null != id )
				return id.contains( "," );
		}

		return false;
	}

	public Uri genImgurUriFromId( String id )
	{
		return Uri.parse( "https://" + IMGUR_IMAGE_DOMAIN + "/" + id + ".jpg" );
	}

	public ImgurImage[] getImagesFromMultiImageUri( Uri uri )
	{
		String ids = getImageId( uri );
		String[] idStrings = ids.split( "," );

		ImgurImage[] images = new ImgurImage[ idStrings.length ];

		if ( images.length > 0 )
		{
			for ( int i = 0; i < images.length; i++ )
			{
				images[i] = new ImgurImage( idStrings[i], genImgurUriFromId( idStrings[i] ).toString() );
			}
		}

		return images;
	}
}
