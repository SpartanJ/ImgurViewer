package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class VidmeResource
{
	@SerializedName( "status" )
	protected boolean status;

	@SerializedName( "video" )
	protected VidmeVideo video;

	public boolean isStatus()
	{
		return status;
	}

	public VidmeVideo getVideo()
	{
		return video;
	}
}
