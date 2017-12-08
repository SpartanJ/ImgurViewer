package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class GiphyVideo
{
	@SerializedName( "url" )
	protected String url;
	
	@SerializedName( "mp4" )
	protected String mp4;
	
	@SerializedName( "webp" )
	protected String webp;
	
	@SerializedName( "width" )
	protected int width;
	
	@SerializedName( "height" )
	protected int height;
	
	public String getUrl()
	{
		return url;
	}
	
	public String getMp4()
	{
		return mp4;
	}
	
	public String getWebp()
	{
		return webp;
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
		if ( null != mp4 )
		{
			return Uri.parse( mp4 );
		}
		else if ( null != webp )
		{
			return Uri.parse( webp );
		}
		
		return Uri.parse( url );
	}
}
