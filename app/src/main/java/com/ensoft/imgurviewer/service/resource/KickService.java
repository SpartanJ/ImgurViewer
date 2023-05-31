package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.KickClipResponse;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;


public class KickService extends MediaServiceSolver {

    protected static final String KICK_DOMAIN = "kick.com";
    protected static final String CLIP_ENDPOINT = "/api/v2/clips/";

    protected String getClipId( Uri uri ) {
        return uri.getQueryParameter( "clip" );
    }

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        String url = "https://" + KICK_DOMAIN + CLIP_ENDPOINT + getClipId(uri);
        RequestService.getInstance().makeJsonRequest(Request.Method.GET, url, new ResponseListener<KickClipResponse>() {
            @Override
            public void onRequestSuccess(Context context, KickClipResponse r) {
                Uri url = Uri.parse(r.clip.getVideoUrl());
                pathResolverListener.onPathResolved( url, UriUtils.guessMediaTypeFromUri( url ), uri );
            }

            @Override
            public void onRequestError(Context context, int errorCode, String errorMessage) {
                pathResolverListener.onPathError(uri, errorMessage);
            }
        });
    }

    @Override
    public boolean isServicePath(Uri uri) {
        String scheme = uri.getScheme();
        if ( scheme == null || ( !scheme.equals("http") && !scheme.equals("https") ) )
            return false;
        String host = uri.getHost();
        if ( host == null || !host.equals(KICK_DOMAIN) )
            return false;
        return getClipId(uri) != null;
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }

}
