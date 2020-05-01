package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class EromeService extends BasicVideoServiceSolver implements AlbumProvider
{
	@Override
	public String getDomain()
	{
		return "erome.com";
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/a/", "/i/" };
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<source src=\"", "<div class=\"img\" data-src=\"" };
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\" type='video/mp4'", "\" >" };
	}
	
	@Override
	protected String parseUrlString( String urlString )
	{
		return urlString;
	}
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		return getFirstVideoUrlFromResponse( response );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, getDomain(), "/a/" );
	}
	
	private ImgurImage[] getMedia( String response )
	{
		ArrayList<ImgurImage> mediaList = new ArrayList<>();
		
		String[] needleStart = getNeedleStart();
		String[] needleEnd = getNeedleEnd();
		
		for ( int i = 0; i < needleStart.length; i++ )
		{
			Matcher matcher = StringUtils.getStringMatcher( response, needleStart[i], needleEnd[i] );
			
			int id = 1;
			while ( matcher.find() )
			{
				String res = matcher.group();
				String image = res.substring( needleStart[i].length(), res.length() - needleEnd[i].length() );
				ImgurImage imgurImage = new ImgurImage( String.valueOf(id), image );
				
				if ( imgurImage.hasVideo() )
				{
					int start = matcher.start();
					int maxTry = 350;
					
					while ( start > 4 && maxTry > 0 ) {
						if ( response.charAt( start - 2 ) == 'j' &&  response.charAt( start - 1 ) == 'p' && response.charAt( start ) == 'g' )
						{
							int startThumbIndex = start;
							while ( startThumbIndex > 0 )
							{
								if ( response.charAt( startThumbIndex ) == '"' )
								{
									String videoThumbnail = response.substring( startThumbIndex + 1, start + 1 );
									
									imgurImage = new ImgurImage( String.valueOf( id ), videoThumbnail, Uri.parse( videoThumbnail ), Uri.parse( image ), ""  );
									break;
								}
								
								startThumbIndex--;
							}
						}
						
						start--;
						maxTry--;
					}
				}
				
				mediaList.add( imgurImage );
				id++;
			}
		}
		
		return mediaList.toArray( new ImgurImage[ 0 ] );
	}
	
	public void getAlbum( Uri uri, final AlbumSolverListener albumSolverListener )
	{
		RequestService.getInstance().makeStringRequest( Request.Method.GET, uri.toString(), new ResponseListener<String>()
		{
			public ThreadMode getThreadMode()
			{
				return ThreadMode.ASYNC;
			}
			
			@Override
			public void onRequestSuccess( Context context, String response )
			{
				try
				{
					ImgurImage[] imgurImages = getMedia( response );
					
					if ( imgurImages.length > 0 )
					{
						new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumResolved( imgurImages ) );
					}
					else
					{
						new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
					}
				}
				catch ( Exception ignored )
				{
					new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				if ( null != errorMessage )
					Log.v( getDomain(), errorMessage );
				
				new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumError( null != errorMessage ? errorMessage : "" ) );
			}
		} );
	}
	
	@Override
	public boolean isAlbum( Uri uri )
	{
		return isGallery( uri );
	}
}
