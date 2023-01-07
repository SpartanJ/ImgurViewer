package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.exception.NeedsToCheckApiException;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurAlbumResource;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.model.ThumbnailSize;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgurService extends MediaServiceSolver
{
	public static final String TAG = ImgurService.class.getCanonicalName();
	public static final String IMGUR_DOMAIN = "imgur.com";
	public static final String IMGURIO_DOMAIN = "imgur.io";
	public static final String IMGUR_IMAGE_DOMAIN = "i.imgur.com";
	public static final String IMGUR_MOBILE_DOMAIN = "m.imgur.com";
	public static final String IMGUR_API_URL = "https://api.imgur.com/3";
	public static final String IMGUR_API_IMAGE_URL = IMGUR_API_URL + "/image/";
	
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
	
	public Uri processPath( Uri uri, boolean fixVideoPath, boolean throwException ) throws NeedsToCheckApiException
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
		else if ( url.contains( "//" + IMGURIO_DOMAIN ) )
		{
			url = url.replace( "//" + IMGURIO_DOMAIN, "//" + IMGUR_IMAGE_DOMAIN );
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
		
		if ( url.startsWith( "http://" ) )
		{
			url = url.replaceFirst( "http://", "https://" );
		}
		
		if ( !url.endsWith( ".png" ) && !url.endsWith( ".jpg" ) && !url.endsWith( ".jpeg" ) && !url.endsWith( ".mp4" ) && !url.endsWith( ".m3u8" ) && !url.endsWith( ".gif" ) )
		{
			if ( throwException )
			{
				throw new NeedsToCheckApiException();
			}
			else
			{
				url += ".jpg";
			}
		}
		
		return Uri.parse( url );
	}
	
	public Uri getThumbnailPath( Uri uri )
	{
		return getThumbnailPath( uri, ThumbnailSize.SMALL_SQUARE );
	}
	
	public Uri getThumbnailPath( Uri uri, ThumbnailSize thumbnailSize )
	{
		try
		{
			String fixedUri = processPath( uri, false, false ).toString();
			
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
		catch ( NeedsToCheckApiException e )
		{
			return Uri.EMPTY;
		}
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
			try
			{
				Uri fixedUri = processPath( uri, true, true );
				
				pathResolverListener.onPathResolved( fixedUri, UriUtils.guessMediaTypeFromUri( uri ), isVideo( uri ) ? uri : getThumbnailPath( fixedUri ) );
			}
			catch ( NeedsToCheckApiException e )
			{
				String imageUrl = IMGUR_API_IMAGE_URL + uri.getLastPathSegment();
				
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( imageUrl, null, response ->
				{
					try
					{
						JSONObject obj = response.getJSONObject( "data" );
						if ( obj.has( "mp4" ) )
						{
							Uri videoUri = Uri.parse( obj.getString( "mp4" ) );
							sendPathResolved( pathResolverListener, videoUri, UriUtils.guessMediaTypeFromUri( videoUri ), uri );
						}
						else
						{
							Uri fixedUri = processPath( Uri.parse( obj.getString( "link" ) ), true, false );
							sendPathResolved( pathResolverListener, fixedUri, UriUtils.guessMediaTypeFromUri( fixedUri ), uri );
						}
					} catch ( JSONException | NeedsToCheckApiException exception )
					{
						sendPathError( uri, pathResolverListener );
					}
				}, error ->
				{
					Log.v( TAG, error.toString() );
					sendPathError( uri, pathResolverListener );
				} )
				{
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError
					{
						Map<String, String> headers = new HashMap<>();
						headers.put( "Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
						headers.put( "User-Agent", UriUtils.getDefaultUserAgent() );
						return headers;
					}
				};
				
				RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
			}
			
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( IMGUR_DOMAIN ) || uri.toString().contains( IMGURIO_DOMAIN );
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
