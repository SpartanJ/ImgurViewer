package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.DeviantArtImageModel;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

public class DeviantArtService extends MediaServiceSolver
{
	public static final String DEVIANTART_DOMAIN = "deviantart.com";
	public static final String OEMBED_URL = "https://backend.deviantart.com/oembed?url=";
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeJsonRequest( Request.Method.GET, OEMBED_URL + Uri.encode( uri.toString() ), new ResponseListener<DeviantArtImageModel>()
		{
			@Override
			public void onRequestSuccess( Context context, DeviantArtImageModel response )
			{
				new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( response.getUri(), MediaType.IMAGE, response.getThumbnailUri() ) );
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( DEVIANTART_DOMAIN, errorMessage );
				
				new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( errorMessage ) );
			}
		} );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( DEVIANTART_DOMAIN + "/art/" );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
