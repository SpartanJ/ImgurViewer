package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class StreamableVideo
{
	@SerializedName( "status" )
	protected int status;

	@SerializedName( "title" )
	protected String title;

	@SerializedName( "url" )
	protected String url;

	@SerializedName( "thumbnail_url" )
	protected String thumbnailUrl;

	@SerializedName( "message" )
	protected String message;

	@SerializedName( "files" )
	protected Map<String,StreamableVideoType> files;

	public int getStatus()
	{
		return status;
	}

	public String getTitle()
	{
		return title;
	}

	public String getUrl()
	{
		return url;
	}

	public String getThumbnailUrl()
	{
		return thumbnailUrl;
	}

	public String getMessage()
	{
		return message;
	}

	public Map<String,StreamableVideoType> getFiles()
	{
		return files;
	}

	public Uri getUri()
	{
		StreamableVideoType video = files.get( "mp4" );

		if ( null != video )
		{
			String videoUrl = video.getUrl();

			if ( videoUrl.startsWith( "//" ) )
			{
				videoUrl = "https:" + videoUrl;
			}

			return Uri.parse( videoUrl );
		}

		return null;
	}
}
