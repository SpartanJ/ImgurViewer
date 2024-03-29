package com.ensoft.imgurviewer.model;

import android.net.Uri;

public class VimeoVideo
{
	protected String url;
	protected String quality;
	protected String id;
	protected long width;
	protected long height;
	
	public String getUrl()
	{
		return url;
	}
	
	public Uri getUri()
	{
		return Uri.parse( url );
	}
	
	public String getQuality()
	{
		return quality;
	}
	
	public String getId()
	{
		return id;
	}
	
	public long getWidth()
	{
		return width;
	}
	
	public long getHeight()
	{
		return height;
	}
}
