package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.InstagramProfileModel;
import com.ensoft.imgurviewer.model.instagram.InstagramProfileBaseModel;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

public class InstagramService extends MediaServiceSolver implements AlbumProvider
{
	public static final String TAG = InstagramService.class.getCanonicalName();
	private static final String INSTAGRAM_DOMAIN = "instagram.com";
	private static final String PROFILE_START_STR = "<script type=\"text/javascript\">window._sharedData = ";
	private static final String PROFILE_END_STR = ";</script>";
	
	private InstagramProfileModel getProfileJson( String response )
	{
		try
		{
			int start = response.indexOf( PROFILE_START_STR );
			int end = response.indexOf( PROFILE_END_STR, start );
			String json = response.substring( start + PROFILE_START_STR.length(), end );
			InstagramProfileBaseModel instagramProfileBaseModel = new Gson().fromJson( json, InstagramProfileBaseModel.class );
			
			return new InstagramProfileModel( instagramProfileBaseModel );
		}
		catch ( Exception e ) {}
		
		return null;
	}
	
	@Override
	public void getAlbum( Uri uri, AlbumSolverListener albumSolverListener )
	{
		String profileMediaUrl = uri.toString();
		
		StringRequest jsonObjectRequest = new StringRequest( profileMediaUrl, response ->
		{
			try
			{
				Log.v( TAG, response );
				
				InstagramProfileModel profile = getProfileJson( response );
				
				if ( null != profile )
				{
					albumSolverListener.onAlbumResolved( profile.getImages() );
				}
				else
				{
					albumSolverListener.onAlbumError( App.getInstance().getString( R.string.failedFetchProfile ) );
				}
			}
			catch ( Exception e )
			{
				Log.v( TAG, e.getMessage() );
				
				albumSolverListener.onAlbumError( e.toString() );
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			albumSolverListener.onAlbumError( error.toString() );
		} )
		{
		};
		
		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
	
	@Override
	public boolean isAlbum( Uri uri )
	{
		return isServicePath( uri ) && isGallery( uri );
	}
	
	public String getMediaUrl( Uri uri )
	{
		String url = uri.toString();
		String mediaSep = url.contains( "/p/" ) ? "/p/" : "/reel/";
		
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
				pathResolverListener.onPathResolved( videoUrl,  UriUtils.guessMediaTypeFromUri( videoUrl ), uri );
			}
			else
			{
				String mediaUrl = getMediaUrl( uri );
				
				if ( mediaUrl != null )
				{
					pathResolverListener.onPathResolved( Uri.parse( mediaUrl + "?size=l" ), UriUtils.guessMediaTypeFromUri( Uri.parse( mediaUrl ) ), Uri.parse( mediaUrl + "?size=t" ) );
				}
				else
				{
					pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.could_not_resolve_url ) );
				}
			}
		}, error ->
		{
			Log.v( TAG, error.toString() );
			
			pathResolverListener.onPathError( uri, error.toString() );
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
}
