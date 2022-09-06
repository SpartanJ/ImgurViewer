package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
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

public abstract class BasicVideoServiceSolver extends MediaServiceSolver
{
	protected Uri referer;
	
	public interface VideoPathSolved
	{
		void onVideoPathSolved( Uri uri, PathResolverListener pathResolverListener );
	}
	
	protected VideoPathSolved getVideoPathSolved()
	{
		return null;
	}
	
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
	
	protected HashMap<String, String> getHeaders( Uri referer )
	{
		HashMap<String,String> headers = new HashMap<>();
		headers.put( "Origin", referer.getScheme() + "://" + referer.getHost() );
		headers.put( "Referer", referer.toString() );
		headers.put( "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" );
		headers.put( "Accept-Language", "en-us,en;q=0.5" );
		headers.put( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
		headers.put( "User-Agent", UriUtils.getDefaultUserAgent() );
		return headers;
	}
	
	protected Uri getFirstVideoUrlFromResponse( String response )
	{
		String[] needleStart = getNeedleStart();
		String[] needleEnd = getNeedleEnd();
		
		for ( int i = 0; i < needleStart.length; i++ )
		{
			String qualityUrl = StringUtils.getFirstStringMatch( response, needleStart[i], ( i < needleEnd.length ) ? needleEnd[i] : needleEnd[0] );
			
			if ( !TextUtils.isEmpty( qualityUrl ) )
			{
				return Uri.parse( parseUrlString( qualityUrl ) );
			}
		}
		
		return null;
	}
	
	protected String getStringMatch( String haystack, String needleStart, String needleEnds )
	{
		return StringUtils.getLastStringMatch( haystack, needleStart, needleEnds );
	}
	
	protected String getStringFromResponse( String response )
	{
		String[] needleStart = getNeedleStart();
		String[] needleEnd = getNeedleEnd();
		
		for ( int i = 0; i < needleStart.length; i++ )
		{
			String qualityUrl = getStringMatch( response, needleStart[i], ( i < needleEnd.length ) ? needleEnd[i] : needleEnd[0] );
			
			if ( !TextUtils.isEmpty( qualityUrl ) )
			{
				return qualityUrl;
			}
		}
		
		return null;
	}
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		String[] needleStart = getNeedleStart();
		String[] needleEnd = getNeedleEnd();
		
		for ( int i = 0; i < needleStart.length; i++ )
		{
			String qualityUrl = getStringMatch( response, needleStart[i], ( i < needleEnd.length ) ? needleEnd[i] : needleEnd[0] );
			
			if ( !TextUtils.isEmpty( qualityUrl ) )
			{
				return Uri.parse( parseUrlString( qualityUrl ) );
			}
		}
		
		return null;
	}
	
	protected ResponseListener<String> getResponseListener( Uri uri, PathResolverListener pathResolverListener )
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
					if ( getVideoPathSolved() == null )
					{
						sendPathResolved( pathResolverListener, videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), referer );
					}
					else
					{
						getVideoPathSolved().onVideoPathSolved( videoUrl, pathResolverListener );
					}
				}
				else
				{
					sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				if ( null != getDomain() && null != errorMessage )
					Log.v( getDomain(), errorMessage );
				
				sendPathError( uri, pathResolverListener, null != errorMessage ? errorMessage : "" );
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
		
		RequestService.getInstance().makeStringRequest( getRequestMethod(), parseReferer( uri ), getResponseListener( uri, pathResolverListener ), getParameters(), getHeaders( uri ) );
	}
	
	public String[] getDomainPath()
	{
		return new String[] { "" };
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		for ( String path : getDomainPath() )
		{
			if ( UriUtils.uriMatchesDomain( uri, getDomain(), path ) )
				return true;
		}
		
		return false;
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
}
