package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.model.InstagramProfileModel;
import com.ensoft.imgurviewer.service.listener.InstagramProfileResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;

import org.json.JSONObject;

public class InstagramProfileService
{
	public static final String TAG = InstagramProfileService.class.getCanonicalName();
	
	protected String getProfileMediaUrl( Uri uri, String maxId )
	{
		String profileUrl = uri.toString();
		
		if ( !profileUrl.endsWith( "/" ) )
		{
			profileUrl += "/";
		}
		
		return profileUrl + "?__a=1&max_id=" + maxId;
	}
	
	public void getProfile( Uri uri, String maxId, final InstagramProfileResolverListener instagramProfileResolverListener )
	{
		String profileMediaUrl = getProfileMediaUrl( uri, maxId );
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( profileMediaUrl, null, response ->
		{
			try
			{
				Log.v( TAG, response.toString() );
				
				InstagramProfileModel profile = new Gson().fromJson( response.getJSONObject( "user" ).getJSONObject( "media" ).toString(), InstagramProfileModel.class );
				
				instagramProfileResolverListener.onProfileResolved( profile );
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				instagramProfileResolverListener.onError( e.toString() );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			instagramProfileResolverListener.onError( error.toString() );
		} )
		{
		};
		
		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
}
