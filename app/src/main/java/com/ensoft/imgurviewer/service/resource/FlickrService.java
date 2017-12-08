package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.FlickrImage;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONObject;

import java.util.List;

public class FlickrService extends ImageServiceSolver
{
	public static final String TAG = FlickrService.class.getCanonicalName();
	protected static final String FLICKR_DOMAIN = "flickr.com";
	protected static final String FLICKR_API_CALL = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=%s&photo_id=%s&format=json&nojsoncallback=1";
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		try
		{
			List<String> uriPathSegments = uri.getPathSegments();
			String id = uriPathSegments.get( 2 );
			String url = String.format( FLICKR_API_CALL, App.getInstance().getString( R.string.flickr_key ), id );
			
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( url, null, response ->
			{
				try
				{
					Log.v( TAG, response.toString() );
					
					FlickrImage flickrImage = new Gson().fromJson( response.toString(), FlickrImage.class );
					
					if ( "ok".equals( flickrImage.getStat() ) )
					{
						pathResolverListener.onPathResolved( flickrImage.getUri(), flickrImage.getThumbnailUri() );
					}
					else
					{
						pathResolverListener.onPathError( App.getInstance().getString( R.string.unknown_error ) );
					}
				}
				catch ( Exception e )
				{
					Log.v( TAG, e.getMessage() );
					
					pathResolverListener.onPathError( e.toString() );
				}
			}, error ->
			{
				Log.v( TAG, error.toString() );
				
				pathResolverListener.onPathError( error.toString() );
			} );
			
			RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
		}
		catch ( Exception e )
		{
			pathResolverListener.onPathError( App.getInstance().getString( R.string.unknown_error ) );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		String uriStr = uri.toString();
		
		return ( uriStr.startsWith( "https://" + FLICKR_DOMAIN ) ||
			uriStr.startsWith( "http://" + FLICKR_DOMAIN ) ||
			uriStr.startsWith( "https://www." + FLICKR_DOMAIN ) ||
			uriStr.startsWith( "http://www." + FLICKR_DOMAIN ) );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
