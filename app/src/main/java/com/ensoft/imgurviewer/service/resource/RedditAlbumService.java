package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.listener.AlbumProvider;
import com.ensoft.imgurviewer.service.listener.AlbumSolverListener;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.imgurviewer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RedditAlbumService implements AlbumProvider
{
	public static final String TAG = RedditAlbumService.class.getCanonicalName();
	
	@Override
	public void getAlbum( Uri uri, AlbumSolverListener albumSolverListener )
	{
		String url = "https://www.reddit.com/" + uri.getPathSegments().get( 1 ) + ".json";
		
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
					
					JSONArray items = data.getJSONObject( "gallery_data" ).getJSONArray( "items" );
					ArrayList<String> mediaIds = new ArrayList<>(  );
					
					for ( int i = 0; i < items.length(); i++ )
					{
						JSONObject item = items.getJSONObject( i );
						mediaIds.add( item.getString( "media_id" ) );
					}
					
					JSONObject mediaMetadata = data.getJSONObject( "media_metadata" );
					ImgurImage[] images = new ImgurImage[ mediaIds.size() ];
					int i = 0;
					for ( String mediaId : mediaIds )
					{
						JSONObject mediaObj = mediaMetadata.getJSONObject( mediaId );
						String img = Html.fromHtml( mediaObj.getJSONObject( "s" ).getString( "u" ) ).toString();
						String thumb = Html.fromHtml( mediaObj.getJSONArray( "p" ).getJSONObject( 0 ).getString( "u" ) ).toString();
						images[i] = new ImgurImage( mediaId, img, Uri.parse( thumb ), null, 0 == i && null != title && !title.isEmpty() ? title : "" );
						i++;
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
}
