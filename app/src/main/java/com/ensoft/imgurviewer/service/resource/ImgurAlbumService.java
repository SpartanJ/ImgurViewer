package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurAlbumResource;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.ImgurAlbumResolverListener;
import com.ensoft.restafari.network.helper.RequestParameters;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imgurviewer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgurAlbumService implements AlbumProvider
{
	public static final String TAG = ImgurAlbumService.class.getCanonicalName();
	public static final String IMGUR_ALBUM_API_URL = ImgurService.IMGUR_API_URL + "/album/";
	
	public String getAlbumId( Uri uri )
	{
		return getAlbumId( uri.toString() );
	}
	
	public String getAlbumId( String uri )
	{
		String endPart = null;
		
		if ( uri.contains( "/a/" ) )
		{
			endPart = uri.substring( uri.lastIndexOf( "/a/" ) + 3 );
		}
		
		if ( null != endPart )
		{
			int slash = endPart.indexOf( "/" );
			
			if ( -1 != slash )
			{
				endPart = endPart.substring( 0, slash );
			}
		}
		
		return endPart;
	}
	
	public boolean isImgurAlbum( Uri uri )
	{
		return new ImgurService().isServicePath( uri ) && ( uri.toString().contains( "/a/" ) );
	}
	
	public void getAlbum( Uri uri, final ImgurAlbumResolverListener imgurAlbumResolverListener )
	{
		String albumUrl = IMGUR_ALBUM_API_URL + getAlbumId( uri );
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( albumUrl, null, response ->
		{
			try
			{
				ImgurAlbumResource album = new Gson().fromJson( response.toString(), ImgurAlbumResource.class );
				
				imgurAlbumResolverListener.onAlbumResolved( album.data );
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				imgurAlbumResolverListener.onAlbumError( e.toString() );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			imgurAlbumResolverListener.onAlbumError( error.toString() );
		} )
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				Map<String, String> headers = new HashMap<>();
				headers.put( "Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
				headers.put( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:76.0) Gecko/20100101 Firefox/79.0" );
				return headers;
			}
		};
		
		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
	
	public static final String IMGUR_GALLERY_API_URL = ImgurService.IMGUR_API_URL + "/gallery/";
	
	public String getGalleryId( Uri uri )
	{
		String endPart = null;
		String uriString = uri.toString();
		
		if ( uriString.contains( "/gallery/" ) )
		{
			endPart = uriString.substring( uriString.lastIndexOf( "/gallery/" ) + 9 );
		}
		else if ( uriString.contains( "/topic/" ) )
		{
			endPart = uri.getLastPathSegment();
		}
		else if ( uriString.contains( "/t/" ) )
		{
			endPart = uri.getLastPathSegment();
		}
		else if ( uriString.contains( "/a/" ) )
		{
			endPart = uriString.substring( uriString.lastIndexOf( "/a/" ) + 3 );
		}
		else if ( uriString.contains( "/album/" ) )
		{
			endPart = uriString.substring( uriString.lastIndexOf( "/album/" ) + 7 );
		}
		
		if ( null != endPart )
		{
			int slash = endPart.indexOf( "/" );
			
			if ( -1 != slash )
			{
				endPart = endPart.substring( 0, slash );
			}
		}
		
		return endPart;
	}
	
	@Override
	public boolean isAlbum( Uri uri )
	{
		return new ImgurService().isServicePath( uri ) && (
			uri.toString().contains( "/a/" ) ||
				uri.toString().contains( "/gallery/" ) ||
				uri.toString().contains( "/topic/" ) ||
				uri.toString().contains( "/t/" ) ||
				new ImgurService().isMultiImageUri( uri )
		);
	}
	
	@Override
	public void getAlbum( Uri uri, AlbumSolverListener albumSolverListener )
	{
		if ( new ImgurService().isMultiImageUri( uri ) )
		{
			albumSolverListener.onAlbumResolved( new ImgurService().getImagesFromMultiImageUri( uri ) );
		}
		else if ( isImgurAlbum( uri ) )
		{
			getAlbum( uri, new ImgurAlbumResolverListener()
			{
				@Override
				public void onAlbumResolved( ImgurAlbum album )
				{
					albumSolverListener.onAlbumResolved( album.getImages() );
				}
				
				@Override
				public void onAlbumError( String error )
				{
					albumSolverListener.onAlbumError( error );
				}
			} );
		}
		else
		{
			String albumUrl = IMGUR_GALLERY_API_URL + getGalleryId( uri );
			Map<String, String> headers = new HashMap<>();
			headers.put( "Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
			headers.put( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:76.0) Gecko/20100101 Firefox/79.0" );
			
			RequestService.getInstance().makeStringRequest( Request.Method.GET, albumUrl, new ResponseListener<String>()
			{
				@Override
				public void onRequestSuccess( Context context, String response )
				{
					try
					{
						JSONObject data = new JSONObject( response ).getJSONObject( "data" );
					
						boolean isAlbum = data.getBoolean( "is_album" );
						
						if ( isAlbum )
						{
							ImgurAlbum album = new Gson().fromJson( data.toString(), ImgurAlbum.class );
							
							albumSolverListener.onAlbumResolved( album.getImages() );
						}
						else
						{
							ImgurImage image = new Gson().fromJson( data.toString(), ImgurImage.class );
							
							albumSolverListener.onImageResolved( image );
						}
					}
					catch ( Exception e )
					{
						Log.v( TAG, e.getMessage() );
						
						albumSolverListener.onAlbumError( e.toString() );
					}
				}
				
				public void onRequestError( Context context, int errorCode, String errorMessage ) {
					Log.v( TAG, errorMessage );
					
					albumSolverListener.onAlbumError( errorMessage );
				}
			},  new RequestParameters(), headers );
		}
	}
}
