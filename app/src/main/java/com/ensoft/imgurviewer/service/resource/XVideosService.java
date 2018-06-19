package com.ensoft.imgurviewer.service.resource;

public class XVideosService extends BasicVideoServiceSolver
{
	public static final String XVIDEOS_DOMAIN = "xvideos.com";
	
	@Override
	public String getDomain()
	{
		return XVIDEOS_DOMAIN;
	}
	
	@Override
	public String getDomainPath()
	{
		return "/video";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "html5player.setVideoUrlHigh('", "html5player.setVideoHLS('", "html5player.setVideoUrlLow('" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "');" };
	}
}
