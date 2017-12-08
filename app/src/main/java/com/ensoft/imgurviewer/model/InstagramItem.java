package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class InstagramItem
{
	@SerializedName( "id" )
	protected String id;
	
	@SerializedName( "code" )
	protected String code;
	
	@SerializedName( "display_src" )
	protected String link;
	
	@SerializedName( "thumbnail_src" )
	protected String thumbnail;
	
	@SerializedName( "caption" )
	protected String caption;
	
	@SerializedName( "is_video" )
	protected boolean isVideo;
	
	public String getId()
	{
		return id;
	}
	
	public String getCode()
	{
		return code;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public boolean isVideo()
	{
		return isVideo;
	}
	
	public Uri getImage()
	{
		return Uri.parse( link );
	}
	
	public Uri getThumbnail()
	{
		return Uri.parse( thumbnail );
	}
	
	public String getCaption()
	{
		return caption;
	}
	
	public String getTitle()
	{
		return ( null != caption ) ? caption : "";
	}
}
