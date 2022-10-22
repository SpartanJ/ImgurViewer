package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.util.List;

public class PrntScrService extends MediaServiceSolver
{
	public static final String TAG = PrntScrService.class.getCanonicalName();
	private static final String PRNTSCR_DOMAIN = "prntscr.com";
	private static final String PRNTSC_DOMAIN = "prnt.sc";
	
	protected Uri getUrlFromResponse( String response )
	{
		return UriUtils.getUriMatch( response, "<meta property=\"og:image\" content=\"", "\"/>" );
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		StringRequest stringRequest = new StringRequest( uri.toString(), response ->
		{
			Uri mediaUrl = getUrlFromResponse( response );
			
			if ( mediaUrl != null )
			{
				pathResolverListener.onPathResolved( mediaUrl,  UriUtils.guessMediaTypeFromUri( mediaUrl ), uri );
			}
			else
			{
				pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.could_not_resolve_url ) );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( uri, error.toString() );
		} );
		
		RequestService.getInstance().addToRequestQueue( stringRequest );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.getHost() != null && ( uri.getHost().equalsIgnoreCase( PRNTSC_DOMAIN ) || uri.getHost().equalsIgnoreCase( PRNTSCR_DOMAIN ) );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
