package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.StreamffModel;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class StreamffService extends MediaServiceSolver
{
	public static String STREAMFF_API = "https://streamff.com/api/videos/";
	
	private String getId( Uri uri )
	{
		return uri.getLastPathSegment();
	}
	
	private String getApiUrl( Uri uri )
	{
		return STREAMFF_API + getId( uri );
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeJsonRequest( Request.Method.GET, getApiUrl( uri ), new ResponseListener<StreamffModel>()
		{
			@Override
			public void onRequestSuccess( Context context, StreamffModel response )
			{
				sendPathResolved( pathResolverListener, response.getExternalLink(), UriUtils.guessMediaTypeFromUri( response.getExternalLink() ), uri );
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
		return uri.toString().toLowerCase().startsWith( "https://streamff.com/v/" );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
