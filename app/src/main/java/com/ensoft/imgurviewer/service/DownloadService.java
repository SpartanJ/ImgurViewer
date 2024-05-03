package com.ensoft.imgurviewer.service;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class DownloadService
{
    private final Context context;

    public DownloadService( Context context )
    {
        this.context = context;
    }

    public void download(Uri uriDownload, String fileName )
    {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService( Context.DOWNLOAD_SERVICE );

        DownloadManager.Request request = new DownloadManager.Request( uriDownload );

        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );

        if ( new PermissionService().isExternalStorageAccess( context ) )
        {
            // Direct access to the Downloads folder is not allowed in Android 10 and above
            // when using scoped storage, so we need to use the subdirectory within download folder.
            String downloadSubDir = "ImgurViewer";
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, downloadSubDir + File.separator + fileName
            );
        }
        else
        {
            request.setDestinationInExternalFilesDir( context, Environment.DIRECTORY_DOWNLOADS, fileName );
        }

        if (null != downloadManager) {
            downloadManager.enqueue(request);
        }
    }
}