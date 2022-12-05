package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RedGifsService extends BasicVideoServiceSolver
{
	public static final String TAG = RedGifsService.class.getCanonicalName();
	public static final String REDGIF_API_URL = "https://api.redgifs.com/v2";
	
	@Override
	public String getDomain()
	{
		return "redgifs.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[ 0 ];
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[ 0 ];
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String code = uri.getLastPathSegment();
		
		if ( null == code )
		{
			return;
		}
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( REDGIF_API_URL + "/auth/temporary", null, response ->
		{
			try
			{
				String token = response.getString( "token" );
				
				JsonObjectRequest videoInfoRequest = new JsonObjectRequest( REDGIF_API_URL + "/gifs/" + code + "?users=yes&views=yes", null, videoResponse -> {
					try
					{
						JSONObject obj = videoResponse.getJSONObject( "gif" ).getJSONObject( "urls" );
						String videoUrl = obj.getString( "hd" );
						Uri videoUri = Uri.parse( videoUrl );
						sendPathResolved( pathResolverListener, videoUri, UriUtils.guessMediaTypeFromUri( videoUri ), referer );
					} catch ( Exception e ) {
						Log.v( TAG, e.getMessage() );
						
						pathResolverListener.onPathError( uri, e.toString() );
					}
				}, error -> {
					Log.v( TAG, error.toString() );
					
					pathResolverListener.onPathError( uri, error.toString() );
				} )
				{
					@Override
					public Map<String, String> getHeaders()
					{
						Map<String, String> headers = new HashMap<>();
						headers.put( "Authorization", "Bearer " + token );
						headers.put( "User-Agent", UriUtils.getDefaultUserAgent() );
						return headers;
					}
				};
				
				RequestService.getInstance().addToRequestQueue( videoInfoRequest );
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				pathResolverListener.onPathError( uri, e.toString() );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( uri, error.toString() );
		} );
		
		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/watch/" };
	}
}
