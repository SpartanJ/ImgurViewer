package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class InstagramItem
{
	@SerializedName( "id" )
	protected String id;

	@SerializedName( "type" )
	protected String type;

	@SerializedName( "code" )
	protected String code;

	@SerializedName( "link" )
	protected String link;

	@SerializedName( "images" )
	protected Map<String,InstagramMedia> images;

	@SerializedName( "videos" )
	protected Map<String,InstagramMedia> videos;

	@SerializedName( "caption" )
	protected InstagramItemCaption caption;

	public String getId()
	{
		return id;
	}

	public String getType()
	{
		return type;
	}

	public String getCode()
	{
		return code;
	}

	public String getLink()
	{
		return link;
	}

	public Map<String, InstagramMedia> getImages()
	{
		return images;
	}

	public Map<String, InstagramMedia> getVideos()
	{
		return videos;
	}

	public boolean isVideo()
	{
		return "video".equals( getType() );
	}

	public Uri getVideo()
	{
		if ( null != videos && videos.size() > 0 )
		{
			if ( videos.containsKey( "standard_resolution" ) )
			{
				InstagramMedia item = videos.get( "standard_resolution" );

				return item.getUri();
			}
		}

		return null;
	}

	public Uri getImage()
	{
		return Uri.parse( link + "media/?size=l" );
	}

	public Uri getThumbnail()
	{
		return Uri.parse( link + "media/?size=t" );
	}

	public InstagramItemCaption getCaption()
	{
		return caption;
	}

	public String getTitle()
	{
		if ( null != caption )
		{
			return caption.getText();
		}

		return "";
	}
}
