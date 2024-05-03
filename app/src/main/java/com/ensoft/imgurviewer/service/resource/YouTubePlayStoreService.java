package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class YouTubePlayStoreService extends MediaServiceSolver {
    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        pathResolverListener.onPathError(uri, "YouTube is not supported on this version of the app!");
    }

    @Override
    public boolean isServicePath(Uri uri) {
        return YouTubeUtils.parseUri(uri) != null;
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }
}
