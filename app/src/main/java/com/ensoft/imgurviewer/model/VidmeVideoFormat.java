package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class VidmeVideoFormat
{
	@SerializedName( "uri" )
	protected String uri;

	@SerializedName( "width" )
	protected int width;

	@SerializedName( "height" )
	protected int height;

	@SerializedName( "type" )
	protected String type;

	public String getUri()
	{
		return uri;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String getType()
	{
		return type;
	}
}
