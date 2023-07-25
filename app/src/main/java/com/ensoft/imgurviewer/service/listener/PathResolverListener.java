package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.resource.MediaServiceSolver;

public abstract class PathResolverListener
{
	protected MediaServiceSolver serviceSolver;
	
	public PathResolverListener( MediaServiceSolver serviceSolver )
	{
		this.serviceSolver = serviceSolver;
	}
	
	public MediaServiceSolver getServiceSolver()
	{
		return serviceSolver;
	}
	
	public void onPathResolved( Uri url, MediaType mediaType, Uri thumbnailOrReferer ) {
		this.onPathResolved(url, mediaType, thumbnailOrReferer, null);
	}

	public abstract void onPathResolved ( Uri url, MediaType mediaType, Uri thumbnailOrReferer, Object additionalData);

	public abstract void onPathError( Uri url, String error );
}
