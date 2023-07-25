package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class PipedStreamResponse {

    @SerializedName( "thumbnailUrl" )
    protected String thumbnail;
    @SerializedName( "videoStreams" )
    protected PipedVideoStream[] videoStreams;

    public PipedVideoStream[] getVideoStreams() {
        return videoStreams;
    }

    public String getThumbnailUrl() {
        return thumbnail;
    }
}
