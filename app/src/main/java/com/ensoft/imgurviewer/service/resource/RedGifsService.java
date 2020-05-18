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
		return new String[] { "<source src=\"", "<source src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type=\"video/mp4\"/>", "\" type=\"video/webm\"/>" };
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/watch/" };
	}
}
