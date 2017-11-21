package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class FlickrImage
{
	@SerializedName( "sizes" )
	protected FlickrImageSizes sizes;

	@SerializedName( "stat" )
	protected String stat;

	public FlickrImageSizes getSizes()
	{
		return sizes;
	}

	public String getStat()
	{
		return stat;
	}

	public Uri getUri()
	{
		return sizes.getUri();
	}

	public Uri getThumbnailUri()
	{
		return sizes.getThumbnailUri();
	}
}
