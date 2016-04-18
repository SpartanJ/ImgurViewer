package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.listener.ImgurGalleryResolverListener;
import com.ensoft.restafari.network.service.RequestService;
import com.google.gson.Gson;
import com.imgurviewer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgurGalleryService
{
	public static final String TAG = ImgurAlbumService.class.getCanonicalName();
	public static final String IMGUR_GALLERY_API_URL = ImgurService.IMGUR_API_URL + "/gallery/";

	public String getGalleryId( Uri uri )
	{
		return getGalleryId( uri.toString() );
	}

	public String getGalleryId( String uri )
	{
		String endPart = null;

		if ( uri.contains( "/gallery/" ) )
		{
			endPart = uri.substring( uri.lastIndexOf( "/gallery/" ) + 9 );
		}

		if ( null != endPart )
		{
			int slash = endPart.indexOf( "/" );

			if ( -1 != slash )
			{
				endPart = endPart.substring( 0, slash );
			}
		}

		return endPart;
	}

	public boolean isImgurGallery( Uri uri )
	{
		return new ImgurService().isServicePath( uri ) && (
			uri.toString().contains( "/a/" ) ||
				uri.toString().contains( "/gallery/" )
		);
	}

	public void getGallery( Uri uri, final ImgurGalleryResolverListener imgurGalleryResolverListener )
	{
		String albumUrl = IMGUR_GALLERY_API_URL + getGalleryId( uri );

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( albumUrl, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.v( TAG, response.toString() );

					JSONObject data = response.getJSONObject( "data" );

					try
					{
						boolean isAlbum = data.getBoolean( "is_album" );

						if ( isAlbum )
						{
							ImgurAlbum album = new Gson().fromJson( data.toString(), ImgurAlbum.class );

							imgurGalleryResolverListener.onAlbumResolved( album );
						}
						else
						{
							ImgurImage image = new Gson().fromJson( data.toString(), ImgurImage.class );

							imgurGalleryResolverListener.onImageResolved( image );
						}
					}
					catch ( JSONException e )
					{
						Log.e( TAG, e.getMessage() );
					}
				}
				catch ( JSONException e )
				{
					Log.v( TAG, e.getMessage() );

					imgurGalleryResolverListener.onError( e.toString() );
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.v( TAG, error.toString() );

				imgurGalleryResolverListener.onError( error.toString() );
			}
		})
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				Map<String, String>  params = new HashMap<>();
				params.put("Authorization", "Client-ID " + App.getInstance().getString( R.string.imgur_client_id ) );
				return params;
			}
		};

		RequestService.getInstance().addToRequestQueue( jsonObjectRequest );
	}
}
