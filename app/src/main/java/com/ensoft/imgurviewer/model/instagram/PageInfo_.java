
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PageInfo_ {

    @SerializedName("has_next_page")
    @Expose
    public boolean hasNextPage;
    @SerializedName("end_cursor")
    @Expose
    public Object endCursor;

}
