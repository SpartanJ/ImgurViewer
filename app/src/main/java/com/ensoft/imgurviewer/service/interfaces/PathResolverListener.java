package com.ensoft.imgurviewer.service.interfaces;

import android.net.Uri;

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
