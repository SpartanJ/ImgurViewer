package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class ImgurImage
{
	@SerializedName( "id" )
	protected String id;

	@SerializedName( "title" )
	protected String title;

	@SerializedName( "description" )
	protected String description;

	@SerializedName( "datetime" )
	protected long dateTime;

	@SerializedName( "width" )
	protected int width;

	@SerializedName( "height" )
	protected int height;

	@SerializedName( "size" )
	protected long size;

	@SerializedName( "link" )
	protected String link;

	public ImgurImage( String id, String title, String description, long dateTime, int width, int height, long size, String link )
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.dateTime = dateTime;
		this.width = width;
		this.height = height;
		this.size = size;
		this.link = link;
	}

	public String getId()
	{
		return id;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public long getDateTime()
	{
		return dateTime;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public long getSize()
	{
		return size;
	}

	public String getLink()
	{
		return link;
	}

	public Uri getLinkUri()
	{
		return Uri.parse( getLink() );
	}
}
