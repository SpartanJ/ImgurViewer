package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

public class ArazuService extends MediaServiceSolver {

    public static final String TAG = ArazuService.class.getCanonicalName();

    private static final String ARAZU_DOMAIN = "arazu.io";

    protected Uri getUrlFromResponse( String response )
    {
        return UriUtils.getUriMatch( response, "<source src=\"", "\" id=\"clip-source\"" );
    }

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        StringRequest stringRequest = new StringRequest( uri.toString(), response ->
        {
            Uri mediaUrl = getUrlFromResponse( response );

            if ( mediaUrl != null )
            {
                pathResolverListener.onPathResolved( mediaUrl,  UriUtils.guessMediaTypeFromUri( mediaUrl ), uri );
            }
            else
            {
                pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.could_not_resolve_video_url ) );
            }
        }, error ->
        {
            Log.v( TAG, error.toString() );

            pathResolverListener.onPathError( uri, error.toString() );
        } );

        RequestService.getInstance().addToRequestQueue( stringRequest );
    }

    @Override
    public boolean isServicePath(Uri uri) {
        String scheme = uri.getScheme();
        if ( scheme == null || ( !scheme.equals("http") && !scheme.equals("https") ) )
            return false;
        String host = uri.getHost();
        if ( host == null )
            return false;
        return host.equals(ARAZU_DOMAIN);
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }
}
