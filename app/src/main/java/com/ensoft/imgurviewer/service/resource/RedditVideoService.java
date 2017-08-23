package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.imgurviewer.R;

public class RedditVideoService extends ImageServiceSolver
{
	public static final String V_REDD_IT_DOMAIN = "v.redd.it";
	public static final String V_REDD_IT_VIDEO_URL = "https://v.redd.it/%s/DASH_2_4_M";
	
	protected String getId( Uri uri )
	{
		String url = uri.toString();
		String[] split = url.split( "/" );
		
		if ( split.length >= 2 )
		{
			if ( !split[ split.length - 1 ].startsWith( "DASH_" ) )
			{
				return split[ split.length - 1 ];
			}
			else
			{
				return split[ split.length - 2 ];
			}
		}
		
		return null;
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		String id = getId( uri );
		
		if ( id != null )
		{
			String url = String.format( V_REDD_IT_VIDEO_URL, id );
			
			pathResolverListener.onPathResolved( Uri.parse( url ), null );
		}
		else
		{
			pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_url ) );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return uri.toString().contains( V_REDD_IT_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return false;
	}
	
	@Override
	public boolean isVideo( Uri uri )
	{
		return true;
	}
	
	@Override
	public boolean isVideo( String uri )
	{
		return true;
	}
}
