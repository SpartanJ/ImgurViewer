package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ImgurAlbumService
{
	public static final String TAG = ImgurAlbumService.class.getCanonicalName();
	public static final String IMGUR_ALBUM_API_URL = ImgurService.IMGUR_API_URL + "/album/";

	public String getAlbumId( Uri uri )
	{
		return getAlbumId( uri.toString() );
	}

	public String getAlbumId( String url )
	{
		return url.substring( url.lastIndexOf( "/a/" ) + 3 );
	}

	public boolean isImgurAlbum( Uri uri )
	{
		return new ImgurService().isImgurPath( uri ) && uri.toString().contains( "/a/" );
	}

	public void getAlbum( Uri uri, final ImgurAlbumResolverListener imgurAlbumResolverListener )
	{
		String albumUrl = IMGUR_ALBUM_API_URL + getAlbumId( uri );

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( albumUrl, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					JSONObject data = response.getJSONObject( "data" );

					ImgurAlbum album = new Gson().fromJson( data.toString(), ImgurAlbum.class );

					imgurAlbumResolverListener.onAlbumResolved( album );
				}
				catch ( JSONException e )
				{
					imgurAlbumResolverListener.onError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				imgurAlbumResolverListener.onError( error.toString() );
			}
		});

		RequestQueueService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
}
