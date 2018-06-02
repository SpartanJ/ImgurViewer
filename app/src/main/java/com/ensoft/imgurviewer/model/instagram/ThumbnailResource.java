
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThumbnailResource {

    @SerializedName("src")
    @Expose
    public String src;
    @SerializedName("config_width")
    @Expose
    public long configWidth;
    @SerializedName("config_height")
    @Expose
    public long configHeight;

}
