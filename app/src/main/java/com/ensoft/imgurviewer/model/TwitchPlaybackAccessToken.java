package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TwitchPlaybackAccessToken
{
	@SerializedName( "signature" )
	public String signature;
	
	@SerializedName( "value" )
	public String value;
}
