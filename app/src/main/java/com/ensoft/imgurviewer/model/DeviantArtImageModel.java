package com.ensoft.imgurviewer.model;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class DeviantArtImageModel
{
	@SerializedName( "url" )
	public String url;
	
	@SerializedName( "thumbnail_url" )
	public String thumbnailUrl;
	
	@SerializedName( "fullsize_url" )
	public String fullsizeUrl;
	
	public String getUrl()
	{
		return !TextUtils.isEmpty( fullsizeUrl ) ? fullsizeUrl : url;
	}
	
	public String getThumbnailUrl()
	{
		return thumbnailUrl;
	}
	
	public Uri getUri()
	{
		return Uri.parse( getUrl() );
	}
	
	public Uri getThumbnailUri()
	{
		return Uri.parse( getThumbnailUrl() );
	}
}
