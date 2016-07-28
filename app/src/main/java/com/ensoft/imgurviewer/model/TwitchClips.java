package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TwitchClips
{
	@SerializedName( "quality_options" )
	protected TwitchClip[] clips;

	public TwitchClip[] getClips()
	{
		return clips;
	}
}
