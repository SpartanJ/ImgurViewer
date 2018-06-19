package com.ensoft.imgurviewer.service.resource;

public class StreamjaService extends BasicVideoServiceSolver
{
	public static final String STREAMJA_DOMAIN = "streamja.com";
	
	@Override
	public String getDomain()
	{
		return STREAMJA_DOMAIN;
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<source src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type=\"video/mp4\">" };
	}
}
