package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TwitchClip
{
	@SerializedName( "quality" )
	protected String quality;
	
	@SerializedName( "source" )
	protected String source;
	
	public String getQuality()
	{
		return quality;
	}
	
	public String getSource()
	{
		return source;
	}
	
	
	public boolean is1080p()
	{
		return "1080".equals( quality );
	}
	
	public boolean is720p()
	{
		return "720".equals( quality );
	}
}
