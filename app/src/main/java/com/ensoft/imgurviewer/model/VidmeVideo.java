package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class VidmeVideo
{
	@SerializedName( "complete_url" )
	protected String url;

	@SerializedName( "formats" )
	protected VidmeVideoFormat[] formats;

	public String getUrl()
	{
		return url;
	}

	public VidmeVideoFormat[] getFormats()
	{
		return formats;
	}

	public Uri getVideoUri()
	{
		if ( null != formats )
		{
			for ( VidmeVideoFormat video : formats )
			{
				if ( "1080p".equals( video.getType() ) )
				{
					return Uri.parse( video.getUri() );
				}
			}

			for ( VidmeVideoFormat video : formats )
			{
				if ( "720p".equals( video.getUri() ) )
				{
					return Uri.parse( video.getUri() );
				}
			}
		}

		return Uri.parse( getUrl() );
	}
}
