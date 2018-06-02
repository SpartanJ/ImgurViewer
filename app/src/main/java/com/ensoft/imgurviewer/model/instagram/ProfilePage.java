
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePage {

    @SerializedName("logging_page_id")
    @Expose
    public String loggingPageId;
    @SerializedName("show_suggested_profiles")
    @Expose
    public boolean showSuggestedProfiles;
    @SerializedName("graphql")
    @Expose
    public Graphql graphql;

}
