package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TwitchClip
{
	@SerializedName( "quality" )
	protected String quality;
	
	@SerializedName( "sourceURL" )
	protected String source;
	
	public String getQuality()
	{
		return quality;
	}
	
	public String getSource()
	{
		return source;
	}
}
