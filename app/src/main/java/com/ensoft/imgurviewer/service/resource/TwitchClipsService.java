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
import java.util.List;

public class TwitchClipsService extends MediaServiceSolver
{
	public static final String TAG = TwitchClipsService.class.getCanonicalName();
	protected static final String TWITCH_CLIENT_ID = "ue6666qo983tsx6so1t0vnawi233wa";
	protected static final String TWITCH_CLIPS_DOMAIN = "clips.twitch.tv";
	protected static final String TWITCH_DOMAIN_SHORT = "twitch.tv";
	protected static final String TWITCH_DOMAIN_FULL = "www.twitch.tv";
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
							first = Uri.parse(
								clip.getSource()
								+ "?sig=" + twitchClips.data.clip.playbackAccessToken.signature
								+ "&token=" + URLEncoder.encode( twitchClips.data.clip.playbackAccessToken.value ) );

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
		headers.put( "client-id", TWITCH_CLIENT_ID );

		RequestParameters parameters = new RequestParameters();
		parameters.putString( "query",
			"{clip(slug:\""
			+ getClipId( uri )
			+ "\"){playbackAccessToken(params:{platform:\"web\"playerType:\"clips-download\"}){signature value}videoQualities{sourceURL}}}"
		);
		RequestParameters variables = new RequestParameters();

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
		String scheme = uri.getScheme();
		if ( scheme == null || ( !scheme.equals("http") && !scheme.equals("https") ) )
			return false;
		String host = uri.getHost();
		if ( host == null )
			return false;
		if ( host.equals(TWITCH_CLIPS_DOMAIN) )
			return true;
		if ( host.equals(TWITCH_DOMAIN_SHORT) || host.equals(TWITCH_DOMAIN_FULL) ) {
			List<String> segments = uri.getPathSegments();
			return segments != null && segments.size() == 3 && segments.get(1).equals("clip");
		}
		return false;
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
