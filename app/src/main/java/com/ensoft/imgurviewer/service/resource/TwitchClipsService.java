package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.TwitchClip;
import com.ensoft.imgurviewer.model.TwitchClipResponse;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.helper.RequestParameters;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.net.URLEncoder;
import java.util.HashMap;

public class TwitchClipsService extends MediaServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIENT_ID = "jzkbprff40iqj646a697cyrvl0zt2m6";
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";
	protected static final String TWITCH_CLIPS_STATUS = "https://gql.twitch.tv/gql";
	protected static final String[] TWITCH_CLIPS_QUALITIES = new String[] { "source", "1080", "720", "480", "360" };
	
	protected String getClipId( Uri uri )
	{
		if ( uri.getLastPathSegment().equals( "embed" ) )
		{
				return uri.getQueryParameter( "clip" );
		}
		
		return uri.getLastPathSegment();
	}
	
	protected Uri getVideoUrlFromResponse( TwitchClipResponse twitchClips )
	{
		try
		{
			if ( null != twitchClips && null != twitchClips.data && null != twitchClips.data.clip && null != twitchClips.data.clip.getClips() )
			{
				TwitchClip[] clips = twitchClips.data.clip.getClips();
				Uri first = null;
				
				for ( String quality : TWITCH_CLIPS_QUALITIES )
				{
					for ( TwitchClip clip : clips )
					{
						if ( quality.equals( clip.getQuality() ) || first == null )
						{
							first = Uri.parse( clip.getSource() + "?" +
								"sig=" + twitchClips.data.clip.playbackAccessToken.signature +
								"&token=" + URLEncoder.encode( twitchClips.data.clip.playbackAccessToken.value.replace( "\\", "" ) ) );
							
							if ( quality.equals( clip.getQuality() ) )
								return first;
						}
					}
				}
				
				return first;
			}
		}
		catch ( Exception e ) {}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		HashMap<String, String> headers = new HashMap<>();
		headers.put( "Client-ID", TWITCH_CLIENT_ID );
		
		RequestParameters parameters = new RequestParameters();
		parameters.putString( "operationName", "VideoAccessToken_Clip" );
		RequestParameters variables = new RequestParameters();
		variables.putString( "slug", getClipId( uri ) );
		parameters.putObject( "variables", variables );
		RequestParameters extensions = new RequestParameters();
		RequestParameters persistedQuery = new RequestParameters();
		persistedQuery.putInt( "version", 1 );
		persistedQuery.putString( "sha256Hash", "36b89d2507fce29e5ca551df756d27c1cfe079e2609642b4390aa4c35796eb11" );
		extensions.putObject( "persistedQuery", persistedQuery );
		parameters.putObject( "extensions", extensions );
		
		RequestService.getInstance().makeJsonRequest( Request.Method.POST, TWITCH_CLIPS_STATUS, new ResponseListener<TwitchClipResponse>()
		{
			@Override
			public void onRequestSuccess( Context context, TwitchClipResponse response )
			{
				Uri videoUrl = getVideoUrlFromResponse( response );
				
				if ( videoUrl != null )
				{
					pathResolverListener.onPathResolved( videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), uri );
				}
				else
				{
					pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.videoUrlNotFound ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				pathResolverListener.onPathError( uri, errorMessage );
			}
		}, parameters, headers );
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
