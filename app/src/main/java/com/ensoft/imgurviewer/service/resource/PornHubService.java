package com.ensoft.imgurviewer.service.resource;

public class PornHubService extends BasicVideoServiceSolver
{
	public static final String PORNHUB_DOMAIN = "pornhub.com";
	
	@Override
	public String getDomain()
	{
		return PORNHUB_DOMAIN;
	}
	
	@Override
	public String getDomainPath()
	{
		return "/view_video.php?";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "\"quality\":\"1440\"", "\"quality\":\"1080\"", "\"quality\":\"720\"", "\"quality\":\"480\"", "\"quality\":\"240\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\",\"videoUrl\":\"", "\"}" };
	}
	
	@Override
	protected String parseUrlString( String urlString )
	{
		return urlString.replaceAll( "\\\\", "" );
	}
}
