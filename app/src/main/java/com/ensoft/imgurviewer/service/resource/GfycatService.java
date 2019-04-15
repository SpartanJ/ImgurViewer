package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.GfycatResource;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

public class GfycatService extends MediaServiceSolver
{
	public static final String TAG = GfycatService.class.getCanonicalName();
	private static final String GFYCAT_DOMAIN = "gfycat.com";
	private static final String GFYCAT_INFO_URL = "https://api.gfycat.com/v1/gfycats/";
	
	private String getResourceName( Uri uri )
	{
		String resourceName = uri.getLastPathSegment();
		
		if ( null != resourceName )
		{
			int index = resourceName.indexOf( '-' );
			
			if ( -1 != index )
			{
				return resourceName.substring( 0, index );
			}
		}
		
		return resourceName;
	}
	
	private String getResourcePath( Uri uri )
	{
		return GFYCAT_INFO_URL + getResourceName( uri );
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeJsonRequest( Request.Method.GET, getResourcePath( uri ), new ResponseListener<GfycatResource>()
		{
			@Override
			public void onRequestSuccess( Context context, GfycatResource resource )
			{
				pathResolverListener.onPathResolved( resource.item.getUri(), UriUtils.guessMediaTypeFromUri( resource.item.getUri() ), uri );
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( TAG, errorMessage );
				
				pathResolverListener.onPathError( errorMessage );
			}
		} );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( GFYCAT_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
