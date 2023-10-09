package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import java.util.List;

public  class YouTubeUtils {

    public static YouTubeUtils.VideoType parseUri(Uri uri) {
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
            return new YouTubeUtils.VideoType(path.get(0), start, false);
        } else if(host.endsWith("youtube.com")) {
            String id = uri.getQueryParameter("v");
            if(id != null)
                return new YouTubeUtils.VideoType(id, start, false);
            List<String> path = uri.getPathSegments();
            if(path == null || path.size() < 2)
                return null;
            if(path.get(0).equals("clip"))
                return new YouTubeUtils.VideoType(path.get(1), start, true);
            if(!path.get(0).matches("(embed|v|shorts)"))
                return null;
            return new YouTubeUtils.VideoType(path.get(1), start, false);
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

}
