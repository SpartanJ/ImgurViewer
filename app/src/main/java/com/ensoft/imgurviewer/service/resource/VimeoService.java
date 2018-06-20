package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.VimeoVideo;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

public class VimeoService extends MediaServiceSolver
{
	public static final String TAG = VimeoService.class.getCanonicalName();
	public static final String VIMEO_DOMAIN = "vimeo.com";
	public static final String VIMEO_API_URL = "https://player.vimeo.com/video/%s/config";
	
	protected String getId( Uri uri )
	{
		String url = uri.toString();
		String[] split = url.split( "/" );
		
		if ( split.length > 0 )
		{
			return split[ split.length - 1 ];
		}
		
		return null;
	}
	
	protected Uri getVideo( VimeoVideo[] videos )
	{
		if ( null != videos && videos.length > 0 )
		{
			for ( VimeoVideo video : videos )
			{
				if ( video.getHeight() == 1920 )
					return video.getUri();
			}
			
			for ( VimeoVideo video : videos )
			{
				if ( video.getHeight() == 720 )
					return video.getUri();
			}
			
			return videos[0].getUri();
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		try
		{
			String id = getId( uri );
			
			if ( null != id )
			{
				String apiUrl = String.format( VIMEO_API_URL, id );
				
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( apiUrl, null, response ->
				{
					try
					{
						Log.v( TAG, response.toString() );
						
						Uri video = getVideo( new Gson().fromJson( response.getJSONObject( "request" ).getJSONObject( "files" ).getJSONArray( "progressive" ).toString(), VimeoVideo[].class ) );
						
						if ( null != video )
						{
							pathResolverListener.onPathResolved( video, UriUtils.guessMediaTypeFromUri( video ), uri );
						}
						else
						{
							pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_video_url ) );
						}
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
		}
		catch ( Exception e )
		{
			Log.v( TAG, e.getMessage() );
			
			pathResolverListener.onPathError( e.toString() );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, VIMEO_DOMAIN );
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
