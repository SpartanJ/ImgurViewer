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
import com.imgurviewer.R;

public class GfycatService extends BasicVideoServiceSolver
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
	public String getDomain()
	{
		return GFYCAT_DOMAIN;
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<source id=\"mp4Source\" src=\"", "<source id=\"webmSource\" src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type=\"video/mp4\">", "\" type=\"video/webm\">" };
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
				
				RequestService.getInstance().makeStringRequest( Request.Method.GET, uri.toString(), new ResponseListener<String>()
				{
					@Override
					public void onRequestSuccess( Context context, String response )
					{
						Uri videoUrl = getVideoUrlFromResponse( response );
						
						if ( videoUrl != null )
						{
							sendPathResolved( pathResolverListener, videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), referer );
						}
						else
						{
							sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
						}
					}
					
					public void onRequestError( Context context, int errorCode, String errorMessage )
					{
						sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
					}
				} );
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
