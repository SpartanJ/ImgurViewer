package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.model.YouTubeVideo;
import com.ensoft.imgurviewer.model.YouTubeVideoFormat;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.imgurviewer.service.listener.VideoOptions;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class YouTubeService extends MediaServiceSolver  {

    public static final String TAG = YouTubeService.class.getCanonicalName();
    private static final String YOUTUBE_CLIP_BASE_URL = "https://youtube.com/clip/";
    private static final String YOUTUBE_INTERNAL_API_URL = "https://www.youtube.com/youtubei/v1/player?key=AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w";
    private static final String YOUTUBE_INTERNAL_API_REQUEST_CONTEXT = "{\"client\":{\"clientName\":\"ANDROID\",\"clientVersion\":\"17.31.35\",\"androidSdkVersion\":30}}";
    private static final String YOUTUBE_INTERNAL_API_USER_AGENT = "com.google.android.youtube/17.31.35 (Linux; U; Android 11) gzip";
    //Loading times are much slower when using adaptive formats
    //Probably a problem with MergingMediaSource
    private static final boolean USE_ADAPTIVE_FORMATS = false;

    @Override
    public void getPath(Uri uri, PathResolverListener pathResolverListener) {
        VideoType info = parseUri(uri);
        if(info == null) {
            pathResolverListener.onPathError(uri, "Could not extract the video id");
            return;
        }
        VideoOptions options = new VideoOptions();
        if(info.startTime > 0)
            options.setStartTime(info.startTime * 1000);
        if(!info.clip) {
            resolveVideo(uri, info.id, options, pathResolverListener);
        } else {
            RequestService.getInstance().makeStringRequest(Request.Method.GET, YOUTUBE_CLIP_BASE_URL + info.id, new ResponseListener<String>() {
                @Override
                public void onRequestSuccess(Context context, String response) {
                    ClipInfo info = parseClipInfo(response);
                    if(info == null) {
                        pathResolverListener.onPathError(uri, "Could not resolve clip");
                        return;
                    }
                    Log.d(TAG, "Resolved Clip: videoId: " + info.parent + " start: " + info.startMs + " end: " + info.endMs);
                    options.setClipRange(info.startMs, info.endMs);
                    resolveVideo(uri, info.parent, options, pathResolverListener);
                }
                @Override
                public void onRequestError(Context context, int errorCode, String errorMessage) {
                    pathResolverListener.onPathError(uri, errorMessage);
                }
            });
        }
    }

    private void resolveVideo(Uri uri, String id, VideoOptions options, PathResolverListener pathResolverListener) {
        JSONObject request = new JSONObject();
        try {
            request.put("videoId", id);
            request.put("context", new JSONObject(YOUTUBE_INTERNAL_API_REQUEST_CONTEXT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                YOUTUBE_INTERNAL_API_URL,
                null,
                response -> {
                    try
                    {
                        YouTubeVideo ytv = new Gson().fromJson( response.toString(), YouTubeVideo.class );

                        Optional<YouTubeVideoFormat> video = ytv
                                .getAdaptiveFormats()
                                .stream()
                                .filter(f -> f.hasVideo() && f.height <= 1080)
                                .max(Comparator.comparing(f -> f.height));

                        Optional<YouTubeVideoFormat> audio = ytv
                                .getAdaptiveFormats()
                                .stream()
                                .filter(f -> !f.hasVideo())
                                .max(Comparator.comparing(f -> f.bitrate));

                        Optional<YouTubeVideoFormat> mixed = ytv
                                .getFormats()
                                .stream()
                                .filter(f -> f.height <= 1080)
                                .max(Comparator.comparing(f -> f.height));

                        if(USE_ADAPTIVE_FORMATS && video.isPresent() && audio.isPresent()) {
                            options.setExternalAudioTrack(Uri.parse(audio.get().url));
                            pathResolverListener.onPathResolved(Uri.parse(video.get().url), MediaType.VIDEO_MP4, uri, options);
                        } else if(mixed.isPresent()) {
                            pathResolverListener.onPathResolved(Uri.parse(mixed.get().url), MediaType.VIDEO_MP4, uri, options);
                        } else {
                            pathResolverListener.onPathError(uri, "Could not find a suitable video stream");
                        }
                    }
                    catch ( Exception e )
                    {
                        Log.v(TAG, e.getMessage());
                        pathResolverListener.onPathError(uri, e.getMessage());
                    }
                },
                e -> {
                    Log.v(TAG, e.getMessage());
                    pathResolverListener.onPathError(uri, e.getMessage());
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                return request.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", YOUTUBE_INTERNAL_API_USER_AGENT);
                return headers;
            }
        };

        RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
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

        int start = 0;
        {
            String timestamp = uri.getQueryParameter("t");
            try {
                if (timestamp != null)
                    start = Integer.parseInt(timestamp);
            } catch (NumberFormatException ignored) {}
        }

        if(host.endsWith("youtu.be")) {
            List<String> path = uri.getPathSegments();
            if(path == null || path.isEmpty())
                return null;
            return new VideoType(path.get(0), start, false);
        } else if(host.endsWith("youtube.com")) {
            String id = uri.getQueryParameter("v");
            if(id != null)
                return new VideoType(id, start, false);
            List<String> path = uri.getPathSegments();
            if(path == null || path.size() < 2)
                return null;
            if(path.get(0).equals("clip"))
                return new VideoType(path.get(1), start, true);
            if(!path.get(0).matches("(embed|v|shorts)"))
                return null;
            return new VideoType(path.get(1), start, false);
        }

        return null;
    }

    static class VideoType {
        public String id;
        public boolean clip;
        public int startTime;

        public VideoType(String id, int startTime, boolean clip) {
            this.id = id;
            this.clip = clip;
            this.startTime = startTime;
        }
    }

    static class ClipInfo {
        public String parent;
        public int startMs, endMs;

        public ClipInfo(String parent, int startMs, int endMs) {
            this.parent = parent;
            this.startMs = startMs;
            this.endMs = endMs;
        }
    }

    private ClipInfo parseClipInfo(String response) {
        String parent = extractKeyed(response, "\"videoId\"", '"', '"');
        String clipConfig = extractKeyed(response, "\"clipConfig\"", '{', '}');
        String start = extractKeyed(clipConfig, "\"startTimeMs\"", '"', '"');
        String end = extractKeyed(clipConfig, "\"endTimeMs\"", '"', '"');
        if(parent == null || start == null || end == null)
            return null;

        return new ClipInfo(parent, Integer.parseInt(start), Integer.parseInt(end));
    }

    private String extractKeyed(String haystack, String key, char startToken, char endToken) {
        if(haystack == null)
            return null;
        int index = haystack.lastIndexOf(key);
        if(index == -1)
            return null;
        int start = haystack.indexOf(startToken, index + key.length()) + 1;
        if(start == 0)
            return null;
        int end = haystack.indexOf(endToken, start);
        if(end == -1)
            return null;
        return haystack.substring(start, end);
    }

}
