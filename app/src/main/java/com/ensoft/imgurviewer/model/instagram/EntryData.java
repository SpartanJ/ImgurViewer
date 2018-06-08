
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EntryData {

    @SerializedName("ProfilePage")
    @Expose
    public List<ProfilePage> profilePage = new ArrayList<ProfilePage>();

}
