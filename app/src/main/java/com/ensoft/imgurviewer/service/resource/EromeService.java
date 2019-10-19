package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

public class EromeService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "erome.com";
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/a/", "/i/" };
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<source src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type='video/mp4'" };
	}
	
	@Override
	protected String parseUrlString( String urlString )
	{
		return urlString;
	}
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		return getFirstVideoUrlFromResponse( response );
	}
}
