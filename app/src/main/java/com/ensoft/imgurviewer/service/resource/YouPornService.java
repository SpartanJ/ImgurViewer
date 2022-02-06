package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.YouPornVideo;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class YouPornService extends MediaServiceSolver
{
	private static final String YOUPORN_DOMAIN = "youporn.com";
	private static final String YOUPORN2_DOMAIN = "www.youporn.com";
	private static final String YOUPORN_API = "https://www.youporn.com/api/video/media_definitions/";
	
	protected String getVideoId( Uri uri )
	{
		return uri.getPathSegments().get( 1 );
	}
	
	protected String getApiUrl( Uri uri )
	{
		return YOUPORN_API + getVideoId( uri ) + "/";
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeJsonArrayRequest( Request.Method.GET, getApiUrl( uri ), new ResponseListener<YouPornVideo[]>()
		{
			@Override
			public void onRequestSuccess( Context context, YouPornVideo[] response )
			{
				sendPathResolved( pathResolverListener, response[0].getVideoUri(), UriUtils.guessMediaTypeFromUri( response[0].getVideoUri() ), uri );
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
			}
		} );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.getHost() != null &&
			( uri.getHost().equalsIgnoreCase( YOUPORN_DOMAIN ) || uri.getHost().equalsIgnoreCase( YOUPORN2_DOMAIN ) ) &&
			uri.getPathSegments().size() > 1 && "watch".equalsIgnoreCase( uri.getPathSegments().get( 0 ) );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
