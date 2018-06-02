package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.InstagramProfileModel;
import com.ensoft.imgurviewer.model.instagram.InstagramProfileBaseModel;
import com.ensoft.imgurviewer.service.listener.InstagramProfileResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONObject;

public class InstagramProfileService
{
	public static final String TAG = InstagramProfileService.class.getCanonicalName();
	private static final String startStr = "<script type=\"text/javascript\">window._sharedData = ";
	private static final String endStr = ";</script>";
	
	protected InstagramProfileModel getProfileJson( String response )
	{
		try
		{
			int start = response.indexOf( startStr );
			int end = response.indexOf( endStr, start );
			String json = response.substring( start + startStr.length(), end );
			InstagramProfileBaseModel instagramProfileBaseModel = new Gson().fromJson( json, InstagramProfileBaseModel.class );
			
			return new InstagramProfileModel( instagramProfileBaseModel );
		}
		catch ( Exception e ) {}
		
		return null;
	}
	
	public void getProfile( Uri uri, final InstagramProfileResolverListener instagramProfileResolverListener )
	{
		String profileMediaUrl = uri.toString();
		
		StringRequest jsonObjectRequest = new StringRequest( profileMediaUrl, response ->
		{
			try
			{
				Log.v( TAG, response );
				
				InstagramProfileModel profile = getProfileJson( response );
				
				if ( null != profile )
				{
					instagramProfileResolverListener.onProfileResolved( profile );
				}
				else
				{
					instagramProfileResolverListener.onError( App.getInstance().getString( R.string.failedFetchProfile ) );
				}
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
