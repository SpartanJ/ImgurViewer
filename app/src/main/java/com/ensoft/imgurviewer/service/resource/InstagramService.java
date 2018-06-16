package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
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
	
	protected Uri getVideoUrlFromResponse( String response )
	{
		return UriUtils.getUriMatch( response, "og:video:secure_url\" content=\"", "\"" );
	}
	
	@Override
	public void getPath( final Uri uri, final PathResolverListener pathResolverListener )
	{
		StringRequest stringRequest = new StringRequest( uri.toString(), response ->
		{
			Uri videoUrl = getVideoUrlFromResponse( response );
			
			if ( videoUrl != null )
			{
				pathResolverListener.onPathResolved( videoUrl, null );
			}
			else
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
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( error.toString() );
		} );
		
		RequestService.getInstance().addToRequestQueue( stringRequest );
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, INSTAGRAM_DOMAIN );
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
