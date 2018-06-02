
package com.ensoft.imgurviewer.model.instagram;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EntryData {

    @SerializedName("ProfilePage")
    @Expose
    public List<ProfilePage> profilePage = new ArrayList<ProfilePage>();

}
