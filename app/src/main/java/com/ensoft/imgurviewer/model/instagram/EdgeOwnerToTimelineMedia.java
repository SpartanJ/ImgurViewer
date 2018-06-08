
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EdgeOwnerToTimelineMedia {

    @SerializedName("count")
    @Expose
    public long count;
    @SerializedName("page_info")
    @Expose
    public PageInfo pageInfo;
    @SerializedName("edges")
    @Expose
    public List<Edge> edges = new ArrayList<Edge>();

}
