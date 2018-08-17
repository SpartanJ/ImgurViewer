package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TumblrImagePage
{
	@SerializedName( "photo" )
	public List<TumblrPhoto> photos;
}
