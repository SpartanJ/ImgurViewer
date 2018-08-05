package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.text.TextUtils;

import com.ensoft.imgurviewer.model.XHamsterVideo;
import com.ensoft.imgurviewer.model.XHamsterVideoMp4;
import com.ensoft.imgurviewer.service.StringUtils;
import com.google.gson.Gson;

public class XHamsterService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "xhamster.com";
	}
	
	@Override
	public String getDomainPath()
	{
		return "/videos";
	}
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		try
		{
			String needleStart = getNeedleStart()[0];
			String needleEnd = getNeedleEnd()[0];
			String jsonString = StringUtils.getStringMatch( response, needleStart, needleEnd );
			
			if ( !TextUtils.isEmpty( jsonString ) )
			{
				XHamsterVideo xHamsterVideo = new Gson().fromJson( jsonString + "}", XHamsterVideo.class );
				
				if ( null != xHamsterVideo )
				{
					XHamsterVideoMp4 xHamsterVideoMp4 = xHamsterVideo.xHamsterVideoModel.xHamsterVideoSources.xHamsterVideoMp4;
					
					if ( !TextUtils.isEmpty( xHamsterVideoMp4.v1080p ) ) return Uri.parse( xHamsterVideoMp4.v1080p );
					else if ( !TextUtils.isEmpty( xHamsterVideoMp4.v720p ) ) return Uri.parse( xHamsterVideoMp4.v720p );
					else if ( !TextUtils.isEmpty( xHamsterVideoMp4.v480p ) ) return Uri.parse( xHamsterVideoMp4.v480p );
					else if ( !TextUtils.isEmpty( xHamsterVideoMp4.v240p ) ) return Uri.parse( xHamsterVideoMp4.v240p );
				}
			}
			
			return null;
		}
		catch ( Exception e )
		{
			return null;
		}
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "window.initials = " };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "};" };
	}
}
