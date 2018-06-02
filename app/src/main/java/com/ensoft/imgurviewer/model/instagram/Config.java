
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("csrf_token")
    @Expose
    public String csrfToken;
    @SerializedName("viewer")
    @Expose
    public Viewer viewer;

}
