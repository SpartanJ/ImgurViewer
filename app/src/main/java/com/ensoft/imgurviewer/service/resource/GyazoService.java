package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.model.GyazoOEmbed;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;

import org.json.JSONObject;

public class GyazoService extends ImageServiceSolver
{
	public static final String TAG = GyazoService.class.getCanonicalName();
	public static final String GYAZO_DOMAIN = "gyazo.com";
	public static final String GYAZO_API_URL = "https://api.gyazo.com/api";
	public static final String GYAZO_GET_IMAGE_URL = GYAZO_API_URL + "/oembed?url=";
	
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String oEmbedUrl = GYAZO_GET_IMAGE_URL + uri.toString();
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( oEmbedUrl, null, response ->
		{
			try
			{
				Log.v( TAG, response.toString() );
				
				GyazoOEmbed oEmbed = new Gson().fromJson( response.toString(), GyazoOEmbed.class );
				
				pathResolverListener.onPathResolved( oEmbed.getUri(), null );
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
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( GYAZO_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
