package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String, String> headers = new HashMap<>();
		headers.put( "Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
		headers.put( "User-Agent", UriUtils.getDefaultUserAgent() );

		RequestService.getInstance().makeStringRequest(Request.Method.GET, uri.toString(), new ResponseListener<String>() {
			@Override
			public void onRequestSuccess(Context context, String response)
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
			}

			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( TAG, errorMessage );

				pathResolverListener.onPathError( uri, errorMessage );
			}
		}, null, headers);
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
