package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.service.resource.ImageServiceSolver;

public abstract class PathResolverListener
{
	protected ImageServiceSolver serviceSolver;

	public PathResolverListener( ImageServiceSolver serviceSolver )
	{
		this.serviceSolver = serviceSolver;
	}

	public ImageServiceSolver getServiceSolver()
	{
		return serviceSolver;
	}

	public abstract void onPathResolved( Uri url, Uri thumbnail );

	public abstract void onPathError( String error );
}
