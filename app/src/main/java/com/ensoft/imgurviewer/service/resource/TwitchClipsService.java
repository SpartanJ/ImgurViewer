package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.TwitchClip;
import com.ensoft.imgurviewer.model.TwitchClips;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

public class TwitchClipsService extends ImageServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		String qualityOptions = "quality_options:";
		
		int pos = response.lastIndexOf( qualityOptions );
		
		if ( -1 != pos )
		{
			int endPos = response.indexOf( "]", pos );
			
			if ( -1 != endPos )
			{
				String json = "{" + response.substring( pos, endPos + 1 ) + "}";
				
				try
				{
					TwitchClips twitchClips = new Gson().fromJson( json, TwitchClips.class );
					
					if ( null != twitchClips && null != twitchClips.getClips() )
					{
						TwitchClip[] clips = twitchClips.getClips();
						
						for ( TwitchClip clip : clips )
						{
							if ( clip.is720p() )
							{
								return Uri.parse( clip.getSource() );
							}
						}
						
						if ( clips.length > 0 )
						{
							return Uri.parse( clips[ 0 ].getSource() );
						}
					}
				}
				catch ( Exception e )
				{
					return null;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		StringRequest stringRequest = new StringRequest( uri.toString(), response ->
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
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( error.toString() );
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
