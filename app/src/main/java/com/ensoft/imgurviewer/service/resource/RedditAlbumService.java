package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RedditAlbumService  extends MediaServiceSolver implements AlbumProvider
{
	public static final String TAG = RedditAlbumService.class.getCanonicalName();
	
	@Override
	public void getAlbum( Uri uri, AlbumSolverListener albumSolverListener )
	{
		String url = "https://www.reddit.com/comments/" + uri.getPathSegments().get( 1 ) + ".json?raw_json=1";
		
		RequestService.getInstance().makeStringRequest( Request.Method.GET, url, new ResponseListener<String>()
		{
			@Override
			public void onRequestSuccess( Context context, String resp )
			{
				try
				{
					JSONArray response = new JSONArray( resp );
					JSONObject data = response.getJSONObject( 0 ).getJSONObject( "data" ).getJSONArray( "children" ).getJSONObject( 0 ).getJSONObject( "data" );
					String title = data.getString( "title" );
					
					JSONArray galleryItems = data.getJSONObject( "gallery_data" ).getJSONArray( "items" );
					JSONObject mediaMetadata = data.getJSONObject( "media_metadata" );
					ImgurImage[] images = new ImgurImage[ galleryItems.length() ];
					
					for ( int i = 0; i < galleryItems.length(); i++ )
					{
					    JSONObject galleryItem = galleryItems.getJSONObject( i );
					    String mediaId = galleryItem.getString( "media_id" );
						JSONObject mediaObj = mediaMetadata.getJSONObject( mediaId );
						String mediaType = mediaObj.getString( "e" );
						
						if ( "Image".equals( mediaType ) || "AnimatedImage".equals( mediaType ) )
						{
							String img;
							String thumb = null;
							String videoUri = null;
							String caption = galleryItem.optString( "caption", null );
							String outbound_url = galleryItem.optString( "outbound_url", null );
							String description = null;
							
							if ( null != caption && null != outbound_url )
							{
								description = caption + '\n' + outbound_url;
							}
							else if ( null != caption )
							{
								description = caption;
							}
							else if ( null != outbound_url )
							{
								description = outbound_url;
							}
							
							if ( "AnimatedImage".equals( mediaType ) )
							{
								if (  mediaObj.getJSONObject( "s" ).has( "gif" ) )
								{
									img = mediaObj.getJSONObject( "s" ).getString( "gif" ).toString();
								}
								else
								{
									JSONArray jsonArray = mediaObj.getJSONArray( "p" );
									img = jsonArray.getJSONObject( jsonArray.length() - 1 ).getString( "u" ).toString();
								}
								
								videoUri = mediaObj.getJSONObject( "s" ).getString( "mp4" ).toString();
							}
							else
							{
								img = mediaObj.getJSONObject( "s" ).getString( "u" ).toString();
							}
							
							if ( 0 != mediaObj.getJSONArray( "p" ).length() )
							{
								thumb = mediaObj.getJSONArray( "p" ).getJSONObject( 0 ).getString( "u" ).toString();
							}
							
							images[ i ] = new ImgurImage( mediaId, img, null != thumb ? Uri.parse( thumb ) : Uri.EMPTY, null != videoUri ? Uri.parse( videoUri ) : null, 0 == i && null != title && !title.isEmpty() ? title : "", description );
						}
					}
					
					new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumResolved( images ) );
				}
				catch ( Exception ignore )
				{
					new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				new Handler( Looper.getMainLooper() ).post( () -> albumSolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
			}
		} );
	}
	
	@Override
	public boolean isAlbum( Uri uri )
	{
		return ( "reddit.com".equals( uri.getHost() ) || "www.reddit.com".equals( uri.getHost() ) ) &&
			uri.getPathSegments().size() >= 2 && uri.getPathSegments().get( 0 ).equals( "gallery" );
	}
	
	@Override
	public void getPath( Uri uri, PathResolverListener pathResolverListener )
	{
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return isAlbum( uri );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		return true;
	}
}
