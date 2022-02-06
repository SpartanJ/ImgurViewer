package com.ensoft.imgurviewer.service.resource;

public class ClippitUserService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "clippituser.tv";
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/c/" };
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "data-hd-file=\"", "data-sd-file=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\"" };
	}
}
