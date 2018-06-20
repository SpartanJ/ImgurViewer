package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
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
		if ( serviceSolver.isVideo( url ) )
		{
			resourceLoadListener.loadVideo( url, mediaType, thumbnailOrReferer );
		}
		else
		{
			resourceLoadListener.loadImage( url, thumbnailOrReferer );
		}
	}

	@Override
	public void onPathError( final String error )
	{
		Log.v( TAG, error );

		Toast.makeText( App.getInstance(), error, Toast.LENGTH_SHORT ).show();
	}
}
