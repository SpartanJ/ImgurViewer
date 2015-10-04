package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImgurService
{
	public static final String TAG = ImgurService.class.getCanonicalName();
	public static final String ALBUM_API_URL = "https://api.imgur.com/3/album/";

	public interface PathListener
	{
		void onPathObtained( Uri url );

		void onPathError( String error );
	}

	protected String getAlbumId( String url )
	{
		return url.substring( url.lastIndexOf( "/a/" ) + 3 );
	}

	protected void getFirstImage( final String url, final PathListener pathListener )
	{
		String albumUrl = ALBUM_API_URL + getAlbumId( url );

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( albumUrl, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					JSONObject data = response.getJSONObject( "data" );

					JSONArray images = data.getJSONArray( "images" );

					JSONObject image = images.getJSONObject( 0 );

					pathListener.onPathObtained( Uri.parse( image.getString( "link" ) ) );
				}
				catch ( JSONException e )
				{
					pathListener.onPathError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				pathListener.onPathError( error.toString() );
			}
		});

		RequestQueueService.getInstance().addToRequestQueue( jsonObjectRequest );
	}

	protected Uri processPath( Uri uri )
	{
		String url = uri.toString();

		if ( url.contains( "//imgur.com" ) )
		{
			url = url.replace( "//imgur.com", "//i.imgur.com" );
		}
		else if ( url.contains( "//www.imgur.com" ) )
		{
			url = url.replace( "//www.imgur.com", "//i.imgur.com" );
		}

		if ( url.endsWith( ".gif" ) || url.endsWith( ".gifv" ) )
		{
			url = url.replace( ".gifv", ".mp4" );
			url = url.replace( ".gif", ".mp4" );
		}

		if ( !url.endsWith( ".png" ) && !url.endsWith( ".jpg" ) && !url.endsWith( ".jpeg" ) && !url.endsWith( ".mp4" ) )
		{
			url += ".jpg";
		}

		return Uri.parse( url );
	}

	public void getPath( Uri uri, PathListener pathListener )
	{
		String url = uri.toString();

		if ( url.contains( "/a/" ) )
		{
			getFirstImage( url, pathListener );
		}
		else
		{
			pathListener.onPathObtained( processPath( uri ) );
		}
	}

	public void getPathUri( Uri uri, PathListener pathListener )
	{
		getPath( uri, pathListener );
	}

	public boolean isVideo( Uri uri )
	{
		return isVideo( uri.toString() );
	}

	public boolean isVideo( String uri )
	{
		return uri.endsWith( ".gifv" ) || uri.endsWith( ".mp4" ) || uri.endsWith( ".avi" ) || uri.endsWith( ".flv" ) || uri.endsWith( ".mkv" );
	}
}
