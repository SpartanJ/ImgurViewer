package com.ensoft.imgurviewer.view.activity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.DeviceService;
import com.ensoft.imgurviewer.service.ImgurService;
import com.ensoft.imgurviewer.service.ImgurAlbumService;
import com.ensoft.imgurviewer.service.ImgurGalleryService;
import com.ensoft.imgurviewer.service.interfaces.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.interfaces.ImgurGalleryResolverListener;
import com.ensoft.imgurviewer.view.adapter.AlbumAdapter;
import com.imgurviewer.R;

public class AlbumGalleryViewer extends AppActivity
{
	public static final String TAG = AlbumGalleryViewer.class.getCanonicalName();
	public static final String ALBUM_DATA = "albumData";

	protected AlbumAdapter albumAdapter;
	protected ProgressBar progressBar;
	protected RecyclerView recyclerView;

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

		progressBar = (ProgressBar)findViewById( R.id.albumViewer_progressBar );

		if ( new ImgurAlbumService().isImgurAlbum( albumData ) )
		{
			new ImgurAlbumService().getAlbum( albumData, new ImgurAlbumResolverListener()
			{
				@Override
				public void onAlbumResolved( ImgurAlbum album )
				{
					create( album.getImages() );
				}

				@Override
				public void onError( String error )
				{
					Toast.makeText( AlbumGalleryViewer.this, error, Toast.LENGTH_SHORT ).show();
				}
			} );
		}
		else if ( new ImgurGalleryService().isImgurGallery( albumData ) )
		{
			new ImgurGalleryService().getGallery( albumData, new ImgurGalleryResolverListener()
			{
				@Override
				public void onAlbumResolved( ImgurAlbum album )
				{
					create( album.getImages() );
				}

				@Override
				public void onImageResolved( ImgurImage image )
				{
					ImgurImage[] images = new ImgurImage[1];
					images[0] = image;
					create( images );
				}

				@Override
				public void onError( String error )
				{
					Toast.makeText( AlbumGalleryViewer.this, error, Toast.LENGTH_SHORT ).show();
				}
			} );
		}
		else if ( new ImgurService().isMultiImageUri( albumData ) )
		{
			create( new ImgurService().getImagesFromMultiImageUri( albumData ) );
		}
	}

	protected void create( ImgurImage[] images )
	{
		progressBar.setVisibility( View.INVISIBLE );
		albumAdapter = new AlbumAdapter( R.layout.item_album_photo, images );
		albumAdapter.setOrientationLandscape( new DeviceService().isLandscapeOrientation( this ) );

		recyclerView = (RecyclerView) findViewById( R.id.albumViewer_listView );
		recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
		recyclerView.setAdapter( albumAdapter );
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged( newConfig );

		if ( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			albumAdapter.setOrientationLandscape( true );
			albumAdapter.notifyDataSetChanged();
		}
		else if ( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT )
		{
			albumAdapter.setOrientationLandscape( false );
			albumAdapter.notifyDataSetChanged();
		}
	}
}
