package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.TwitchClip;
import com.ensoft.imgurviewer.model.TwitchClips;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class TwitchClipsService extends MediaServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";
	protected static final String TWITCH_CLIPS_STATUS = "https://clips.twitch.tv/api/v2/clips/{id}/status";
	
	protected String getVideoStatusUrl( Uri uri )
	{
		return TWITCH_CLIPS_STATUS.replace( "{id}", uri.getLastPathSegment() );
	}
	
	protected Uri getVideoUrlFromResponse( TwitchClips twitchClips )
	{
		try
		{
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
		catch ( Exception e ) {}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeJsonRequest( Request.Method.GET, getVideoStatusUrl( uri ), new ResponseListener<TwitchClips>()
		{
			@Override
			public void onRequestSuccess( Context context, TwitchClips response )
			{
				Uri videoUrl = getVideoUrlFromResponse( response );
				
				if ( videoUrl != null )
				{
					new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), uri ) );
				}
				else
				{
					new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( App.getInstance().getString( R.string.videoUrlNotFound ) ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( errorMessage ) );
			}
		} );
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
