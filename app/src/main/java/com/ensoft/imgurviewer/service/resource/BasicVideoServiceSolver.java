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
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicVideoServiceSolver extends MediaServiceSolver
{
	protected Uri referer;
	
	public abstract String getDomain();
	
	public abstract String[] getNeedleStart();
	
	public abstract String[] getNeedleEnd();
	
	protected String parseUrlString( String urlString )
	{
		return urlString;
	}
	
	protected String parseReferer( Uri referer )
	{
		return referer.toString();
	}
	
	protected JSONObject getParameters()
	{
		return new JSONObject();
	}
	
	protected Map<String, String> getHeaders( Uri referer )
	{
		HashMap<String,String> headers = new HashMap<>();
		headers.put( "Origin", referer.getScheme() + "://" + referer.getHost() );
		headers.put( "Referer", referer.toString() );
		headers.put( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" );
		headers.put( "Accept-Language", "en-us,en;q=0.5" );
		headers.put( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
		headers.put( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0 (Chrome)" );
		return headers;
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
	
	protected ResponseListener<String> getResponseListener( PathResolverListener pathResolverListener )
	{
		return new ResponseListener<String>()
		{
			@Override
			public ThreadMode getThreadMode()
			{
				return ThreadMode.ASYNC;
			}
			
			@Override
			public void onRequestSuccess( Context context, String response )
			{
				Uri videoUrl = getVideoUrlFromResponse( response );
				
				if ( videoUrl != null )
				{
					new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), referer ) );
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
		};
	}
	
	protected int getRequestMethod()
	{
		return Request.Method.GET;
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		referer = uri;
		
		RequestService.getInstance().makeStringRequest( getRequestMethod(), parseReferer( uri ), getResponseListener( pathResolverListener ), getParameters(), getHeaders( uri ) );
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
