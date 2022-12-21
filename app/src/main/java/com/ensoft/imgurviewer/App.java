package com.ensoft.imgurviewer;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.ProxyUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.restafari.network.service.RequestService;
import com.ensoft.restafari.network.service.RequestServiceOptions;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

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
			OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().connectTimeout( 30, TimeUnit.SECONDS )
				.addInterceptor( chain -> chain.proceed( chain.request().newBuilder().addHeader( "User-Agent", UriUtils.getDefaultUserAgent() ).build() ) );
			
			if ( getPreferencesService().getProxyHost() != null && !getPreferencesService().getProxyHost().isEmpty() )
			{
				Proxy proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( getPreferencesService().getProxyHost(), getPreferencesService().getProxyPort() ) );
				
				proxyUtils.setProxy( proxy );
				
				okHttpClientBuilder.proxy( proxy );
			}
			
			ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder( this, okHttpClientBuilder.build() )
				.build();
			
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
