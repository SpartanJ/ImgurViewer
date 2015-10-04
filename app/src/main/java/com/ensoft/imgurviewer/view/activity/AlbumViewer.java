package com.ensoft.imgurviewer.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.service.ImgurAlbumService;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.view.adapter.AlbumAdapter;
import com.imgurviewer.R;

public class AlbumViewer extends AppActivity
{
	public static final String TAG = AlbumViewer.class.getCanonicalName();
	public static final String ALBUM_DATA = "albumData";

	protected ImgurAlbum mAlbum;
	protected AlbumAdapter mAlbumAdapter;
	protected RecyclerView mRecyclerView;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_albumviewer );

		Uri albumData = null;

		if ( null != getIntent().getExtras() && null != getIntent().getExtras().getString( ALBUM_DATA ) )
		{
			albumData = Uri.parse( getIntent().getExtras().getString( ALBUM_DATA ) );
		}
		else if ( getIntent().getData() != null )
		{
			albumData = getIntent().getData();
		}

		if ( null == albumData )
		{
			finish();

			Log.v( TAG, "Data not found." );

			return;
		}

		Log.v( TAG, "Data is: " + albumData.toString() );

		new ImgurAlbumService().getAlbum( albumData, new ImgurAlbumResolverListener()
		{
			@Override
			public void onAlbumResolved( ImgurAlbum album )
			{
				create( album );
			}

			@Override
			public void onError( String error )
			{
				Toast.makeText( AlbumViewer.this, error, Toast.LENGTH_SHORT ).show();
			}
		} );
	}

	protected void create( ImgurAlbum album )
	{
		mAlbum = album;

		mAlbumAdapter = new AlbumAdapter( R.layout.item_album_photo, mAlbum.getImages() );

		mRecyclerView = (RecyclerView) findViewById( R.id.albumViewer_listView );
		mRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );
		mRecyclerView.setAdapter( mAlbumAdapter );
	}
}
