
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PageInfo {

    @SerializedName("has_next_page")
    @Expose
    public boolean hasNextPage;
    @SerializedName("end_cursor")
    @Expose
    public String endCursor;

}
