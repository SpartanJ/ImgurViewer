package com.ensoft.imgurviewer.service.resource;

public class GyazoVideoService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "gyazo.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\"></video>" };
	}
}
