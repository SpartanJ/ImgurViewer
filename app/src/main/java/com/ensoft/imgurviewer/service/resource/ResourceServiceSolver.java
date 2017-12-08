package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.GenericPathResolverListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;

public class ResourceServiceSolver
{
	ImageServiceSolver serviceSolver;
	ResourceLoadListener resourceLoadListener;
	Class<?> galleryViewClass;
	
	public ResourceServiceSolver( ImageServiceSolver serviceSolver, ResourceLoadListener resourceLoadListener, Class<?> galleryViewClass )
	{
		this.serviceSolver = serviceSolver;
		this.resourceLoadListener = resourceLoadListener;
		this.galleryViewClass = galleryViewClass;
	}
	
	public boolean solve( Uri uri )
	{
		if ( serviceSolver.isServicePath( uri ) )
		{
			if ( serviceSolver.isGallery( uri ) )
			{
				if ( null != galleryViewClass )
				{
					resourceLoadListener.loadAlbum( uri, galleryViewClass );
				}
			}
			else
			{
				serviceSolver.getPath( uri, new GenericPathResolverListener( serviceSolver, resourceLoadListener ) );
			}
			
			return true;
		}
		
		return false;
	}
}
