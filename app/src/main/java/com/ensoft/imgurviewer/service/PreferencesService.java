package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesService
{
	public static final String PROXY_HOST = "proxyHost";
	public static final String PROXY_PORT = "proxyPort";

	protected Context mContext;

	public SharedPreferences getDefaultSharedPreferences()
	{
		return mContext.getSharedPreferences( getDefaultSharedPreferencesName( mContext ), getDefaultSharedPreferencesMode() );
	}

	private static String getDefaultSharedPreferencesName( Context context )
	{
		return context.getPackageName() + "_preferences";
	}

	private static int getDefaultSharedPreferencesMode()
	{
		return Context.MODE_PRIVATE;
	}

	public PreferencesService( Context context )
	{
		mContext = context;
	}

	public String getProxyHost()
	{
		return getDefaultSharedPreferences().getString( PROXY_HOST, "" );
	}

	public int getProxyPort()
	{
		return Integer.valueOf( getDefaultSharedPreferences().getString( PROXY_PORT, "8118" ) );
	}

	public void setProxyHost( String proxyHost )
	{
		getDefaultSharedPreferences().edit().putString( PROXY_HOST, proxyHost ).apply();
		RequestQueueService.getInstance().createRequestQueue();
	}

	public void setProxyPort( int proxyPort )
	{
		getDefaultSharedPreferences().edit().putString( PROXY_PORT, Integer.toString( proxyPort ) ).apply();
		RequestQueueService.getInstance().createRequestQueue();
	}
}
