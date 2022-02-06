package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class TumblrImageResponse
{
	@SerializedName( "imageResponse" )
	public TumblrPhoto[] photos;
}
