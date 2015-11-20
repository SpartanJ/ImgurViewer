package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class GyazoOEmbed
{
	@SerializedName( "version" )
	protected String version;

	@SerializedName( "type" )
	protected String type;

	@SerializedName( "provider_name" )
	protected String providerName;

	@SerializedName( "providerUrl" )
	protected String providerUrl;

	@SerializedName( "url" )
	protected String url;

	@SerializedName( "width" )
	protected int width;

	@SerializedName( "height" )
	protected int height;

	public String getVersion()
	{
		return version;
	}

	public String getType()
	{
		return type;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public String getProviderUrl()
	{
		return providerUrl;
	}

	public String getUrl()
	{
		return url;
	}

	public Uri getUri()
	{
		return Uri.parse( url );
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}
