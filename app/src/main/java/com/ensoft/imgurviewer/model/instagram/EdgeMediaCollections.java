
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EdgeMediaCollections {

    @SerializedName("count")
    @Expose
    public long count;
    @SerializedName("page_info")
    @Expose
    public PageInfo__ pageInfo;
    @SerializedName("edges")
    @Expose
    public List<Object> edges = new ArrayList<Object>();

}
