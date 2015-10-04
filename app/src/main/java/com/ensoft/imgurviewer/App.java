package com.ensoft.imgurviewer;

import android.app.Application;

import com.ensoft.imgurviewer.service.RequestQueueService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
	formUri = "http://imgurviewer.ensoft-dev.com/"
)
public class App extends Application
{
	protected static App sInstance;
	protected int mActivityCount = 0;

	public static App getInstance()
	{
		return sInstance;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		sInstance = this;

		RequestQueueService.init( this );

		ACRA.init( this );
	}

	protected void onDestroy()
	{
		Fresco.shutDown();
	}

	public void addActivity()
	{
		if ( 0 == mActivityCount )
		{
			ImagePipelineConfig config = ImagePipelineConfig.newBuilder( this )
				.setDownsampleEnabled( true )
				.build();

			Fresco.initialize( this, config );
		}

		mActivityCount++;
	}

	public void destroyActivity()
	{
		mActivityCount--;

		if ( 0 == mActivityCount )
		{
			onDestroy();
		}
	}
}