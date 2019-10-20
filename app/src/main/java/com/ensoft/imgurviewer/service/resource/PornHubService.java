package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import java.util.Map;

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
	protected Map<String, String> getHeaders( Uri referer )
	{
		Map<String, String> headers = super.getHeaders( referer );
		headers.put( "Cookie", "age_verified: 1; platform: tv;" );
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
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		return Uri.EMPTY;
	}
}
