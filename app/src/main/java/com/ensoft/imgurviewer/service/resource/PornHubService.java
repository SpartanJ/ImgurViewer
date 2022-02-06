package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.YouPornVideo;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.helper.RequestParameters;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.rest.response.Header;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornHubService extends BasicVideoServiceSolver
{
	public static final String PORNHUB_DOMAIN = "pornhub.com";
	
	@Override
	public String getDomain()
	{
		return PORNHUB_DOMAIN;
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/view_video.php?" };
	}
	
	@Override
	protected HashMap<String, String> getHeaders( Uri referer )
	{
		HashMap<String, String> headers = super.getHeaders( referer );
		headers.put( "Cookie", "platform=pc;" );
		return headers;
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
	
	private Map<String, String> extractUrls( String response )
	{
		HashMap<String, String> map = new HashMap<>(  );
		HashMap<String, String> res = new HashMap<>(  );
		ArrayList<String> list = new ArrayList<>();
		Pattern regex = Pattern.compile( "(var\\s+(?:media|quality)_.+)" );
		Matcher m = regex.matcher(response);
		while ( m.find() )
		{
			for ( int i = 0; i < m.groupCount(); i++ )
			{
				list.addAll( Arrays.asList( m.group( i ).split( ";" ) ) );
			}
		}
		
		for ( String var: list )
		{
			var = var.substring( 4 );
			String[] split = var.split( "=" );
			String valuePart = split[1];
			if ( split.length > 2 )
			{
				for ( int i = 2; i < split.length; i++ )
				{
					valuePart += "=" + split[i];
				}
			}
			valuePart = valuePart.replaceAll( "/\\*(?:(?!\\*/).)*?\\*/", "" )
				.trim()
				.replaceAll( "\"", "" );
			
			map.put( split[0], valuePart );
		}
		
		
		for ( Map.Entry<String, String> entry : map.entrySet() )
		{
			if ( entry.getKey().startsWith( "quality" ) || entry.getKey().startsWith( "media" ) )
			{
				String[] values = entry.getValue().split( "\\+" );
				StringBuilder solved = new StringBuilder();
				
				for ( String value : values )
				{
					value = value.trim().replaceAll( " \\+ ", "" );
					String newVal = map.get( value );
					if ( newVal != null )
					{
						newVal = newVal.trim().replaceAll( " \\+ ", "" );
						solved.append( newVal );
					}
				}
				
				entry.setValue( solved.toString() );
				
				res.put( entry.getKey(), entry.getValue() );
			}
		}
		
		return res;
	}
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		Map<String, String> vars = extractUrls( response );
		
		if ( !vars.isEmpty() )
		{
			for ( Map.Entry<String, String> entry : vars.entrySet() )
			{
				if ( "media_1".equalsIgnoreCase( entry.getKey() ) )
				{
					return Uri.parse( entry.getValue() );
				}
			}
		}
		
		return null;
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
				Uri videoUrl = getVideoUrlFromResponse( response );
				
				if ( videoUrl != null )
				{
					sendPathResolved( pathResolverListener, videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), uri );
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
}
