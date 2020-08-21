package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpankBangService extends BasicVideoServiceSolver
{
	public static final String SPANK_BANG_DOMAIN = "spankbang.com";
	private static final String SPANK_BANG_API = "https://spankbang.com/api/videos/stream";
	
	@Override
	public String getDomain()
	{
		return SPANK_BANG_DOMAIN;
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] {};
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] {};
	}
	
	@Override
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
				String dataStreamKey = StringUtils.getFirstStringMatch( response,"data-streamkey=\"", "\">" );
				String csrfSession = null;
				List<HttpCookie> cookieList = RequestService.getInstance().getCookieManager().getCookieStore().get( URI.create( uri.toString() ) );
				
				for ( HttpCookie cookie : cookieList )
				{
					if ( "sb_session".equals( cookie.getName() ) )
					{
						csrfSession = cookie.getValue();
					}
				}
				
				if ( null != dataStreamKey && null != csrfSession )
				{
					Map<String, String> headers = new HashMap<>();
					headers.put( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/79.0" );
					headers.put( "Referer", uri.toString() );
					headers.put( "Cookie", "sb_session=" + csrfSession + ";" );
					
					String body = "id=" + dataStreamKey;
					
					StringRequest postRequest = new StringRequest(Request.Method.POST, SPANK_BANG_API,
						response1 -> {
							String[] starts = new String[]{
								"\"1080p\":[\"",
								"\"720p\":[\"",
								"\"480p\":[\"",
								"\"320p\":[\"",
								"\"240p\":[\"",
								"\"stream_url_1080p\":[\"",
								"\"stream_url_720p\":[\"",
								"\"stream_url_480p\":[\"",
								"\"stream_url_320p\":[\"",
								"\"stream_url_240p\":[\"",
							};
							String[] needleEnds = new String[]{
								"\",",
								"\"]"
							};
							
							for ( String needleEnd : needleEnds )
							{
								for ( String search : starts )
								{
									String url = StringUtils.getFirstStringMatch( response1, search, needleEnd );
									
									if ( url != null && !url.isEmpty() )
									{
										Uri videoUrl = Uri.parse( url );
										sendPathResolved( pathResolverListener, videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), referer );
										return;
									}
								}
							}
							
							sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
						},
						error -> sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url )
					) {
						@Override
						public Map<String, String> getHeaders()
						{
							return headers;
						}
						
						@Override
						public byte[] getBody()
						{
							return body.getBytes();
						}
					};
					
					RequestService.getInstance().getRequestQueue().add( postRequest );
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
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return super.isServicePath( uri ) || uri.toString().contains( SPANK_BANG_DOMAIN );
	}
}
