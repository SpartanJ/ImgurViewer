package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.model.StreamableVideo;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.rest.response.HttpStatus;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class StreamableService extends MediaServiceSolver
{
	public static final String TAG = StreamableService.class.getCanonicalName();
	public static final String STREAMABLE_DOMAIN = "streamable.com";
	public static final String STREAMABLE_API_URL = "https://api.streamable.com/videos/";
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String code = uri.getLastPathSegment();
		
		if ( null == code )
		{
			return;
		}
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( STREAMABLE_API_URL + code, null, response ->
		{
			try
			{
				Log.v( TAG, response.toString() );
				
				final StreamableVideo video = new Gson().fromJson( response.toString(), StreamableVideo.class );
				
				new Thread( () ->
				{
					
					HttpURLConnection urlConnection = null;
					
					System.setProperty( "http.keepAlive", "false" );
					
					try
					{
						URL url = new URL( video.getUri().toString() );
						urlConnection = (HttpURLConnection) url.openConnection();
						urlConnection.setRequestMethod( "HEAD" );
						urlConnection.getInputStream().close();
						
						if ( urlConnection.getResponseCode() == HttpStatus.OK_200.getCode() )
						{
							new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( video.getUri(), MediaType.VIDEO_MP4, uri ) );
						}
						else
						{
							new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( App.getInstance().getString( R.string.videoRemoved ) ) );
						}
					}
					catch ( final Exception e )
					{
						new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( App.getInstance().getString( R.string.videoRemoved ) ) );
					}
					finally
					{
						if ( urlConnection != null )
						{
							urlConnection.disconnect();
						}
					}
				} ).start();
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				pathResolverListener.onPathError( e.toString() );
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
		return ( uriStr.startsWith( "https://" + STREAMABLE_DOMAIN ) || uriStr.startsWith( "http://" + STREAMABLE_DOMAIN ) ) &&
			( uriStr.length() - uriStr.replace( "/", "" ).length() == 3 );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
