package com.ensoft.imgurviewer;

import android.app.Application;

import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.restafari.network.service.RequestService;
import com.ensoft.restafari.network.service.RequestServiceOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
	formUri = "http://imgurviewer.ensoft-dev.com/"
)
public class App extends Application
{
	protected static App instance;
	protected PreferencesService preferencesService;
	protected int activityCount = 0;
	
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
		
		ACRA.init( this );
		
		RequestServiceOptions requestServiceOptions = new RequestServiceOptions.Builder().
			setProxyHost( getPreferencesService().getProxyHost() ).
			setProxyPort( getPreferencesService().getProxyPort() ).
			build();
		
		RequestService.init( this, requestServiceOptions );
	}
	
	protected void onDestroy()
	{
		Fresco.shutDown();
	}
	
	public void addActivity()
	{
		if ( 0 == activityCount )
		{
			BigImageViewer.initialize( FrescoImageLoader.with( this ) );
		}
		
		activityCount++;
	}
	
	public void destroyActivity()
	{
		activityCount--;
		
		if ( 0 == activityCount )
		{
			onDestroy();
		}
	}
	
	public PreferencesService getPreferencesService()
	{
		return preferencesService;
	}
}