package com.ensoft.imgurviewer.model;


import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class FlickrImageSize
{
	@SerializedName( "label" )
	protected String label;
	
	@SerializedName( "width" )
	protected int width;
	
	@SerializedName( "height" )
	protected int height;
	
	@SerializedName( "source" )
	protected String source;
	
	@SerializedName( "media" )
	protected String media;
	
	public String getLabel()
	{
		return label;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public String getMedia()
	{
		return media;
	}
	
	public Uri getUri()
	{
		return Uri.parse( getSource() );
	}
}
