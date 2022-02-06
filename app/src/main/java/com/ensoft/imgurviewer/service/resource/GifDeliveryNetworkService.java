package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.GfycatResource;
import com.ensoft.imgurviewer.model.GifDeliveryNetworkAccessToken;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.helper.RequestParameters;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import org.json.JSONObject;

import java.util.Map;

public class GifDeliveryNetworkService extends BasicVideoServiceSolver
{
	public static String API_GFYCATS = "https://api.redgifs.com/v1/gfycats/";
	public static String API_REDGIFS = "https://api.redgifs.com/v2/gifs/"; // TODO: Upgrade to the new API
	
	@Override
	public String getDomain()
	{
		return "gifdeliverynetwork.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<script src=\"/assets/js/app." };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { ".js\"" };
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/" };
	}
	
	protected String getClipId( Uri uri )
	{
		return null != uri.getLastPathSegment()? uri.getLastPathSegment().toLowerCase() : "";
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
				String slug = getClipId( uri );
				
				RequestService.getInstance().makeJsonRequest( Request.Method.GET, API_GFYCATS + slug, new ResponseListener<GfycatResource>()
				{
					@Override
					public void onRequestSuccess( Context context, GfycatResource response )
					{
						if (null != response.item)
						{
							String url = response.item.getUrl();
							
							if ( !TextUtils.isEmpty( url ) )
							{
								Uri videoUrl = Uri.parse( url );
								sendPathResolved( pathResolverListener, videoUrl, UriUtils.guessMediaTypeFromUri( videoUrl ), referer );
							}
							else
							{
								sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
							}
						}
						else
						{
							sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
						}
					}
					
					public void onRequestError( Context context, int errorCode, String errorMessage )
					{
						sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
					}
				}, getParameters() );
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

