package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.text.TextUtils;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.rest.response.HttpStatus;
import com.imgurviewer.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwimgPBSService extends MediaServiceSolver {
    public static final String TAG = TwimgPBSService.class.getCanonicalName();
    private static final String DOMAIN = "pbs.twimg.com";
    private static final Pattern NAME_INFO_REGEX = Pattern.compile("^(.+?)(?:\\.([\\w\\d]{3,}))?(?::([\\w\\d]+))?$");

    private Uri reformatUri(final Uri uri, final String fileName, final String format, final String name) {
        final List<String> pathSegments = uri.getPathSegments();

        Uri.Builder newUri = uri.buildUpon()
            .path("/")
            .clearQuery()
            .appendQueryParameter("format", format)
            .appendQueryParameter("name", name);
        // Exclude the last path segment, we want to add only its filename
        for (int i = 0; pathSegments.size() - 1 > i; i++) {
            newUri.appendPath(pathSegments.get(i));
        }
        newUri.appendPath(fileName);
        return newUri.build();
    }

    private boolean imageExists(final Uri uri) {
        HttpURLConnection urlConnection = null;

        try {
            System.setProperty("http.keepAlive", "false");
            urlConnection = App.getInstance().getProxyUtils().openConnectionTo( uri );
            urlConnection.setRequestMethod("HEAD");
            urlConnection.setRequestProperty("User-Agent", UriUtils.getDefaultUserAgent());
            urlConnection.getInputStream().close();

            return urlConnection.getResponseCode() == HttpStatus.OK_200.getCode();
        } catch (Exception e) {
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public void getPath(Uri uri, final PathResolverListener pathResolverListener) {
        final List<String> pathSegments = uri.getPathSegments();
        if (pathSegments == null || pathSegments.size() < 1) {
            pathResolverListener.onPathError(uri, App.getInstance().getString(R.string.could_not_resolve_video_url));
            return;
        }
        if ("profile_images".equals(pathSegments.get(0)) || "profile_banners".equals(pathSegments.get(0))) {
            sendPathResolved(pathResolverListener, uri, MediaType.IMAGE, null);
            return;
        }

        final String lastPathSegment = uri.getLastPathSegment();
        if (TextUtils.isEmpty(lastPathSegment)) {
            pathResolverListener.onPathError(uri, App.getInstance().getString(R.string.could_not_resolve_video_url));
            return;
        }
        final Matcher matcher = NAME_INFO_REGEX.matcher(lastPathSegment);
        if (!matcher.find()) {
            pathResolverListener.onPathError(uri, App.getInstance().getString(R.string.could_not_resolve_video_url));
            return;
        }

        final String fileName = matcher.group(1);
        String format = uri.getQueryParameter("format");
        if (TextUtils.isEmpty(format)) {
            format = matcher.group(2);
            if (TextUtils.isEmpty(format)) {
                pathResolverListener.onPathError(uri, App.getInstance().getString(R.string.could_not_resolve_video_url));
                return;
            }
        }
        String name = uri.getQueryParameter("name");
        if (TextUtils.isEmpty(name)) {
            name = matcher.group(3);
            if (TextUtils.isEmpty(name)) {
                name = "orig";
            }
        }

        Uri reformattedUri = reformatUri(uri, fileName, format, name);
        // png + orig gives a 4xx
        if (format.equals("png") && name.equals("orig")) {
            if (!imageExists(reformattedUri)) {
                reformattedUri = reformatUri(uri, fileName, format, "4096x4096");
            }
        }

        sendPathResolved(pathResolverListener, reformattedUri, MediaType.IMAGE, null);
    }

    @Override
    public boolean isServicePath(Uri uri) {
        if (!DOMAIN.equals(uri.getHost())) {
            return false;
        }
        if (!TextUtils.isEmpty(uri.getQueryParameter("format"))) {
            return true;
        }

        final String lastPathSegment = uri.getLastPathSegment();
        if (TextUtils.isEmpty(lastPathSegment)) {
            return false;
        }
        final Matcher matcher = NAME_INFO_REGEX.matcher(lastPathSegment);
        if (!matcher.find()) {
            return false;
        }
        return !TextUtils.isEmpty(matcher.group(2));
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }

    @Override
    public boolean isVideo(Uri uri) {
        return false;
    }
}
