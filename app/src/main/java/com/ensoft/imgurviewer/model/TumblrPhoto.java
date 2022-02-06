package com.ensoft.imgurviewer.model;

import android.net.Uri;
import android.text.TextUtils;

public class TumblrPhoto
{
	protected String url;
	
	protected String type;
	
	protected int width;
	
	protected int height;
	
	public String getUrl()
	{
		return url;
	}
	
	public String getType()
	{
		return type;
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
		return !TextUtils.isEmpty( url ) ? Uri.parse( url ) : null;
	}
}
