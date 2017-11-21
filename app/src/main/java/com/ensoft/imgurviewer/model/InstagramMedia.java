package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class InstagramMedia
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

	public Uri getUri()
	{
		return Uri.parse( url );
	}
}
