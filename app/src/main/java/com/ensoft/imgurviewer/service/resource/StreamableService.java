package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.model.StreamableVideo;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;

import org.json.JSONObject;

public class StreamableService extends ImageServiceSolver
{
	public static final String TAG = StreamableService.class.getCanonicalName();
	public static final String STREAMABLE_DOMAIN = "streamable.com";
	public static final String STREAMABLE_API_URL = "https://api.streamable.com/videos/";

	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String code = uri.getLastPathSegment();

		if ( null == code )
			return;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( STREAMABLE_API_URL + code, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					StreamableVideo video = new Gson().fromJson( response.toString(), StreamableVideo.class );

					pathResolverListener.onPathResolved( video.getUri(), null );
				}
				catch ( Exception e )
				{
					Log.v( TAG, e.getMessage() );

					pathResolverListener.onPathError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.v( TAG, error.toString() );

				pathResolverListener.onPathError( error.toString() );
			}
		});

		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}

	@Override
	public boolean isServicePath( Uri uri )
	{
		String uriStr = uri.toString();
		return ( uriStr.startsWith( "https://" + STREAMABLE_DOMAIN ) || uriStr.startsWith( "http://" + STREAMABLE_DOMAIN ) ) &&
				( uriStr.length() - uriStr.replace( "/", "" ).length() == 3 );
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
