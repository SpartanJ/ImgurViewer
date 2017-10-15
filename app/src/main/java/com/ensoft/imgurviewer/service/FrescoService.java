package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoService
{
	public void loadImage( Uri uri, Uri thumbnail, DraweeView view, ControllerImageInfoListener controllerListener )
	{
		ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource( uri );

		if ( UriUtil.isNetworkUri( uri ) )
		{
			imageRequestBuilder.setProgressiveRenderingEnabled( true );
		}
		else
		{
			imageRequestBuilder.setResizeOptions( new ResizeOptions( view.getLayoutParams().width, view.getLayoutParams().height) );
		}

		imageRequestBuilder.setAutoRotateEnabled( true );

		PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
			.setTapToRetryEnabled( true )
			.setControllerListener( controllerListener )
			.setImageRequest( imageRequestBuilder.build() )
			.setOldController( view.getController() )
			.setAutoPlayAnimations( true );

		if ( null != thumbnail )
		{
			draweeControllerBuilder.setLowResImageRequest( ImageRequest.fromUri( thumbnail ) );
		}

		DraweeController draweeController = draweeControllerBuilder.build();

		view.setController( draweeController );
	}

	public void loadImage( Uri uri, DraweeView view, ControllerImageInfoListener controllerListener )
	{
		loadImage( uri, null, view, controllerListener );
	}
	
	public void clearCaches()
	{
		Fresco.getImagePipeline().clearCaches();
	}
}
