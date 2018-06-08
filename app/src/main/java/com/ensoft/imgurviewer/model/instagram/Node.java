
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Node {

    @SerializedName("__typename")
    @Expose
    public String typename;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("edge_media_to_caption")
    @Expose
    public EdgeMediaToCaption edgeMediaToCaption;
    @SerializedName("shortcode")
    @Expose
    public String shortcode;
    @SerializedName("edge_media_to_comment")
    @Expose
    public EdgeMediaToComment edgeMediaToComment;
    @SerializedName("comments_disabled")
    @Expose
    public boolean commentsDisabled;
    @SerializedName("taken_at_timestamp")
    @Expose
    public long takenAtTimestamp;
    @SerializedName("dimensions")
    @Expose
    public Dimensions dimensions;
    @SerializedName("display_url")
    @Expose
    public String displayUrl;
    @SerializedName("edge_liked_by")
    @Expose
    public EdgeLikedBy edgeLikedBy;
    @SerializedName("edge_media_preview_like")
    @Expose
    public EdgeMediaPreviewLike edgeMediaPreviewLike;
    @SerializedName("gating_info")
    @Expose
    public Object gatingInfo;
    @SerializedName("media_preview")
    @Expose
    public Object mediaPreview;
    @SerializedName("owner")
    @Expose
    public Owner owner;
    @SerializedName("thumbnail_src")
    @Expose
    public String thumbnailSrc;
    @SerializedName("thumbnail_resources")
    @Expose
    public List<ThumbnailResource> thumbnailResources = new ArrayList<ThumbnailResource>();
    @SerializedName("is_video")
    @Expose
    public boolean isVideo;

}
