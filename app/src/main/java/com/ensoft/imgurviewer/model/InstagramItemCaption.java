package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class InstagramItemCaption
{
	@SerializedName( "text" )
	protected String text;
	
	public String getText()
	{
		return text;
	}
}
