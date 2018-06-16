package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class SpankBangService extends ImageServiceSolver
{
	public static final String TAG = PornHubService.class.getCanonicalName();
	public static final String SPANK_BANG_DOMAIN = "spankbang.com";
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		String[] qualities = new String[] { "var stream_url_1080p  = '", "var stream_url_720p  = '", "var stream_url_480p  = '", "var stream_url_320p  = '", "var stream_url_240p  = '" };
		
		for ( String quality : qualities )
		{
			Uri qualityUri = UriUtils.getUriMatch( response, quality, "';" );
			
			if ( null != qualityUri )
			{
				return qualityUri;
			}
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		String uriString = uri.toString();
		
		StringRequest stringRequest = new StringRequest( uriString, response ->
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
		return UriUtils.uriMatchesDomain( uri, SPANK_BANG_DOMAIN ) || uri.toString().contains( SPANK_BANG_DOMAIN );
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
