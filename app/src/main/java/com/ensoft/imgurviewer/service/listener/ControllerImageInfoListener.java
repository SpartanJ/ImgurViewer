package com.ensoft.imgurviewer.service.listener;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

public abstract class ControllerImageInfoListener implements ControllerListener<ImageInfo>
{
	@Override
	public void onSubmit( String id, Object callerContext ) {}

	@Override
	public void onIntermediateImageSet( String id, ImageInfo imageInfo ) {}

	@Override
	public void onIntermediateImageFailed( String id, Throwable throwable ) {}

	@Override
	public void onRelease( String id ) {}
}
