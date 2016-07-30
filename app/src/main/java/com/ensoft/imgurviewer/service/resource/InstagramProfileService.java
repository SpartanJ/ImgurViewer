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
	public static final String TAG = ImgurAlbumService.class.getCanonicalName();

	protected String getProfileMediaUrl( Uri uri )
	{
		String profileUrl = uri.toString();

		if ( !profileUrl.endsWith( "/" ) )
		{
			profileUrl += "/";
		}

		return profileUrl + "media/";
	}

	public void getProfile( Uri uri, final InstagramProfileResolverListener instagramProfileResolverListener )
	{
		String profileMediaUrl = getProfileMediaUrl( uri );

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( profileMediaUrl, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					InstagramProfileModel profile = new Gson().fromJson( response.toString(), InstagramProfileModel.class );

					instagramProfileResolverListener.onProfileResolved( profile );
				}
				catch ( Exception e )
				{
					Log.v( TAG, e.getMessage() );

					instagramProfileResolverListener.onError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.v( TAG, error.toString() );

				instagramProfileResolverListener.onError( error.toString() );
			}
		}) {};

		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
}
