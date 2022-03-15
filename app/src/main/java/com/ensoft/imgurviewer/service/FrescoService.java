package com.ensoft.imgurviewer.service;

import android.graphics.Point;
import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoService
{
	public void loadImage( Uri uri, Uri thumbnail, DraweeView view, ControllerImageInfoListener controllerListener, Point resizeOption )
	{
		ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource( uri );
		
		imageRequestBuilder.setRotationOptions( RotationOptions.autoRotate() );
		imageRequestBuilder.setLocalThumbnailPreviewsEnabled( true );
		
		if ( resizeOption.x != 0 && resizeOption.y != 0 )
		{
			imageRequestBuilder.setResizeOptions( new ResizeOptions( resizeOption.x, resizeOption.y ) );
		}
		
		PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
			.setTapToRetryEnabled( true )
			.setControllerListener( controllerListener )
			.setImageRequest( imageRequestBuilder.build() )
			.setOldController( view.getController() )
			.setAutoPlayAnimations( true );
		
		if ( null != thumbnail && !Uri.EMPTY.equals(thumbnail) )
		{
			draweeControllerBuilder.setLowResImageRequest( ImageRequest.fromUri( thumbnail ) );
		}
		
		DraweeController draweeController = draweeControllerBuilder.build();
		
		view.setController( draweeController );
	}
	
	public void clearCaches()
	{
		Fresco.getImagePipeline().clearCaches();
	}
}
