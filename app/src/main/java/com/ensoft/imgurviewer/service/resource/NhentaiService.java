package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NhentaiService extends MediaServiceSolver implements AlbumProvider {

    public static final String BASE_IMG_URL = "https://i.nhentai.net/galleries/";
    public static final String BASE_THUMB_URL = "https://t.nhentai.net/galleries/";
    public static final String DOMAIN = "nhentai.net";
    public static final String IMAGE_REGEX = "<img src=\"https://t.nhentai.net/galleries/(\\d+/\\d+)t.jpg";

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {

    }

    @Override
    public void getAlbum(Uri uri, AlbumSolverListener albumSolverListener) {
        RequestService.getInstance().makeStringRequest(Request.Method.GET, uri.toString(), new ResponseListener<String>() {
            @Override
            public void onRequestSuccess(Context context, String response) {
                final Pattern pattern = Pattern.compile(IMAGE_REGEX, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(response);

                ArrayList<ImgurImage> images = new ArrayList<>();
                while (matcher.find()) {
                    String id = matcher.group(1);
                    Uri thumbnail = Uri.parse(BASE_THUMB_URL + id + "t.jpg");
                    images.add(new ImgurImage(id, BASE_IMG_URL + id + ".jpg", thumbnail, null));
                }

                ImgurImage[] album = new ImgurImage[images.size()];
                album = images.toArray(album);
                albumSolverListener.onAlbumResolved(album);
            }

            @Override
            public void onRequestError(Context context, int errorCode, String errorMessage) {
                albumSolverListener.onAlbumError(errorCode + ": Could not load album");
            }
        });
    }

    @Override
    public boolean isGallery(Uri uri) {
        return UriUtils.uriMatchesDomain(uri, DOMAIN, "/g/");
    }

    @Override
    public boolean isServicePath(Uri uri) {
        return UriUtils.uriMatchesDomain(uri, DOMAIN);
    }

    @Override
    public boolean isAlbum(Uri uri) {
        return isGallery(uri);
    }
}
