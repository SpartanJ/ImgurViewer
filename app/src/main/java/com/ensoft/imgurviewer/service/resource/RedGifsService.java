package com.ensoft.imgurviewer.service.resource;

public class RedGifsService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "redgifs.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<meta property=\"og:video\" content=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\">" };
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/watch/" };
	}
}
