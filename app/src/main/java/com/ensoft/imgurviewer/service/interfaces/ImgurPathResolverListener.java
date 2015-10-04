package com.ensoft.imgurviewer.service.interfaces;

import android.net.Uri;

public interface ImgurPathResolverListener
{
	void onPathResolved( Uri url, Uri thumbnail );

	void onPathError( String error );
}
