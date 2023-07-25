package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.resource.MediaServiceSolver;

public class GenericPathResolverListener extends PathResolverListener
{
	public static final String TAG = GenericPathResolverListener.class.getCanonicalName();
	private ResourceLoadListener resourceLoadListener;

	public GenericPathResolverListener( MediaServiceSolver serviceSolver, ResourceLoadListener resourceLoadListener )
	{
		super( serviceSolver );
		this.resourceLoadListener = resourceLoadListener;
	}

	@Override
	public void onPathResolved( Uri url, MediaType mediaType, Uri thumbnailOrReferer )
	{
		if ( mediaType == MediaType.VIDEO_MP4 )
		{
			resourceLoadListener.loadVideo( url, mediaType, thumbnailOrReferer );
		}
		else
		{
			resourceLoadListener.loadImage( url, thumbnailOrReferer );
		}
	}

	@Override
	public void onPathError( Uri url, final String error )
	{
		resourceLoadListener.loadFailed( url, error );
	}
}
