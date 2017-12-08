package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class GfycatVideo
{
	@SerializedName( "gfyId" )
	protected String id;
	
	@SerializedName( "gfyName" )
	protected String name;
	
	@SerializedName( "width" )
	protected int width;
	
	@SerializedName( "height" )
	protected int height;
	
	@SerializedName( "mp4Url" )
	protected String url;
	
	@SerializedName( "webmUrl" )
	protected String webmUrl;
	
	public GfycatVideo( String id, String name, int width, int height, String url, String webmUrl )
	{
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.url = url;
		this.webmUrl = webmUrl;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getWebmUrl()
	{
		return webmUrl;
	}
	
	public Uri getUri()
	{
		return Uri.parse( url );
	}
}
