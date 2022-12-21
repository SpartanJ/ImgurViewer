package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.facebook.common.util.UriUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

public class ProxyUtils
{
	protected Proxy mProxy;
	
	public void setProxy( Proxy proxy )
	{
		mProxy = proxy;
	}
	
	public Proxy getProxy()
	{
		return mProxy;
	}
	
	public HttpURLConnection openConnectionTo( URL url, Proxy proxy ) throws IOException
	{
		return (HttpURLConnection) ( proxy != null ? url.openConnection( proxy ) : url.openConnection() );
	}
	
	public HttpURLConnection openConnectionTo( Uri uri, Proxy proxy ) throws IOException
	{
		return openConnectionTo( UriUtil.uriToUrl( uri ), proxy );
	}
	
	public HttpURLConnection openConnectionTo( Uri uri ) throws IOException
	{
		return openConnectionTo( uri, mProxy );
	}
	
	public HttpURLConnection openConnectionTo( URL uri ) throws IOException
	{
		return openConnectionTo( uri, mProxy );
	}
	
	public void updateProxySettings( PreferencesService preferencesService )
	{
		Properties systemProperties = System.getProperties();
		if ( preferencesService.getProxyHost() != null && !preferencesService.getProxyHost().isEmpty() )
		{
			systemProperties.setProperty( "http.proxyHost", preferencesService.getProxyHost() );
			systemProperties.setProperty( "http.proxyPort", String.valueOf( preferencesService.getProxyPort() ) );
		} else {
			systemProperties.remove( "http.proxyHost" );
			systemProperties.remove( "http.proxyPort" );
		}
	}
}
