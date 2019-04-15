package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.rest.response.HttpStatus;
import com.imgurviewer.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class RedditVideoService extends MediaServiceSolver
{
	public static final String TAG = RedditVideoService.class.getCanonicalName();
	private static final String V_REDD_IT_DOMAIN = "v.redd.it";
	private static final String I_REDD_IT_DOMAIN = "i.redd.it";
	private static final String PREVIEW_REDD_IT_DOMAIN = "preview.redd.it";
	private static final String V_REDD_IT_VIDEO_URL_M38U = "https://v.redd.it/%s/HLSPlaylist.m3u8";
	private static final String V_REDD_IT_VIDEO_URL = "https://v.redd.it/%s/DASH_2_4_M";
	private static final String V_REDD_IT_VIDEO_URL_2 = "https://v.redd.it/%s/DASH_600_K";
	
	private String getId( Uri uri )
	{
		String url = uri.toString();
		String[] split = url.split( "/" );
		
		if ( split.length >= 2 )
		{
			if ( !split[ split.length - 1 ].startsWith( "DASH_" ) )
			{
				return split[ split.length - 1 ];
			}
			else
			{
				return split[ split.length - 2 ];
			}
		}
		
		return null;
	}
	
	private boolean videoExists( final String video )
	{
		HttpURLConnection urlConnection = null;
		
		try
		{
			System.setProperty( "http.keepAlive", "false" );
			URL url = new URL( video );
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod( "HEAD" );
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
					String video = String.format( V_REDD_IT_VIDEO_URL_M38U, id );
					
					if ( videoExists( video ) )
					{
						sendPathResolved( pathResolverListener, Uri.parse( video ), MediaType.STREAM_HLS, uri );
						return;
					}
					else
					{
						video = String.format( V_REDD_IT_VIDEO_URL, id );
						
						if ( videoExists( video ) )
						{
							sendPathResolved( pathResolverListener, Uri.parse( video ), MediaType.STREAM_DASH, uri );
							return;
						}
						else
						{
							video = String.format( V_REDD_IT_VIDEO_URL_2, id );
							
							if ( videoExists( video ) )
							{
								sendPathResolved( pathResolverListener, Uri.parse( video ), MediaType.STREAM_DASH, uri );
								return;
							}
						}
					}
					
					sendPathError( pathResolverListener );
				} ).start();
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				pathResolverListener.onPathError( e.toString() );
			}
		}
		else
		{
			pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_video_url ) );
		}
	}
	
	protected boolean isGifVideo( Uri uri )
	{
		return ( uri.toString().contains( PREVIEW_REDD_IT_DOMAIN ) || uri.toString().contains( I_REDD_IT_DOMAIN ) ) &&
				null != uri.getLastPathSegment() && uri.getLastPathSegment().endsWith( ".gif" ) && "mp4".equals( uri.getQueryParameter( "format" ) );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( V_REDD_IT_DOMAIN ) || isGifVideo( uri );
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
