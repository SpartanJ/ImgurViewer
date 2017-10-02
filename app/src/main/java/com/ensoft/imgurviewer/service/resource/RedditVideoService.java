package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.rest.response.HttpStatus;
import com.imgurviewer.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class RedditVideoService extends ImageServiceSolver
{
	public static final String TAG = RedditVideoService.class.getCanonicalName();
	public static final String V_REDD_IT_DOMAIN = "v.redd.it";
	public static final String V_REDD_IT_VIDEO_URL_M38U = "https://v.redd.it/%s/HLSPlaylist.m3u8";
	public static final String V_REDD_IT_VIDEO_URL = "https://v.redd.it/%s/DASH_2_4_M";
	public static final String V_REDD_IT_VIDEO_URL_2 = "https://v.redd.it/%s/DASH_600_K";
	
	protected String getId( Uri uri )
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
	
	protected boolean videoExists( final String video )
	{
		HttpURLConnection urlConnection = null;
		
		try
		{
			System.setProperty("http.keepAlive", "false");
			URL url = new URL( video );
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod( "HEAD" );
			urlConnection.getInputStream().close();
			
			if ( urlConnection.getResponseCode() == HttpStatus.OK_200.getCode() )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch ( Exception e )
		{
			return false;
		}
		finally
		{
			if ( null != urlConnection )
				urlConnection.disconnect();
		}
	}
	
	protected void sendPathResolved( final String video, final PathResolverListener pathResolverListener )
	{
		new Handler( Looper.getMainLooper() ).post( new Runnable()
		{
			public void run()
			{
				pathResolverListener.onPathResolved( Uri.parse( video ), null );
			}
		});
	}
	
	protected void sendPathNotFound( final PathResolverListener pathResolverListener )
	{
		new Handler( Looper.getMainLooper() ).post( new Runnable()
		{
			public void run()
			{
				pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_video_url ) );
			}
		} );
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		final String id = getId( uri );
		
		if ( id != null )
		{
			try
			{
				new Thread( new Runnable()
				{
					@Override
					public void run()
					{
						String video = String.format( V_REDD_IT_VIDEO_URL_M38U, id );
						
						if ( videoExists( video ) )
						{
							sendPathResolved( video, pathResolverListener );
							return;
						}
						else
						{
							video = String.format( V_REDD_IT_VIDEO_URL, id );
							
							if ( videoExists( video ) )
							{
								sendPathResolved( video, pathResolverListener );
								return;
							}
							else
							{
								video = String.format( V_REDD_IT_VIDEO_URL_2, id );
								
								if ( videoExists( video ) )
								{
									sendPathResolved( video, pathResolverListener );
									return;
								}
							}
						}
						
						sendPathNotFound( pathResolverListener );
					}
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
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( V_REDD_IT_DOMAIN );
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
	
	@Override
	public boolean isVideo( String uri )
	{
		return true;
	}
}
