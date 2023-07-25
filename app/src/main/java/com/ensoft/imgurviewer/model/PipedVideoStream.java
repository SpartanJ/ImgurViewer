package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class PipedVideoStream {

    @SerializedName( "url" )
    protected String url;

    @SerializedName( "quality" )
    protected String quality;

    @SerializedName( "mimeType" )
    protected String mimeType;

    @SerializedName( "codec" )
    protected String codec;

    @SerializedName( "videoOnly" )
    protected boolean videoOnly;

    @SerializedName( "width" )
    protected int width;

    @SerializedName( "height" )
    protected int height;

    @SerializedName( "fps" )
    protected int fps;

    public String getMimeType() {
        return mimeType;
    }
    public String getVideoUrl() {
        return url;
    }

    public String getQuality() {
        return quality;
    }

    public String getCodec() {
        return codec;
    }

    public boolean isVideoOnly() {
        return videoOnly;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFps() {
        return fps;
    }
}
