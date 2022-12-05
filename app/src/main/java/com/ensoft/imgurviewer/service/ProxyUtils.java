package com.ensoft.imgurviewer.service;

import java.net.Proxy;
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
