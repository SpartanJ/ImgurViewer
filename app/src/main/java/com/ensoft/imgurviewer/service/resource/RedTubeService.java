package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.YouPornVideo;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class RedTubeService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "redtube.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "{\"format\":\"mp4\",\"videoUrl\":\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\",\"remote\":true}" };
	}
	
	@Override
	protected String parseUrlString( String urlString )
	{
		return urlString.replaceAll( "\\\\", "" );
	}
	
	@Override
	protected VideoPathSolved getVideoPathSolved()
	{
		return ( uri, pathResolverListener ) -> RequestService.getInstance().makeJsonArrayRequest( Request.Method.GET, uri.toString(), new ResponseListener<YouPornVideo[]>()
		{
			@Override
			public void onRequestSuccess( Context context, YouPornVideo[] response )
			{
				sendPathResolved( pathResolverListener, response[0].getVideoUri(), UriUtils.guessMediaTypeFromUri( response[0].getVideoUri() ), uri );
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
			}
		} );
	}
}
