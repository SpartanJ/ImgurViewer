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
	@Override
	public void onCreate()
	{
		super.onCreate();

		ImagePipelineConfig config = ImagePipelineConfig.newBuilder( getBaseContext() )
			.setDownsampleEnabled( true )
			.build();

		RequestQueueService.init( this );

		Fresco.initialize( this, config );

		ACRA.init( this );
	}
}