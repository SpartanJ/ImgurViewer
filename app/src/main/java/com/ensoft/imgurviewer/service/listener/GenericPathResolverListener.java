package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.resource.ImageServiceSolver;

public class GenericPathResolverListener extends PathResolverListener
{
	public static final String TAG = GenericPathResolverListener.class.getCanonicalName();
	private ResourceLoadListener resourceLoadListener;

	public GenericPathResolverListener( ImageServiceSolver serviceSolver, ResourceLoadListener resourceLoadListener )
	{
		super( serviceSolver );
		this.resourceLoadListener = resourceLoadListener;
	}

	@Override
	public void onPathResolved( Uri url, Uri thumbnail )
	{
		if ( serviceSolver.isVideo( url ) )
		{
			resourceLoadListener.loadVideo( url );
		}
		else
		{
			resourceLoadListener.loadImage( url, thumbnail );
		}
	}

	@Override
	public void onPathError( final String error )
	{
		Log.v( TAG, error );

		Toast.makeText( App.getInstance(), error, Toast.LENGTH_SHORT ).show();
	}
}
