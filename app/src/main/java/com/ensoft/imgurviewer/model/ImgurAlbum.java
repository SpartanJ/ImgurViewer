package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class ImgurAlbum
{
	@SerializedName( "id" )
	protected String id;
	
	@SerializedName( "title" )
	protected String title;
	
	@SerializedName( "description" )
	protected String description;
	
	@SerializedName( "cover" )
	protected String cover;
	
	@SerializedName( "datetime" )
	protected long dateTime;
	
	@SerializedName( "images_count" )
	protected int imageCount;
	
	@SerializedName( "images" )
	protected ImgurImage[] images;
	
	public ImgurAlbum( String id, String title, String description, String cover, long dateTime, int imageCount, ImgurImage[] images )
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.cover = cover;
		this.dateTime = dateTime;
		this.imageCount = imageCount;
		this.images = images;
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
	
	public String getCover()
	{
		return cover;
	}
	
	public long getDateTime()
	{
		return dateTime;
	}
	
	public int getImageCount()
	{
		return imageCount;
	}
	
	public ImgurImage[] getImages()
	{
		return images;
	}
	
	public ImgurImage getImage( int index )
	{
		return images[ index ];
	}
}
