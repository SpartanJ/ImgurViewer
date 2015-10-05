package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgurAlbumService
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
		else if ( uri.contains( "/gallery/" ) )
		{
			endPart = uri.substring( uri.lastIndexOf( "/gallery/" ) + 9 );
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
		return new ImgurService().isImgurPath( uri ) && (
			uri.toString().contains( "/a/" ) ||
			uri.toString().contains( "/gallery/" )
		);
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
					Log.v( TAG, e.getMessage() );

					imgurAlbumResolverListener.onError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.v( TAG, error.toString() );

				imgurAlbumResolverListener.onError( error.toString() );
			}
		})
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<>();
				params.put("Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
				return params;
			}
		};

		RequestQueueService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
}
