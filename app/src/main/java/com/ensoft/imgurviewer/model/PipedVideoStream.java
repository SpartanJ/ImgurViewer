package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class PipedVideoStream {

    @SerializedName( "url" )
    protected String url;

    @SerializedName( "mimeType" )
    protected String mimeType;

    public String getMimeType() {
        return mimeType;
    }
    public String getVideoUrl() {
        return url;
    }

}
