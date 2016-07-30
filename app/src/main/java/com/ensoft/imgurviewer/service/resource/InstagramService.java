package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.imgurviewer.R;

public class InstagramService extends ImageServiceSolver
{
	public static final String TAG = InstagramService.class.getCanonicalName();
	protected static final String INSTAGRAM_DOMAIN = "instagram.com";

	public String getMediaUrl( Uri uri )
	{
		String url = uri.toString();
		String mediaSep = "/p/";

		int pos = url.indexOf( mediaSep );

		if ( -1 != pos )
		{
			int endPos = url.indexOf( "/", pos + mediaSep.length() );
			String mediaId;

			if ( -1 != endPos )
			{
				mediaId = url.substring( pos + mediaSep.length(), endPos );
			}
			else
			{
				mediaId = url.substring( pos + mediaSep.length() );
			}

			return "https://www.instagram.com/p/" + mediaId + "/media/";
		}

		return null;
	}

	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
		String mediaUrl = getMediaUrl( uri );

		if ( mediaUrl != null )
		{
			pathResolverListener.onPathResolved( Uri.parse( mediaUrl + "?size=l" ), Uri.parse( mediaUrl + "?size=t" ) );
		}
		else
		{
			pathResolverListener.onPathError( App.getInstance().getString( R.string.could_not_resolve_url ) );
		}
	}

	@Override
	public boolean isServicePath( Uri uri )
	{
		String uriStr = uri.toString();

		return (	uriStr.startsWith( "https://" + INSTAGRAM_DOMAIN ) ||
					uriStr.startsWith( "http://" + INSTAGRAM_DOMAIN ) ||
					uriStr.startsWith( "https://www." + INSTAGRAM_DOMAIN ) ||
					uriStr.startsWith( "http://www." + INSTAGRAM_DOMAIN ));
	}

	@Override
	public boolean isGallery( Uri uri )
	{
		int countedMatches = StringUtils.countMatches( uri.toString(), "/" );

		return getMediaUrl( uri ) == null && ( countedMatches == 4 || countedMatches == 3 );
	}

	public boolean isInstagramProfile( Uri uri )
	{
		return isServicePath( uri ) && isGallery( uri );
	}
}
