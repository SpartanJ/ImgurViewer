package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class YouTubeVideoFormat {

    @SerializedName("url")
    public String url;

    @SerializedName("height")
    public int height;

    @SerializedName("bitrate")
    public int bitrate;

    public boolean hasVideo() {
        return height > 0;
    }

}
