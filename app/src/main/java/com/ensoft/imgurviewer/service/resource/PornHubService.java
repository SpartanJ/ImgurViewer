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
		return new String[] { "\"quality\":\"1440\",\"videoUrl\":\"", "\"quality\":\"1080\",\"videoUrl\":\"", "\"quality\":\"720\",\"videoUrl\":\"", "\"quality\":\"480\",\"videoUrl\":\"", "\"quality\":\"240\",\"videoUrl\":\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\"}" };
	}
	
	@Override
	protected String parseUrlString( String urlString )
	{
		return urlString.replaceAll( "\\\\", "" );
	}
}
