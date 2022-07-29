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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NhentaiService extends MediaServiceSolver implements AlbumProvider {

    public static final String DOMAIN = "nhentai.net";
    private static final String NHENTAI_URL_REGEX = "https://nhentai\\.net/g/(\\d+)/";
    private static final String API_BASE = "https://janda.mod.land/nhentai/get?book=";

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {

    }

    @Override
    public void getAlbum(Uri uri, AlbumSolverListener albumSolverListener) {
        // Make a regex pattern to
        Pattern regex = Pattern.compile(NHENTAI_URL_REGEX);
        Matcher matcher = regex.matcher(uri.toString());

        // Check if the regex matches. If it doesn't then this URL is invalid.
        if (!matcher.find()) {
            albumSolverListener.onAlbumError("URL is not a valid album.");
            return;
        }

        // Grab the album id from the match
        final String albumId = matcher.group(1);

        // Make a request to the intermediary API
        String apiUrl = API_BASE + albumId;
        RequestService.getInstance().makeStringRequest(Request.Method.GET, apiUrl, new ResponseListener<String>() {
            @Override
            public void onRequestSuccess(Context context, String response) {
                try {
                    // Parse the JSON result and get the image list
                    JSONObject data = new JSONObject(response).getJSONObject("data");
                    JSONArray imageUrls = data.optJSONArray("image");

                    // Make sure we actually got some images
                    if (imageUrls == null) {
                        albumSolverListener.onAlbumError("Server returned no images.");
                        return;
                    }

                    // Iterate over the images in the returned response and add them to the album
                    ImgurImage[] album = new ImgurImage[imageUrls.length()];
                    for (int i = 0; i < imageUrls.length(); i++) {
                        String imageUrl = imageUrls.getString(i);
                        String thumbnailUrl = imageUrl.replace("i.nhentai", "t.nhentai").replace(".jpg", "t.jpg");
                        album[i] = new ImgurImage(albumId + "/" + i, imageUrl, Uri.parse(thumbnailUrl), null);
                    }

                    // Return the album
                    albumSolverListener.onAlbumResolved(album);

                } catch (JSONException e) {
                    albumSolverListener.onAlbumError("Server returned bad JSON data.");
                }
            }

            @Override
            public void onRequestError(Context context, int errorCode, String errorMessage) {
                albumSolverListener.onAlbumError(errorCode + ": " + errorMessage);
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
