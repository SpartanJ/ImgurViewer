package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.model.ThumbnailSize;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class ImgurService extends MediaServiceSolver
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
				
				pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), isVideo( uri ) ? uri : getThumbnailPath( uri ) );
			}
			
			@Override
			public void onAlbumError( String error )
			{
				pathResolverListener.onPathError( url, error );
			}
		} );
	}
	
	protected void getFirstGalleryImage( final Uri url, final PathResolverListener pathResolverListener )
	{
		new ImgurAlbumService().getAlbum( url, new AlbumSolverListener()
		{
			@Override
			public void onAlbumResolved( ImgurImage[] images )
			{
				Uri uri = Uri.parse( images[0].getLink() );
				
				pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), isVideo( uri ) ? url : getThumbnailPath( uri ) );
			}
			
			@Override
			public void onImageResolved( ImgurImage image )
			{
				Uri uri = image.getLinkUri();
				
				pathResolverListener.onPathResolved( uri, UriUtils.guessMediaTypeFromUri( uri ), isVideo( uri ) ? url : getThumbnailPath( uri ) );
			}
			
			@Override
			public void onAlbumError( String error )
			{
				pathResolverListener.onPathError( url, error );
			}
		} );
	}
	
	public Uri processPath( Uri uri, boolean fixVideoPath )
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
		
		if ( url.contains( "?" ) )
		{
			url = url.substring( 0, url.indexOf( '?' ) );
		}
		
		if ( url.endsWith( ".gifv" ) )
		{
			url = url.replace( ".gifv", ".mp4" );
		}
		
		if ( fixVideoPath && ( url.endsWith( ".gif" ) ) )
		{
			url = url.replace( ".gif", ".mp4" );
		}
		
		if ( !url.endsWith( ".png" ) && !url.endsWith( ".jpg" ) && !url.endsWith( ".jpeg" ) && !url.endsWith( ".mp4" ) && !url.endsWith( ".m3u8" ) && !url.endsWith( ".gif" ) )
		{
			url += ".jpg";
		}
		
		return Uri.parse( url );
	}
	
	public Uri getThumbnailPath( Uri uri )
	{
		return getThumbnailPath( uri, ThumbnailSize.SMALL_SQUARE );
	}
	
	public Uri getThumbnailPath( Uri uri, ThumbnailSize thumbnailSize )
	{
		String fixedUri = processPath( uri, false ).toString();
		
		int pos = fixedUri.lastIndexOf( "." );
		
		String path = fixedUri.substring( 0, pos );
		String ext = fixedUri.substring( pos );
		String thumbSize = thumbnailSize.toString();
		
		if ( ".gif".equals( ext ) || ".mp4".equals( ext ) )
		{
			ext = ".jpg";
			thumbSize = "";
		}
		
		return Uri.parse( path + thumbSize + ext );
	}
	
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		if ( new ImgurAlbumService().isImgurAlbum( uri ) )
		{
			getFirstAlbumImage( uri, pathResolverListener );
		}
		else if ( new ImgurAlbumService().isAlbum( uri ) )
		{
			getFirstGalleryImage( uri, pathResolverListener );
		}
		else
		{
			Uri fixedUri = processPath( uri, true );
			
			pathResolverListener.onPathResolved( fixedUri, UriUtils.guessMediaTypeFromUri( uri ), isVideo( uri ) ? uri : getThumbnailPath( fixedUri ) );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( IMGUR_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return new ImgurAlbumService().isImgurAlbum( uri ) || new ImgurAlbumService().isAlbum( uri ) || isMultiImageUri( uri );
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
		if ( isServicePath( uri ) )
		{
			String id = getImageId( uri );
			
			if ( null != id )
			{
				return id.contains( "," );
			}
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
				images[ i ] = new ImgurImage( idStrings[ i ], genImgurUriFromId( idStrings[ i ] ).toString() );
			}
		}
		
		return images;
	}
}
