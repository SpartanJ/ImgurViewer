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
import java.util.List;


public class YouTubeService extends MediaServiceSolver  {

    private static final String PIPED_INSTANCE = "https://api.piped.yt";

    private PipedVideoStream findBestQuality(PipedVideoStream[] streams) {
        int bestHeight = -1;
        PipedVideoStream best = null;
        for(PipedVideoStream current : streams) {
            if(current.isVideoOnly())
                continue;
            int currentHeight = current.getHeight();
            if(currentHeight == 0) {
                String[] q = current.getQuality().split("p");
                if(q.length >= 1){
                    currentHeight = Integer.parseInt(q[0]);
                }
            }
            if(bestHeight < currentHeight) {
                best = current;
                bestHeight = currentHeight;
            }
        }
        return best;
    }

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        VideoType info = parseUri(uri);
        if(info == null) {
            pathResolverListener.onPathError(uri, "Could not extract the video id");
            return;
        }
        String endpoint = PIPED_INSTANCE + "/streams/" + info.id;
        RequestService.getInstance().makeJsonRequest(Request.Method.GET, endpoint, new ResponseListener<PipedStreamResponse>() {
            @Override
            public void onRequestSuccess(Context context, PipedStreamResponse response) {
                PipedVideoStream stream = findBestQuality(response.getVideoStreams());
                if(stream == null) {
                    pathResolverListener.onPathError(uri, "Could not find a suitable video stream");
                    return;
                }
                pathResolverListener.onPathResolved(
                        Uri.parse(stream.getVideoUrl()),
                        MediaType.VIDEO_MP4,
                        Uri.parse(response.getThumbnailUrl()) );
            }
            @Override
            public void onRequestError(Context context, int errorCode, String errorMessage) {
                pathResolverListener.onPathError(uri, errorMessage);
            }
        });

    }

    @Override
    public boolean isServicePath(Uri uri) {
        return parseUri(uri) != null;
    }

    @Override
    public boolean isGallery(Uri uri) {
        return false;
    }

    private VideoType parseUri(Uri uri) {
        String host = uri.getHost();
        if(host == null)
            return null;

        if(host.endsWith("youtu.be")) {
            List<String> path = uri.getPathSegments();
            if(path == null || path.isEmpty())
                return null;
            return new VideoType(path.get(0), false);
        } else if(host.endsWith("youtube.com")) {
            String id = uri.getQueryParameter("v");
            if(id != null)
                return new VideoType(id, false);
            List<String> path = uri.getPathSegments();
            if(path == null || path.size() < 2 || !path.get(0).matches("(embed|v|shorts)"))
                return null;
            return new VideoType(path.get(1), false);
        }

        return null;
    }

    class VideoType {
        public String id;
        public boolean clip;

        public VideoType(String id, boolean clip) {
            this.id = id;
            this.clip = clip;
        }
    }

}
