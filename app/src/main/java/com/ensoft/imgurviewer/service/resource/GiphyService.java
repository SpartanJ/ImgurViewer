package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.GiphyResource;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

public class GiphyService extends MediaServiceSolver
{
	public static final String TAG = GiphyService.class.getCanonicalName();
	public static final String GIPHY_DOMAIN = "giphy.com";
	public static final String GIPHY_API_URL = "https://api.giphy.com/v1/gifs/%s?api_key=%s";
	
	protected String getId( Uri uri )
	{
		String url = uri.toString();
		String[] split = url.split( "-" );
		
		if ( split.length > 0 )
		{
			return split[ split.length - 1 ];
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		try
		{
			String id = getId( uri );
			
			if ( null != id )
			{
				String apiUrl = String.format( GIPHY_API_URL, id, App.getInstance().getString( R.string.giphy_api_key ) );
				
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( apiUrl, null, response ->
				{
					try
					{
						Log.v( TAG, response.toString() );
						
						GiphyResource giphyResource = new Gson().fromJson( response.toString(), GiphyResource.class );
						
						if ( 200 == giphyResource.getStatus() )
						{
							pathResolverListener.onPathResolved( giphyResource.getData().getUri(), UriUtils.guessMediaTypeFromUri( giphyResource.getData().getUri() ), uri );
						}
						else
						{
							pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.unknown_error ) );
						}
					}
					catch ( Exception e )
					{
						Log.v( TAG, e.getMessage() );
						
						pathResolverListener.onPathError( uri, e.toString() );
					}
				}, error ->
				{
					Log.v( TAG, error.toString() );
					
					pathResolverListener.onPathError( uri, error.toString() );
				} );
				
				RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
			}
		}
		catch ( Exception e )
		{
			Log.v( TAG, e.getMessage() );
			
			pathResolverListener.onPathError( uri, e.toString() );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, GIPHY_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
