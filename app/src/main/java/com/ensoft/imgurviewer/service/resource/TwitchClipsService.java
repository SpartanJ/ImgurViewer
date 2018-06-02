package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.TwitchClip;
import com.ensoft.imgurviewer.model.TwitchClips;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONObject;

public class TwitchClipsService extends ImageServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";
	protected static final String TWITCH_CLIPS_STATUS = "https://clips.twitch.tv/api/v2/clips/{id}/status";
	
	protected String getVideoStatusUrl( Uri uri )
	{
		String lastPath = uri.toString().substring( uri.toString().lastIndexOf( "/" ) + 1 );
		
		return TWITCH_CLIPS_STATUS.replace( "{id}", lastPath );
	}
	
	protected Uri getVideoUrlFromResponse( JSONObject response )
	{
		try
		{
			TwitchClips twitchClips = new Gson().fromJson( response.toString(), TwitchClips.class );
			
			if ( null != twitchClips && null != twitchClips.getClips() )
			{
				TwitchClip[] clips = twitchClips.getClips();
				
				for ( TwitchClip clip : clips )
				{
					if ( clip.is1080p() )
					{
						return Uri.parse( clip.getSource() );
					}
					
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
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( getVideoStatusUrl( uri ), response ->
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
		
		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
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
