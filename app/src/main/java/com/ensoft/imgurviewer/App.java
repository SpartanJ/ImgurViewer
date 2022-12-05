package com.ensoft.imgurviewer;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.ensoft.imgurviewer.service.HttpUrlConnectionNetworkFetcherProxied;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.ProxyUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.restafari.network.service.RequestService;
import com.ensoft.restafari.network.service.RequestServiceOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.producers.HttpUrlConnectionNetworkFetcher;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class App extends Application
{
	protected static App instance;
	protected PreferencesService preferencesService;
	protected ProxyUtils proxyUtils;
	
	public static App getInstance()
	{
		return instance;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		instance = this;
		
		preferencesService = new PreferencesService( this );
		
		proxyUtils = new ProxyUtils();
		
		proxyUtils.updateProxySettings( preferencesService );
		
		RequestServiceOptions requestServiceOptions = new RequestServiceOptions.Builder().
			setProxyHost( getPreferencesService().getProxyHost() ).
			setProxyPort( getPreferencesService().getProxyPort() ).
			build();
		
		RequestService.init( this, requestServiceOptions );
		
		new Thread( () -> {
			NetworkFetcher networkFetcher;
			
			if ( getPreferencesService().getProxyHost() != null && !getPreferencesService().getProxyHost().isEmpty() )
			{
				Proxy proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( getPreferencesService().getProxyHost(), getPreferencesService().getProxyPort() ) );
				
				proxyUtils.setProxy( proxy );
				
				networkFetcher = new HttpUrlConnectionNetworkFetcherProxied( proxy, UriUtils.getDefaultUserAgent(), HttpUrlConnectionNetworkFetcher.HTTP_DEFAULT_TIMEOUT );
			}
			else
			{
				networkFetcher = new HttpUrlConnectionNetworkFetcher( UriUtils.getDefaultUserAgent(), HttpUrlConnectionNetworkFetcher.HTTP_DEFAULT_TIMEOUT );
			}
			
			ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this )
				.setNetworkFetcher( networkFetcher ).build();
			
			BigImageViewer.initialize( FrescoImageLoader.with( this, config ) );
		} ).start();
	}
	
	public PreferencesService getPreferencesService()
	{
		return preferencesService;
	}
	
	public ProxyUtils getProxyUtils()
	{
		return proxyUtils;
	}
	
	public String getVersionName()
	{
		try
		{
			PackageInfo packageInfo = getPackageManager().getPackageInfo( getPackageName(), 0 );
			
			return packageInfo.versionName;
		}
		catch ( Exception e )
		{
			return "";
		}
	}
}