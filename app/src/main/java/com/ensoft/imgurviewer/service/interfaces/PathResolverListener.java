package com.ensoft.imgurviewer.service.interfaces;

import android.net.Uri;

public interface PathResolverListener
{
	void onPathResolved( Uri url, Uri thumbnail );

	void onPathError( String error );
}
