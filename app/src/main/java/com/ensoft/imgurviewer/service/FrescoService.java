package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoService
{
	public void loadImage( Uri uri, DraweeView view, ControllerImageInfoListener controllerListener )
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

		DraweeController draweeController = Fresco.newDraweeControllerBuilder()
			.setTapToRetryEnabled( true )
			.setControllerListener( controllerListener )
			.setImageRequest( imageRequestBuilder.build() )
			.setOldController( view.getController() )
			.setAutoPlayAnimations( true )
			.build();

		view.setController( draweeController );
	}
}
