package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.DeviantArtImageModel;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

import java.util.List;

public class DeviantArtService extends MediaServiceSolver
{
	public static final String DEVIANTART_DOMAIN = "deviantart.com";
	public static final String OEMBED_URL = "https://backend.deviantart.com/oembed?url=";

	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		// Normalize the URI to the modern www.deviantart.com/username/... format
		String host = uri.getHost().toLowerCase();
		String normalizedUrl = uri.toString();

		if (host.endsWith("." + DEVIANTART_DOMAIN) && !host.equals("www." + DEVIANTART_DOMAIN) && !host.equals(DEVIANTART_DOMAIN)) {
			String username = host.substring(0, host.length() - ("." + DEVIANTART_DOMAIN).length());
			normalizedUrl = "https://www." + DEVIANTART_DOMAIN + "/" + username + uri.getPath();
		}

		RequestService.getInstance().makeJsonRequest( Request.Method.GET, OEMBED_URL + normalizedUrl, new ResponseListener<DeviantArtImageModel>()
		{
			@Override
			public void onRequestSuccess( Context context, DeviantArtImageModel response )
			{
				pathResolverListener.onPathResolved( response.getUri(), MediaType.IMAGE, response.getThumbnailUri() );
			}

			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( DEVIANTART_DOMAIN, errorMessage );

				pathResolverListener.onPathError( uri, errorMessage );
			}
		} );
	}

	@Override
	public boolean isServicePath( Uri uri )
	{
		if ( uri.toString().contains( DEVIANTART_DOMAIN ) )
		{
			if ( uri.toString().contains( DEVIANTART_DOMAIN + "/art/" ) )
			{
				return true;
			}
			else
			{
				List<String> segments = uri.getPathSegments();

				return segments.size() >= 2 && segments.get( segments.size() - 2 ).equals( "art" );
			}
		}

		return false;
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}