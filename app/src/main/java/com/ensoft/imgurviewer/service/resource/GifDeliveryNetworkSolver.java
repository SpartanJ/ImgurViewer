package com.ensoft.imgurviewer.service.resource;

public class GifDeliveryNetworkSolver extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "gifdeliverynetwork.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<source id=\"mp4Source\" src=\"", "<source id=\"webmSource\" src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type=\"video/mp4\">", "\" type=\"video/webm\">" };
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/" };
	}
}

