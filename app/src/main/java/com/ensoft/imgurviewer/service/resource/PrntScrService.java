package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.util.List;

public class PrntScrService extends MediaServiceSolver {
    public static final String TAG = PrntScrService.class.getCanonicalName();
    private static final String PRNTSCR_DOMAIN = "prntscr.com";
    private static final String PRNTSC_DOMAIN = "prnt.sc";

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 2 && pathSegments.get(1).equals("direct")) {
            pathResolverListener.onPathResolved(uri, MediaType.IMAGE, uri);
        } else if (pathSegments.size() == 1) {
            Uri mediaUrl = uri.buildUpon().authority(PRNTSC_DOMAIN).path(pathSegments.get(0)).appendPath("direct").build();
            pathResolverListener.onPathResolved(mediaUrl, MediaType.IMAGE, uri);
        } else {
            pathResolverListener.onPathError(uri, App.getInstance().getString(R.string.could_not_resolve_url));
        }
    }

    @Override
    public boolean isServicePath(Uri uri) {
        return uri.getHost().equalsIgnoreCase(PRNTSC_DOMAIN) || uri.getHost().equalsIgnoreCase(PRNTSCR_DOMAIN);
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }
}
