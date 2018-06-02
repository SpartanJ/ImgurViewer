
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityCounts {

    @SerializedName("comment_likes")
    @Expose
    public long commentLikes;
    @SerializedName("comments")
    @Expose
    public long comments;
    @SerializedName("likes")
    @Expose
    public long likes;
    @SerializedName("relationships")
    @Expose
    public long relationships;
    @SerializedName("usertags")
    @Expose
    public long usertags;

}
