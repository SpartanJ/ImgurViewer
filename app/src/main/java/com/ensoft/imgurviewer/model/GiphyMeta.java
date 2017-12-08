package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class GiphyMeta
{
	@SerializedName( "msg" )
	protected String msg;
	
	@SerializedName( "status" )
	protected int status;
	
	public String getMsg()
	{
		return msg;
	}
	
	public int getStatus()
	{
		return status;
	}
}
