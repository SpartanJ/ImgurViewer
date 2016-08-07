package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.VidmeResource;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONObject;

public class VidmeService extends ImageServiceSolver
{
	public static final String TAG = VidmeService.class.getCanonicalName();
	public static final String VIDME_DOMAIN = "vid.me";
	public static final String VIDME_API_URL = "https://api.vid.me";
	public static final String VIDME_GET_IMAGE_URL = VIDME_API_URL + "/videoByUrl?url=";

	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String oEmbedUrl = VIDME_GET_IMAGE_URL + uri.toString();

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( oEmbedUrl, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					VidmeResource vidmeResource = new Gson().fromJson( response.toString(), VidmeResource.class );

					if ( null != vidmeResource && null != vidmeResource.getVideo() )
					{
						pathResolverListener.onPathResolved( vidmeResource.getVideo().getVideoUri(), null );
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

		return ( uriStr.startsWith( "https://" + VIDME_DOMAIN ) || uriStr.startsWith( "http://" + VIDME_DOMAIN ) );
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}

	@Override
	public boolean isVideo( String uri )
	{
		return true;
	}
}
