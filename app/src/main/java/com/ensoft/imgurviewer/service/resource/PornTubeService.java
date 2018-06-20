package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.PornTubeVideos;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class PornTubeService extends BasicVideoServiceSolver
{
	public static String API_PATH = "https://www.porntube.com/api/videos/";
	
	@Override
	public String getDomain()
	{
		return "porntube.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "" };
	}
	
	@Override
	protected String parseReferer( Uri referer )
	{
		String url = referer.toString();
		int pos = url.lastIndexOf( '_' );
		
		if ( -1 != pos )
		{
			String id = url.substring( pos + 1 );
			
			return API_PATH + id;
		}
		
		return "";
	}
	
	protected ResponseListener<String> getResponseListener( PathResolverListener pathResolverListener )
	{
		return new ResponseListener<String>()
		{
			@Override
			public void onRequestSuccess( Context context, String response )
			{
				try
				{
					int pos = response.lastIndexOf( "\"annotations\":[" );
					
					if ( -1 != pos )
					{
						String id = StringUtils.getFirstStringMatch( response.substring( pos ), "\"mediaId\":", "," );
						String url = "https://tkn.kodicdn.com/" + id + "/desktop/240+360+480+720+1080";
						
						RequestService.getInstance().makeJsonRequest( Request.Method.POST, url, new ResponseListener<PornTubeVideos>()
						{
							@Override
							public void onRequestSuccess( Context context, PornTubeVideos response )
							{
								String videoUrl = "";
								
								if ( null != response )
								{
									if ( null != response.v1080 ) videoUrl = response.v1080.token;
									else if ( null != response.v720 ) videoUrl = response.v720.token;
									else if ( null != response.v480 ) videoUrl = response.v480.token;
									else if ( null != response.v240 ) videoUrl = response.v240.token;
								}
								
								if ( !TextUtils.isEmpty( videoUrl ) )
								{
									final Uri videoUri = Uri.parse( videoUrl );
									
									new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( videoUri, UriUtils.guessMediaTypeFromUri( videoUri ), referer ) );
								}
								else
								{
									new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( context.getString( R.string.could_not_resolve_video_url ) ) );
								}
							}
							
							@Override
							public void onRequestError( Context context, int errorCode, String errorMessage )
							{
								new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( context.getString( R.string.could_not_resolve_video_url ) ) );
							}
						}, getParameters(), getHeaders( referer ), null );
					}
					else
					{
						new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( context.getString( R.string.could_not_resolve_video_url ) ) );
					}
				}
				catch ( Exception e )
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
}
