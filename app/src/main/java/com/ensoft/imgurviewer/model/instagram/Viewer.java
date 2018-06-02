
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viewer {

    @SerializedName("biography")
    @Expose
    public String biography;
    @SerializedName("external_url")
    @Expose
    public Object externalUrl;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("has_profile_pic")
    @Expose
    public boolean hasProfilePic;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("profile_pic_url")
    @Expose
    public String profilePicUrl;
    @SerializedName("profile_pic_url_hd")
    @Expose
    public String profilePicUrlHd;
    @SerializedName("username")
    @Expose
    public String username;

}
