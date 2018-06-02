
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dimensions {

    @SerializedName("height")
    @Expose
    public long height;
    @SerializedName("width")
    @Expose
    public long width;

}
