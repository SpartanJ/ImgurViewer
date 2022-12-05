package com.ensoft.imgurviewer;

import android.app.Application;
import android.content.pm.PackageInfo;

import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.restafari.network.service.RequestService;
import com.ensoft.restafari.network.service.RequestServiceOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.producers.HttpUrlConnectionNetworkFetcher;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;

public class App extends Application
{
	protected static App instance;
	protected PreferencesService preferencesService;
	
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
		
		RequestServiceOptions requestServiceOptions = new RequestServiceOptions.Builder().
			setProxyHost( getPreferencesService().getProxyHost() ).
			setProxyPort( getPreferencesService().getProxyPort() ).
			build();
		
		RequestService.init( this, requestServiceOptions );
		
		ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this )
			.setNetworkFetcher( new HttpUrlConnectionNetworkFetcher( UriUtils.getDefaultUserAgent(),
				HttpUrlConnectionNetworkFetcher.HTTP_DEFAULT_TIMEOUT ) ).build();
		
		BigImageViewer.initialize( FrescoImageLoader.with( this, config ) );
	}
	
	public PreferencesService getPreferencesService()
	{
		return preferencesService;
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