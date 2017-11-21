package com.ensoft.imgurviewer.service;

import android.content.Context;

import com.ensoft.restafari.network.service.NetworkPreferencesService;

public class PreferencesService extends NetworkPreferencesService
{
	public static final String MUTE_VIDEOS = "muteVideos";
	
	public PreferencesService( Context context )
	{
		super( context );
	}
	
	public boolean videosMuted()
	{
		return getMuteVideos();
	}
	
	public boolean getMuteVideos()
	{
		return getDefaultSharedPreferences().getBoolean( MUTE_VIDEOS, false );
	}
	
	public void setMuteVideos( boolean muteVideos )
	{
		getDefaultSharedPreferences().edit().putBoolean( MUTE_VIDEOS, muteVideos ).apply();
	}
}
