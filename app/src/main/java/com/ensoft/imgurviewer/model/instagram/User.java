
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("biography")
    @Expose
    public String biography;
    @SerializedName("blocked_by_viewer")
    @Expose
    public boolean blockedByViewer;
    @SerializedName("country_block")
    @Expose
    public boolean countryBlock;
    @SerializedName("external_url")
    @Expose
    public Object externalUrl;
    @SerializedName("external_url_linkshimmed")
    @Expose
    public Object externalUrlLinkshimmed;
    @SerializedName("edge_followed_by")
    @Expose
    public EdgeFollowedBy edgeFollowedBy;
    @SerializedName("followed_by_viewer")
    @Expose
    public boolean followedByViewer;
    @SerializedName("edge_follow")
    @Expose
    public EdgeFollow edgeFollow;
    @SerializedName("follows_viewer")
    @Expose
    public boolean followsViewer;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("has_blocked_viewer")
    @Expose
    public boolean hasBlockedViewer;
    @SerializedName("highlight_reel_count")
    @Expose
    public long highlightReelCount;
    @SerializedName("has_requested_viewer")
    @Expose
    public boolean hasRequestedViewer;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("is_private")
    @Expose
    public boolean isPrivate;
    @SerializedName("is_verified")
    @Expose
    public boolean isVerified;
    @SerializedName("mutual_followers")
    @Expose
    public Object mutualFollowers;
    @SerializedName("profile_pic_url")
    @Expose
    public String profilePicUrl;
    @SerializedName("profile_pic_url_hd")
    @Expose
    public String profilePicUrlHd;
    @SerializedName("requested_by_viewer")
    @Expose
    public boolean requestedByViewer;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("connected_fb_page")
    @Expose
    public Object connectedFbPage;
    @SerializedName("edge_owner_to_timeline_media")
    @Expose
    public EdgeOwnerToTimelineMedia edgeOwnerToTimelineMedia;
    @SerializedName("edge_saved_media")
    @Expose
    public EdgeSavedMedia edgeSavedMedia;
    @SerializedName("edge_media_collections")
    @Expose
    public EdgeMediaCollections edgeMediaCollections;

}
