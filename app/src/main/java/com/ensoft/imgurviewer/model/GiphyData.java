package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GiphyData
{
	@SerializedName( "id" )
	protected String id;

	@SerializedName( "images" )
	protected Map<String, GiphyVideo> videos;

	public String getId()
	{
		return id;
	}

	public Map<String, GiphyVideo> getVideos()
	{
		return videos;
	}

	public Uri getUri()
	{
		if ( videos.containsKey( "original" ) )
		{
			return videos.get("original").getUri();
		}
		else if ( videos.containsKey( "downsized_large" ) )
		{
			return videos.get("downsized_large").getUri();
		}

		if ( videos.size() > 0 )
		{
			for ( Map.Entry<String, GiphyVideo> video : videos.entrySet() )
			{
				return video.getValue().getUri();
			}
		}

		return null;
	}
}
