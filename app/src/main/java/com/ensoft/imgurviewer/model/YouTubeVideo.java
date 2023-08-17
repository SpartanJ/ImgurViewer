package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YouTubeVideo {

    @SerializedName("playabilityStatus")
    protected Status playabilityStatus;

    @SerializedName("streamingData")
    protected StreamingData streamingData;

    public String getStatus() {
        return this.playabilityStatus.status;
    }

    public List<YouTubeVideoFormat> getFormats() {
        return this.streamingData.formats;
    }

    static class Status {
        @SerializedName("status")
        public String status;
    }

    static class StreamingData {
        @SerializedName("formats")
        protected List<YouTubeVideoFormat> formats;
    }

}

