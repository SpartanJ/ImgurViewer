package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class XHamsterVideoMp4
{
	@SerializedName( "1080p" )
	public String v1080p;
	
	@SerializedName( "720p" )
	public String v720p;
	
	@SerializedName( "480p" )
	public String v480p;
	
	@SerializedName( "240p" )
	public String v240p;
}
