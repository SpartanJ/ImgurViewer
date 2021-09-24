package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TwitchClips
{
	@SerializedName( "playbackAccessToken" )
	public TwitchPlaybackAccessToken playbackAccessToken;
	
	@SerializedName( "videoQualities" )
	protected TwitchClip[] clips;
	
	public TwitchClip[] getClips()
	{
		return clips;
	}
}
