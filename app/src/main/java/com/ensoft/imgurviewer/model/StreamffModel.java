package com.ensoft.imgurviewer.model;

import android.net.Uri;

public class StreamffModel
{
	public String externalLink;
	
	public Uri getExternalLink()
	{
		return Uri.parse( externalLink );
	}
}
