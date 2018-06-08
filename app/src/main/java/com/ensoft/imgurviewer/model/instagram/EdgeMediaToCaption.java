
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EdgeMediaToCaption {

    @SerializedName("edges")
    @Expose
    public List<Edge_> edges = new ArrayList<Edge_>();

}
