package com.ensoft.imgurviewer.service.resource;

public class YouPornService extends RedTubeService
{
	@Override
	public String getDomain()
	{
		return "youporn.com";
	}
	
	@Override
	public String getDomainPath()
	{
		return "/watch/";
	}
}
