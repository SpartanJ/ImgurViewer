package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class TwitchClipsService extends ImageServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";

	protected Uri getVideoUrlFromResponse( String response )
	{
		String videoUrlTag = "clip_video_url:";

		int pos = response.lastIndexOf( videoUrlTag );

		if ( -1 != pos )
		{
			int endPos = response.indexOf( "\",", pos );

			if ( -1 != endPos )
			{
				String url = response.substring( pos + videoUrlTag.length(), endPos );

				url = url.replace( "\"", "" );
				url = url.replace( " ", "" );
				url = url.replace( "\\", "" );

				return Uri.parse( url );
			}
		}

		return null;
	}

	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		StringRequest stringRequest = new StringRequest( uri.toString(), new Response.Listener<String>()
		{
			@Override
			public void onResponse( String response )
			{
				Uri videoUrl = getVideoUrlFromResponse( response );

				if ( videoUrl != null )
				{
					pathResolverListener.onPathResolved( videoUrl, null );
				}
				else
				{
					pathResolverListener.onPathError( App.getInstance().getString( R.string.videoUrlNotFound ) );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse( VolleyError error )
			{
				Log.v( TAG, error.toString() );

				pathResolverListener.onPathError( error.toString() );
			}
		} );

		RequestService.getInstance().addToRequestQueue( stringRequest );
	}

	@Override
	public boolean isServicePath( Uri uri )
	{
		String uriStr = uri.toString();
		return ( uriStr.startsWith( "https://" + TWITCH_CLIPS_DOMAIN ) || uriStr.startsWith( "http://" + TWITCH_CLIPS_DOMAIN ) );
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
