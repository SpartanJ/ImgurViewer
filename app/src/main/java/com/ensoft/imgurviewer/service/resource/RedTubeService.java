package com.ensoft.imgurviewer.service.resource;

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
		return new String[] { "\"quality\":\"1080\",\"videoUrl\":\"", "\"quality\":\"720\",\"videoUrl\":\"", "\"quality\":\"480\",\"videoUrl\":\"", "\"quality\":\"240\",\"videoUrl\":\"" };
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
