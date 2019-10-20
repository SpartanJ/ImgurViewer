package com.ensoft.imgurviewer.service.resource;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.FlickrAlbumImage;
import com.ensoft.imgurviewer.model.FlickrImage;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.StringUtils;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.listener.AlbumResolverListener;
import com.ensoft.imgurviewer.service.listener.PathResolverListener;
import com.ensoft.restafari.helper.ThreadMode;
import com.ensoft.restafari.network.processor.ResponseListener;
import com.ensoft.restafari.network.service.RequestService;
import com.ensoft.restafari.network.toolbox.UntrustedHurlStack;
import com.google.gson.Gson;
import com.imgurviewer.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FlickrService extends MediaServiceSolver
{
	public static final String TAG = FlickrService.class.getCanonicalName();
	protected static final String FLICKR_DOMAIN = "flickr.com";
	protected static final String FLICKR_SHORT_DOMAIN = "flic.kr";
	protected static final String FLICKR_API_CALL = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=%s&photo_id=%s&format=json&nojsoncallback=1";
	protected static final String FLICKR_SHORT_SOLVER = "https://www.flickr.com/photo.gne?short=";
	protected static RequestQueue sRequestQueue = null;
	
	protected void followRedirect( Uri uri, final PathResolverListener pathResolverListener )
	{
		if ( null == sRequestQueue )
		{
			HttpStack httpStack = new UntrustedHurlStack()
			{
				@Override
				protected HttpURLConnection createConnection( URL url) throws IOException
				{
					HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
					
					trustAllCertsForConnection( httpURLConnection );
					
					httpURLConnection.setInstanceFollowRedirects( false );
					
					return httpURLConnection;
				}
			};
			
			sRequestQueue = Volley.newRequestQueue( App.getInstance().getApplicationContext(), httpStack );
		}
		
		sRequestQueue.add( new StringRequest( Request.Method.GET, FLICKR_SHORT_SOLVER + uri.getLastPathSegment(), response -> {}, error -> {
			if ( error.networkResponse.statusCode == 301 || error.networkResponse.statusCode == 302 )
			{
				if ( error.networkResponse.headers.containsKey( "Location" ) )
				{
					getPath( Uri.parse( "https://" + FLICKR_DOMAIN + error.networkResponse.headers.get( "Location" ) ), pathResolverListener );
				}
				else
				{
					sendPathError( uri, pathResolverListener, App.getInstance().getString( R.string.unknown_error ) );
				}
			}
			else
			{
				sendPathError( uri, pathResolverListener, App.getInstance().getString( R.string.unknown_error ) );
			}
		} ) );
	}
	
	@Override
	public void getPath( Uri uri, final PathResolverListener pathResolverListener )
	{
		try
		{
			String id;
			
			if ( UriUtils.uriMatchesDomain( uri, FLICKR_SHORT_DOMAIN ) )
			{
				followRedirect( uri, pathResolverListener );
				return;
			}
			else
			{
				id = uri.getLastPathSegment();
				
				if ( !TextUtils.isDigitsOnly( id ) )
				{
					List<String> segments = uri.getPathSegments();
					
					if ( null != segments && segments.size() > 1 )
					{
						for ( int i = segments.size() - 1; i >= 0; i-- )
						{
							String str = segments.get( i );
							
							if ( TextUtils.isDigitsOnly( str ) )
							{
								id = str;
								break;
							}
						}
					}
				}
			}
			
			String url = String.format( FLICKR_API_CALL, App.getInstance().getString( R.string.flickr_key ), id );
			
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( url, null, response ->
			{
				try
				{
					FlickrImage flickrImage = new Gson().fromJson( response.toString(), FlickrImage.class );
					
					if ( "ok".equals( flickrImage.getStat() ) )
					{
						sendPathResolved( pathResolverListener, flickrImage.getUri(), MediaType.IMAGE, flickrImage.getThumbnailUri() );
					}
					else
					{
						sendPathError( uri, pathResolverListener, App.getInstance().getString( R.string.unknown_error ) );
					}
				}
				catch ( Exception e )
				{
					Log.v( TAG, e.getMessage() );
					
					sendPathError( uri, pathResolverListener, e.toString() );
				}
			}, error ->
			{
				Log.v( TAG, error.toString() );
				
				sendPathError( uri, pathResolverListener, error.toString() );
			} );
			
			RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
		}
		catch ( Exception e )
		{
			pathResolverListener.onPathError( uri, App.getInstance().getString( R.string.unknown_error ) );
		}
	}
	
	@Override
	public boolean isServicePath( Uri uri )
	{
		return UriUtils.uriMatchesDomain( uri, FLICKR_DOMAIN ) || UriUtils.uriMatchesDomain( uri, FLICKR_SHORT_DOMAIN );
	}
	
	@Override
	public boolean isGallery( Uri uri )
	{
		if ( UriUtils.uriMatchesDomain( uri, FLICKR_SHORT_DOMAIN ) )
		{
			return uri.getPathSegments().size() > 1 && uri.getPathSegments().get( uri.getPathSegments().size() - 2 ).equals( "s" );
		}
		else if ( uri.getPathSegments().size() > 3 )
		{
			String segmentName = uri.getPathSegments().get( uri.getPathSegments().size() - 2 );
			
			return "albums".equals( segmentName ) || "sets".equals( segmentName );
		}
		
		return false;
	}
	
	public void getGallery( Uri uri, final AlbumResolverListener albumResolverListener )
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
					String imagesList = StringUtils.getStringMatch( response, "\"photoPageList\":{\"_data\":", ",\"fetchedStart\":" );
					
					FlickrAlbumImage[] images = new Gson().fromJson( imagesList, FlickrAlbumImage[].class );
					
					if ( null != images && images.length > 0 )
					{
						ImgurImage[] imgurImages = new ImgurImage[ images.length ];
						
						int i = 0;
						
						for ( FlickrAlbumImage img : images )
						{
							imgurImages[i] = new ImgurImage( img.getImage(), img.getThumbnail(), img.getTitle(), img.getDescription() );
							
							imgurImages[i].setFullSizeLink( img.getFullSizeImage() );
							
							i++;
						}
						
						new Handler( Looper.getMainLooper() ).post( () -> albumResolverListener.onAlbumResolved( imgurImages ) );
					}
					else
					{
						new Handler( Looper.getMainLooper() ).post( () -> albumResolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
					}
				}
				catch ( Exception ignored )
				{
					new Handler( Looper.getMainLooper() ).post( () -> albumResolverListener.onAlbumError( context.getString( R.string.could_not_resolve_album_url ) ) );
				}
			}
			
			@Override
			public void onRequestError( Context context, int errorCode, String errorMessage )
			{
				if ( null != errorMessage )
					Log.v( FLICKR_DOMAIN, errorMessage );
				
				new Handler( Looper.getMainLooper() ).post( () -> albumResolverListener.onAlbumError( null != errorMessage ? errorMessage : "" ) );
			}
		} );
	}
}
