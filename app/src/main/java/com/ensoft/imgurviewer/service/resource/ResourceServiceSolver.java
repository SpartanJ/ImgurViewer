package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.GenericPathResolverListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;

public class ResourceServiceSolver
{
	MediaServiceSolver serviceSolver;
	ResourceLoadListener resourceLoadListener;
	Class<?> galleryViewClass;
	
	public ResourceServiceSolver( MediaServiceSolver serviceSolver, ResourceLoadListener resourceLoadListener, Class<?> galleryViewClass )
	{
		this.serviceSolver = serviceSolver;
		this.resourceLoadListener = resourceLoadListener;
		this.galleryViewClass = galleryViewClass;
	}
	
	public boolean solve( Uri uri )
	{
		if ( "http".equals( uri.getScheme() ) )
		{
			uri = Uri.parse( uri.toString().replaceFirst( "http", "https" ) );
		}
		
		if ( null != uri && serviceSolver.isServicePath( uri ) )
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
	
	public boolean isGallery( Uri uri )
	{
		return serviceSolver.isGallery( uri );
	}
	
	public boolean isSolvable( Uri uri )
	{
		if ( "http".equals( uri.getScheme() ) )
		{
			uri = Uri.parse( uri.toString().replaceFirst( "http", "https" ) );
		}
		
		return null != uri && serviceSolver.isServicePath( uri );
	}
	
	public Class<?> getGalleryViewClass()
	{
		return galleryViewClass;
	}
}
