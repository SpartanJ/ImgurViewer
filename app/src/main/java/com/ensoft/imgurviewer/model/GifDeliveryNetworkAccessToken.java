package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class GifDeliveryNetworkAccessToken
{
	@SerializedName( "access_token" )
	protected String accessToken;
	
	public String getAccessToken()
	{
		return accessToken;
	}
}
