package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.KickClipResponse;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.model.PipedStreamResponse;
import com.ensoft.imgurviewer.model.PipedVideoStream;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

import java.util.Arrays;


public class YouTubeService extends MediaServiceSolver  {

    private static final String YOUTUBE_DOMAIN = "youtube.com";

    private String getVideoId(Uri uri) {
        return uri.getQueryParameter("v");
    }

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        String endpoint = "https://api.piped.yt/streams/" + getVideoId(uri);
        RequestService.getInstance().makeJsonRequest(Request.Method.GET, endpoint, new ResponseListener<PipedStreamResponse>() {
            @Override
            public void onRequestSuccess(Context context, PipedStreamResponse response) {
                PipedVideoStream videoUrl = null;
                for (PipedVideoStream stream : response.getVideoStreams()) {
                    if (stream.getMimeType().equals("video/mp4")) {
                        videoUrl = stream;
                    }
                }
                Uri url = Uri.parse(videoUrl.getVideoUrl());
                Log.i("############", videoUrl.getMimeType());
                pathResolverListener.onPathResolved( url, MediaType.VIDEO_MP4, Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/YouTube_Logo_2017.svg/1200px-YouTube_Logo_2017.svg.png") );
            }
        });

    }

    @Override
    public boolean isServicePath(Uri uri) {
        String host = uri.getHost();
        return host != null && host.equals(YOUTUBE_DOMAIN);
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }

}
