package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public abstract class BasicVideoServiceSolver extends ImageServiceSolver
{
	public abstract String getDomain();
	
	public abstract String[] getNeedleStart();
	
	public abstract String[] getNeedleEnd();
	
	protected String parseUrlString( String urlString )
	{
		return urlString;
	}
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		String[] needleStart = getNeedleStart();
		String[] needleEnd = getNeedleEnd();
		
		for ( int i = 0; i < needleStart.length; i++ )
		{
			String qualityUrl = StringUtils.getStringMatch( response, needleStart[i], ( i < needleEnd.length ) ? needleEnd[i] : needleEnd[0] );
			
			if ( !TextUtils.isEmpty( qualityUrl ) )
			{
				return Uri.parse( parseUrlString( qualityUrl ) );
			}
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		RequestService.getInstance().makeStringRequest( Request.Method.GET, uri.toString(), new ResponseListener<String>()
		{
			@Override
			public void onRequestSuccess( Context context, String response )
			{
				Uri videoUrl = getVideoUrlFromResponse( response );
				
				if ( videoUrl != null )
				{
					new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( videoUrl, null ) );
				}
				else
				{
					new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( context.getString( R.string.could_not_resolve_video_url ) ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				Log.v( getDomain(), errorMessage );
				
				new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( errorMessage ) );
			}
		} );
	}
	
	public String getDomainPath()
	{
		return "";
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, getDomain(), getDomainPath() );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
	
	@Override
	public boolean isVideo( Uri uri )
	{
		return true;
	}
	
	@Override
	public boolean isVideo( String uri )
	{
		return true;
	}
}
