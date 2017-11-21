package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.ensoft.imgurviewer.service.resource.ImgurService;
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

	protected Uri thumbnailUri;

	protected Uri videoUri;

	public ImgurImage( String id, String link )
	{
		this.id = id;
		this.link = link;
	}

	public ImgurImage( String id, String link, Uri thumbnailUri, Uri videoUri, String title )
	{
		this.id = id;
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.videoUri = videoUri;
		this.title = title;
	}

	public ImgurImage( String id, String link, Uri thumbnailUri, String title )
	{
		this.id = id;
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.title = title;
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

	public Uri getThumbnailLinkUri()
	{
		return null != thumbnailUri ? thumbnailUri : new ImgurService().getThumbnailPath( getLinkUri() );
	}

	public boolean hasVideo()
	{
		return null != videoUri;
	}

	public Uri getVideoUri()
	{
		return videoUri;
	}
}
