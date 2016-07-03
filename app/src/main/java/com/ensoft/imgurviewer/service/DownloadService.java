package com.ensoft.imgurviewer.service;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class DownloadService
{
	Context context;

	public DownloadService( Context context )
	{
		this.context = context;
	}

	public long download( Uri uriDownload, String fileName )
	{
		DownloadManager downloadManager = (DownloadManager)context.getSystemService( Context.DOWNLOAD_SERVICE );

		DownloadManager.Request request = new DownloadManager.Request( uriDownload );

		request.setAllowedOverRoaming(false);
		request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );
		request.setDestinationInExternalFilesDir( context, Environment.DIRECTORY_DOWNLOADS, fileName );

		return downloadManager.enqueue( request );
	}
}
