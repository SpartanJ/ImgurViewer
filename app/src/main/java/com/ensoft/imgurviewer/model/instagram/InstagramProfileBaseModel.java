
package com.ensoft.imgurviewer.model.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InstagramProfileBaseModel {

    @SerializedName("activity_counts")
    @Expose
    public ActivityCounts activityCounts;
    @SerializedName("config")
    @Expose
    public Config config;
    @SerializedName("supports_es6")
    @Expose
    public boolean supportsEs6;
    @SerializedName("country_code")
    @Expose
    public String countryCode;
    @SerializedName("language_code")
    @Expose
    public String languageCode;
    @SerializedName("locale")
    @Expose
    public String locale;
    @SerializedName("entry_data")
    @Expose
    public EntryData entryData;
    @SerializedName("hostname")
    @Expose
    public String hostname;
    @SerializedName("platform")
    @Expose
    public String platform;
    @SerializedName("rhx_gis")
    @Expose
    public String rhxGis;
    @SerializedName("nonce")
    @Expose
    public String nonce;
    @SerializedName("rollout_hash")
    @Expose
    public String rolloutHash;
    @SerializedName("bundle_variant")
    @Expose
    public String bundleVariant;
    @SerializedName("probably_has_app")
    @Expose
    public boolean probablyHasApp;
    @SerializedName("show_app_install")
    @Expose
    public boolean showAppInstall;

}
