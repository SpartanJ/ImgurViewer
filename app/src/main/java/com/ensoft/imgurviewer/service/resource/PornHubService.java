package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class PornHubService extends ImageServiceSolver
{
	public static final String TAG = PornHubService.class.getCanonicalName();
	public static final String PORNHUB_DOMAIN = "pornhub.com";
	
	protected String getVideoUrlQuality( String response, String quality )
	{
		return StringUtils.getStringMatch( response,"\"quality\":\"" + quality + "\",\"videoUrl\":\"", "\"}" );
	}
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		String[] qualities = new String[] { "1440", "1080", "720", "480", "240" };
		
		for ( String quality : qualities )
		{
			String qualityUrl = getVideoUrlQuality( response, quality );
			
			if ( !TextUtils.isEmpty( qualityUrl ) )
			{
				return Uri.parse( qualityUrl.replaceAll( "\\\\", "" ) );
			}
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		StringRequest stringRequest = new StringRequest( uri.toString(), response ->
		{
			Uri videoUrl = getVideoUrlFromResponse( response );
			
			if ( videoUrl != null )
			{
				pathResolverListener.onPathResolved( videoUrl, null );
			}
			else
			{
				pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_video_url ) );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( error.toString() );
		} );
		
		RequestService.getInstance().addToRequestQueue( stringRequest );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, PORNHUB_DOMAIN, "/view_video.php?" );
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
