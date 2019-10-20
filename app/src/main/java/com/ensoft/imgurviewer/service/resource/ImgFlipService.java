package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;

public class ImgFlipService extends MediaServiceSolver
{
	private static final String IMGFLIP_DOMAIN = "imgflip.com";
	private static final String IMGFLIP_IMAGE_PATH = "https://i.imgflip.com/{id}.jpg";
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		try
		{
			Uri uriSolved = Uri.parse( IMGFLIP_IMAGE_PATH.replace( "{id}", uri.getLastPathSegment() ) );
			
			pathResolverListener.onPathResolved( uriSolved, UriUtils.guessMediaTypeFromUri( uriSolved ), null );
		}
		catch ( Exception e )
		{
			pathResolverListener.onPathError( uri, e.toString() );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.getHost().equals( IMGFLIP_DOMAIN ) && uri.getPathSegments().size() > 0 && uri.getPathSegments().get( 0 ).equals( "i" );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
}
