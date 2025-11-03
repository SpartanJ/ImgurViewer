package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.model.TumblrImageResponse;
import com.ensoft.imgurviewer.model.TumblrMedia;
import com.ensoft.imgurviewer.model.TumblrPhoto;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.imgurviewer.R;

public class TumblrService extends MediaServiceSolver
{
	private static final String TAG = TumblrService.class.getCanonicalName();
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeStringRequest( Request.Method.GET, uri.toString(), new ResponseListener<String>()
		{
			@Override
			public void onRequestSuccess( Context context, String response )
			{
				String jsonData = StringUtils.getLastStringMatch( response, "<script type=\"application/json\" id=\"___INITIAL_STATE___\">", "</script>" );
				TumblrMedia tumblrMedia = null;
				
				if ( !TextUtils.isEmpty( jsonData ) )
				{
					jsonData = jsonData.trim();

					try
					{
						tumblrMedia = new Gson().fromJson( jsonData, TumblrMedia.class );
					}
					catch ( JsonSyntaxException e )
					{
						sendPathError( uri, pathResolverListener, e.toString() );
						return;
					}
				}
				
				if ( null != tumblrMedia && null != tumblrMedia.imagePage.photo.photos && tumblrMedia.imagePage.photo.photos.length > 0 )
				{
					TumblrPhoto photo = tumblrMedia.imagePage.photo.photos[0];
					
					sendPathResolved( pathResolverListener, photo.getUri(), photo.getType().contains( "image" ) ? MediaType.IMAGE : MediaType.VIDEO_MP4, null );
				}
				else
				{
					sendPathError( uri, pathResolverListener, R.string.unknown_error );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( TAG, errorMessage );
				
				sendPathError( uri, pathResolverListener, errorMessage );
			}
		} );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.getHost().contains( "tumblr.com" ) && null != uri.getPathSegments() &&
			uri.getPathSegments().size() > 0 &&
			( "image".contains( uri.getPathSegments().get( 0 ) ) || "video".contains( uri.getPathSegments().get( 0 ) ) );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
