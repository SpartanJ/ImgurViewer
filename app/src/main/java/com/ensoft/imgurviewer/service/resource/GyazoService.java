package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.GyazoOEmbed;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class GyazoService extends MediaServiceSolver
{
	public static final String TAG = GyazoService.class.getCanonicalName();
	private static final String GYAZO_DOMAIN = "gyazo.com";
	private static final String GYAZO_API_URL = "https://api.gyazo.com/api";
	private static final String GYAZO_GET_IMAGE_URL = GYAZO_API_URL + "/oembed?url=";
	private static final String GYAZO_PLAYER_URL = "https://gyazo.com/player/";
	
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		String oEmbedUrl = GYAZO_GET_IMAGE_URL + uri.toString();
		
		RequestService.getInstance().makeJsonRequest( Request.Method.GET, oEmbedUrl, new ResponseListener<GyazoOEmbed>()
		{
			@Override
			public void onRequestSuccess( Context context, GyazoOEmbed response )
			{
				if ( null != response && null != response.getUri() && !TextUtils.isEmpty( response.getUri().toString() ) )
				{
					pathResolverListener.onPathResolved( response.getUri(), UriUtils.guessMediaTypeFromUri( response.getUri() ), uri );
				}
				else if ( null != response && "video".equals( response.getType() ) && !TextUtils.isEmpty( response.getHtml() ) && response.getHtml().startsWith( "<iframe" ) )
				{
					Uri playerUri = Uri.parse( GYAZO_PLAYER_URL + uri.getLastPathSegment() );
					
					new GyazoVideoService().getPath( playerUri, new PathResolverListener( GyazoService.this )
					{
						@Override
						public void onPathResolved( Uri url, MediaType mediaType, Uri thumbnailOrReferer, Object additionalData )
						{
							pathResolverListener.onPathResolved( url, mediaType, thumbnailOrReferer );
						}
						
						@Override
						public void onPathError( Uri uri, String error )
						{
							Log.v( TAG, error );
							
							pathResolverListener.onPathError( uri, error );
						}
					} );
				}
				else
				{
					pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.could_not_resolve_url ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( TAG, errorMessage );
				
				pathResolverListener.onPathError( uri, errorMessage );
			}
		} );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( GYAZO_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
