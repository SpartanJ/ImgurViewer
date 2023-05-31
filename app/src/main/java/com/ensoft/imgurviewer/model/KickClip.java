package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class KickClip {

    @SerializedName( "video_url" )
    protected String url;

    public String getVideoUrl() {
        return url;
    }

}
