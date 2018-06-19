package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

public class SpankBangService extends BasicVideoServiceSolver
{
	public static final String SPANK_BANG_DOMAIN = "spankbang.com";
	
	@Override
	public String getDomain()
	{
		return SPANK_BANG_DOMAIN;
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "var stream_url_1080p  = '", "var stream_url_720p  = '", "var stream_url_480p  = '", "var stream_url_320p  = '", "var stream_url_240p  = '" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "';" };
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return super.isServicePath( uri ) || uri.toString().contains( SPANK_BANG_DOMAIN );
	}
}
