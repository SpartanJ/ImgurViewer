package com.ensoft.imgurviewer.model;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class TumblrPhoto
{
	@SerializedName( "mediaUrlTemplate" )
	protected String url;
	
	protected String type;
	
	protected int width;
	
	protected int height;
	
	@SerializedName( "sizes" )
	protected HashMap<String, TumblrSizes> sizes;
	
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
	
	protected TumblrSizes getBiggestSize()
	{
		TumblrSizes biggestSize = null;
		
		for ( TumblrSizes tumblrSize : sizes.values() )
		{
			if ( null == biggestSize || tumblrSize.width > biggestSize.width )
			{
				biggestSize = tumblrSize;
			}
		}
		
		return biggestSize;
	}
	
	public Uri getUri()
	{
		if ( null != url )
		{
			TumblrSizes biggestSize = getBiggestSize();
			
			if ( null != biggestSize )
			{
				return Uri.parse( url.replace( "{id}", getBiggestSize().getId() ) );
			}
		}
		
		return !TextUtils.isEmpty( url ) ? Uri.parse( url ) : null;
	}
}
