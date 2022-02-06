package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.imgurviewer.R;

public abstract class MediaServiceSolver
{
	public abstract void getPath( Uri uri, final PathResolverListener pathResolverListener );
	
	public abstract boolean isServicePath( Uri uri );
	
	public boolean isVideo( Uri uri )
	{
		return UriUtils.isVideoUrl( uri );
	}
	
	public abstract boolean isGallery( Uri uri );
	
	protected void sendPathResolved( final PathResolverListener pathResolverListener, final Uri uri, final MediaType mediaType, final Uri referer )
	{
		new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathResolved( uri, mediaType, referer ) );
	}
	
	protected void sendPathError( Uri uri, final PathResolverListener pathResolverListener, String errorMessage )
	{
		new Handler( Looper.getMainLooper() ).post( () -> pathResolverListener.onPathError( uri, errorMessage ) );
	}
	
	protected void sendPathError( Uri uri, final PathResolverListener pathResolverListener, int errorMessage )
	{
		sendPathError( uri, pathResolverListener, App.getInstance().getString( errorMessage ) );
	}
	
	protected void sendPathError( Uri uri, final PathResolverListener pathResolverListener )
	{
		sendPathError( uri, pathResolverListener, R.string.could_not_resolve_video_url );
	}
}
