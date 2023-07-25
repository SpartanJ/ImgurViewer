package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class PipedStreamResponse {

    @SerializedName( "videoStreams" )
    protected PipedVideoStream[] videoStreams;

    public PipedVideoStream[] getVideoStreams() {
        return videoStreams;
    }
}
