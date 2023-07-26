package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.imgurviewer.service.listener.VideoOptions;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class YouTubeService extends MediaServiceSolver  {

    private static final String YOUTUBE_CLIP_BASE_URL = "https://youtube.com/clip/";

    private static boolean newPipeInitialized = false;

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
                    Log.d("YouTube Service", "Resolved Clip: videoId: " + info.parent + " start: " + info.startMs + " end: " + info.endMs);
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
        if(!newPipeInitialized) {
            OkHttpDownloader.init(null);
            NewPipe.init(OkHttpDownloader.getInstance());
            newPipeInitialized = true;
        }
        Handler handler = new Handler(App.getInstance().getMainLooper());
        new Thread(() -> {
            try {
                long t =  System.currentTimeMillis();
                YoutubeService yt = ServiceList.YouTube;
                StreamExtractor extractor = yt.getStreamExtractor(yt.getStreamLHFactory().fromId(id));
                extractor.fetchPage();
                Optional<VideoStream> stream = extractor
                        .getVideoStreams()
                        .stream()
                        .max(Comparator.comparing(VideoStream::getHeight));
                Log.d("YouTubeExtractor", "Extraction took " + (System.currentTimeMillis() - t) + "ms");
                handler.post(() ->  {
                    if(stream.isPresent()) {
                        pathResolverListener.onPathResolved(Uri.parse(stream.get().getContent()), MediaType.VIDEO_MP4, uri, options);
                    } else {
                        pathResolverListener.onPathError(uri, "Could not find a suitable video stream");
                    }
                });
            } catch (Exception e) {
                Log.e("NewPipeExtractor", "Failed to extract video information", e);
                handler.post(() -> pathResolverListener.onPathError(uri, e.getMessage()));
            }
        }).start();
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

    class VideoType {
        public String id;
        public boolean clip;
        public int startTime;

        public VideoType(String id, int startTime, boolean clip) {
            this.id = id;
            this.clip = clip;
            this.startTime = startTime;
        }
    }

    class ClipInfo {
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

class OkHttpDownloader extends Downloader {

    //public static final String USER_AGENT =
    //        "Mozilla/5.0 (Windows NT 10.0; rv:91.0) Gecko/20100101 Firefox/91.0";
    public static final String YOUTUBE_RESTRICTED_MODE_COOKIE_KEY =
            "youtube_restricted_mode_key";
    public static final String YOUTUBE_RESTRICTED_MODE_COOKIE = "PREF=f2=8000000";
    public static final String YOUTUBE_DOMAIN = "youtube.com";

    private static OkHttpDownloader instance;
    private final Map<String, String> mCookies;
    private final OkHttpClient client;

    public OkHttpDownloader(final OkHttpClient.Builder builder) {
        this.client = builder
                .readTimeout(30, TimeUnit.SECONDS)
//                .cache(new Cache(new File(context.getExternalCacheDir(), "okhttp"),
//                        16 * 1024 * 1024))
                .build();
        this.mCookies = new HashMap<>();
    }

    public static OkHttpDownloader init(final OkHttpClient.Builder builder) {
        instance = new OkHttpDownloader(
                builder != null ? builder : new OkHttpClient.Builder());
        return instance;
    }

    public static OkHttpDownloader getInstance() {
        return instance;
    }

    public String getCookies(final String url) {
        String youtubeCookie = url.contains(YOUTUBE_DOMAIN)
                ? getCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY) : null;
        //// Recaptcha cookie is always added TODO: not sure if this is necessary
        return Stream.of(youtubeCookie /*, getCookie(ReCaptchaActivity.RECAPTCHA_COOKIES_KEY) */)
                .filter(Objects::nonNull)
                .flatMap(cookies -> Arrays.stream(cookies.split("; *")))
                .distinct()
                .collect(Collectors.joining("; "));
    }

    public String getCookie(final String key) {
        return mCookies.get(key);
    }

    public void setCookie(final String key, final String cookie) {
        mCookies.put(key, cookie);
    }

    public void removeCookie(final String key) {
        mCookies.remove(key);
    }

    public void updateYoutubeRestrictedModeCookies(final boolean youtubeRestrictedModeEnabled) {
        if (youtubeRestrictedModeEnabled) {
            setCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY,
                    YOUTUBE_RESTRICTED_MODE_COOKIE);
        } else {
            removeCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY);
        }
        //InfoCache.getInstance().clearCache();
    }

    public long getContentLength(final String url) throws IOException {
        try {
            final Response response = head(url);
            return Long.parseLong(response.getHeader("Content-Length"));
        } catch (final NumberFormatException e) {
            throw new IOException("Invalid content length", e);
        } catch (final ReCaptchaException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Response execute(@NonNull org.schabi.newpipe.extractor.downloader.Request request) throws IOException, ReCaptchaException {
        final String httpMethod = request.httpMethod();
        final String url = request.url();
        final Map<String, List<String>> headers = request.headers();
        final byte[] dataToSend = request.dataToSend();

        RequestBody requestBody = null;
        if (dataToSend != null) {
            requestBody = RequestBody.create(dataToSend);
        }

        final okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .method(httpMethod, requestBody).url(url)
                .addHeader("User-Agent", UriUtils.getDefaultUserAgent());

        final String cookies = getCookies(url);
        if (!cookies.isEmpty()) {
            requestBuilder.addHeader("Cookie", cookies);
        }

        for (final Map.Entry<String, List<String>> pair : headers.entrySet()) {
            final String headerName = pair.getKey();
            final List<String> headerValueList = pair.getValue();

            if (headerValueList.size() > 1) {
                requestBuilder.removeHeader(headerName);
                for (final String headerValue : headerValueList) {
                    requestBuilder.addHeader(headerName, headerValue);
                }
            } else if (headerValueList.size() == 1) {
                requestBuilder.header(headerName, headerValueList.get(0));
            }

        }
        final okhttp3.Response response = client.newCall(requestBuilder.build()).execute();

        if (response.code() == 429) {
            response.close();

            throw new ReCaptchaException("reCaptcha Challenge requested", url);
        }

        final ResponseBody body = response.body();
        String responseBodyToReturn = null;

        if (body != null) {
            responseBodyToReturn = body.string();
        }
        final String latestUrl = response.request().url().toString();
        return new Response(response.code(), response.message(), response.headers().toMultimap(),
                responseBodyToReturn, latestUrl);
    }
}