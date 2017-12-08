package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class StreamableVideoType
{
	@SerializedName( "url" )
	protected String url;
	
	@SerializedName( "width" )
	protected int width;
	
	@SerializedName( "height" )
	protected int height;
	
	public String getUrl()
	{
		return url;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
