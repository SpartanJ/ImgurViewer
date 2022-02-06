package com.ensoft.imgurviewer.model;

import android.net.Uri;

public class YouPornVideo
{
	protected String videoUrl;
	
	public String getVideoUrl()
	{
		return videoUrl;
	}
	
	public Uri getVideoUri()
	{
		return Uri.parse(getVideoUrl());
	}
}
