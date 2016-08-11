package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class GiphyResource
{
	@SerializedName( "data" )
	protected GiphyData data;

	@SerializedName( "meta" )
	protected GiphyMeta meta;

	public int getStatus()
	{
		if ( null != meta )
		{
			return meta.getStatus();
		}

		return 500;
	}

	public GiphyMeta getMeta()
	{
		return meta;
	}

	public GiphyData getData()
	{
		return data;
	}
}
