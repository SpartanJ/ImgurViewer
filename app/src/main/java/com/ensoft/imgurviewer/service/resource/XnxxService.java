package com.ensoft.imgurviewer.service.resource;

public class XnxxService extends XVideosService
{
	@Override
	public String getDomain()
	{
		return "xnxx.com";
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/video" };
	}
}
