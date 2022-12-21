package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.rest.response.HttpStatus;
import com.imgurviewer.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RedditVideoService extends MediaServiceSolver
{
	public static final String TAG = RedditVideoService.class.getCanonicalName();
	private static final String V_REDD_IT_DOMAIN = "v.redd.it";
	private static final String I_REDD_IT_DOMAIN = "i.redd.it";
	private static final String PREVIEW_REDD_IT_DOMAIN = "preview.redd.it";
	private static final String REDDIT_DOMAIN = "reddit.com";
	private static final String REDDIT_SUBDOMAIN = ".reddit.com";
	private static final String V_REDD_IT_VIDEO_720p_URL = "https://v.redd.it/%s/DASH_720.mp4";
	private static final String V_REDD_IT_VIDEO_480p_URL = "https://v.redd.it/%s/DASH_480.mp4";
	private static final String V_REDD_IT_VIDEO_240p_URL = "https://v.redd.it/%s/DASH_240.mp4";
	private static final String V_REDD_IT_VIDEO_URL_M38U = "https://v.redd.it/%s/HLSPlaylist.m3u8";
	private static final String V_REDD_IT_VIDEO_URL_DASH = "https://v.redd.it/%s/DASHPlaylist.mpd";
	private static final String V_REDD_IT_VIDEO_URL = "https://v.redd.it/%s/DASH_2_4_M";
	private static final String V_REDD_IT_VIDEO_URL_2 = "https://v.redd.it/%s/DASH_600_K";
	private static final String[] VIDEO_FORMATS = new String[] {
		V_REDD_IT_VIDEO_URL_DASH,
		V_REDD_IT_VIDEO_URL_M38U,
		V_REDD_IT_VIDEO_720p_URL,
		V_REDD_IT_VIDEO_URL,
		V_REDD_IT_VIDEO_480p_URL,
		V_REDD_IT_VIDEO_240p_URL,
		V_REDD_IT_VIDEO_URL_2 };
	
	private String getId( Uri uri )
	{
		if ( isPlayerUrl ( uri ) )
		{
			return uri.getPathSegments().size() == 7
					? uri.getPathSegments().get(5)
					: uri.getPathSegments().get(3);
		}
		return uri.getPathSegments().get(0);
	}
	
	private boolean videoExists( final String video )
	{
		HttpURLConnection urlConnection = null;
		
		try
		{
			System.setProperty( "http.keepAlive", "false" );
			URL url = new URL( video );
			urlConnection = App.getInstance().getProxyUtils().openConnectionTo( url );
			urlConnection.setRequestMethod( "HEAD" );
			urlConnection.setRequestProperty( "User-Agent", UriUtils.getDefaultUserAgent() );
			urlConnection.getInputStream().close();
			
			return urlConnection.getResponseCode() == HttpStatus.OK_200.getCode();
		}
		catch ( Exception e )
		{
			return false;
		}
		finally
		{
			if ( null != urlConnection )
			{
				urlConnection.disconnect();
			}
		}
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		if ( isGifVideo( uri ) )
		{
			sendPathResolved( pathResolverListener, uri, MediaType.VIDEO_MP4, uri );
			return;
		}
		
		final String id = getId( uri );
		
		if ( id != null )
		{
			try
			{
				new Thread( () ->
				{
					for ( String format: VIDEO_FORMATS )
					{
						String video = String.format( format, id );
						
						if ( videoExists( video ) )
						{
							Uri videoUri = Uri.parse( video );
							sendPathResolved( pathResolverListener, videoUri, UriUtils.guessMediaTypeFromUri( videoUri ), uri );
							return;
						}
					}
					
					sendPathError( uri, pathResolverListener );
				} ).start();
			}
			catch ( Exception e )
			{
				if ( e.getMessage() != null )
					Log.v( TAG, e.getMessage() );
				
				pathResolverListener.onPathError( uri, e.toString() );
			}
		}
		else
		{
			pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.could_not_resolve_video_url ) );
		}
	}
	
	protected boolean isGifVideo( Uri uri )
	{
		return ( uri.toString().contains( PREVIEW_REDD_IT_DOMAIN ) || uri.toString().contains( I_REDD_IT_DOMAIN ) ) &&
				null != uri.getLastPathSegment() && uri.getLastPathSegment().endsWith( ".gif" ) && "mp4".equals( uri.getQueryParameter( "format" ) );
	}
	
	private boolean isPlayerUrl( Uri uri )
	{
		final List<String> segments = uri.getPathSegments();
		final boolean hasSubredditPrefix = segments.size() == 7 && segments.get(0).equals( "r" ) && !segments.get(1).isEmpty();
		if ( !hasSubredditPrefix && segments.size() != 5 ) {
			return false;
		}
		final int segmentOffset = hasSubredditPrefix ? 2 : 0;
		return null != uri.getHost() && ( uri.getHost().equals( REDDIT_DOMAIN ) || uri.getHost().endsWith( REDDIT_SUBDOMAIN ) ) &&
				segments.get( segmentOffset ).equals( "link" ) && !segments.get( 1 + segmentOffset ).isEmpty() &&
				segments.get( 2 + segmentOffset ).equals( "video" ) && !segments.get( 3 + segmentOffset ).isEmpty() &&
				segments.get( 4 + segmentOffset ).equals( "player" );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( V_REDD_IT_DOMAIN ) || isGifVideo( uri ) || isPlayerUrl( uri );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
	
	@Override
	public boolean isVideo( Uri uri )
	{
		return true;
	}
}
