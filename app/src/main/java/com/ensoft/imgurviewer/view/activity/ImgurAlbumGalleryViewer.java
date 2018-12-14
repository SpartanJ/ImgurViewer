package com.ensoft.imgurviewer.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.model.InstagramProfileModel;
import com.ensoft.imgurviewer.model.LayoutType;
import com.ensoft.imgurviewer.service.DeviceService;
import com.ensoft.imgurviewer.service.DownloadService;
import com.ensoft.imgurviewer.service.IntentUtils;
import com.ensoft.imgurviewer.service.PermissionService;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.TransparencyUtils;
import com.ensoft.imgurviewer.service.listener.ImgurAlbumResolverListener;
import com.ensoft.imgurviewer.service.listener.ImgurGalleryResolverListener;
import com.ensoft.imgurviewer.service.listener.InstagramProfileResolverListener;
import com.ensoft.imgurviewer.service.resource.ImgurAlbumService;
import com.ensoft.imgurviewer.service.resource.ImgurGalleryService;
import com.ensoft.imgurviewer.service.resource.ImgurService;
import com.ensoft.imgurviewer.service.resource.InstagramProfileService;
import com.ensoft.imgurviewer.service.resource.InstagramService;
import com.ensoft.imgurviewer.view.adapter.ImgurAlbumAdapter;
import com.ensoft.imgurviewer.view.helper.MetricsHelper;
import com.ensoft.imgurviewer.view.helper.SlidrPositionHelper;
import com.ensoft.imgurviewer.view.helper.ViewHelper;
import com.imgurviewer.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;

public class ImgurAlbumGalleryViewer extends AppActivity
{
	public static final String TAG = ImgurAlbumGalleryViewer.class.getCanonicalName();
	
	protected RelativeLayout albumContainer;
	protected LinearLayout floatingMenu;
	protected ImageView layoutTypeView;
	protected ImgurAlbumAdapter albumAdapter;
	protected ProgressBar progressBar;
	protected RecyclerView recyclerView;
	protected Uri albumData;
	protected ImgurImage[] images;
	protected LayoutType currentLayoutType;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_albumviewer );
		
		progressBar = findViewById( R.id.albumViewer_progressBar );
		albumContainer = findViewById( R.id.albumViewer_container );
		floatingMenu = findViewById( R.id.floating_menu );
		recyclerView = findViewById( R.id.albumViewer_listView );
		layoutTypeView = findViewById( R.id.view_type );
		findViewById( R.id.settings ).setOnClickListener( this::showSettings );
		findViewById( R.id.download ).setOnClickListener( this::downloadImage );
		findViewById( R.id.share ).setOnClickListener( this::shareImage );
		
		currentLayoutType = App.getInstance().getPreferencesService().getDefaultGalleryLayoutType();
		setLayoutType( currentLayoutType );
		layoutTypeView.setVisibility( View.VISIBLE );
		layoutTypeView.setOnClickListener( this::onLayoutTypeClick );
		
		if ( null != getResources() && null != getResources().getConfiguration() )
		{
			setFloatingMenuOrientation( getResources().getConfiguration().orientation );
		}
		
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
		}
	}
	
	@Override
	public void onPostCreate( @Nullable Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		
		statusBarTint();
		
		if ( App.getInstance().getPreferencesService().getDisableWindowTransparency() )
			TransparencyUtils.convertActivityFromTranslucent( this );
		
		Log.v( TAG, "Data is: " + albumData.toString() );
		
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
					Toast.makeText( ImgurAlbumGalleryViewer.this, error, Toast.LENGTH_SHORT ).show();
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
					ImgurImage[] images = new ImgurImage[ 1 ];
					images[ 0 ] = image;
					create( images );
				}
				
				@Override
				public void onError( String error )
				{
					Toast.makeText( ImgurAlbumGalleryViewer.this, error, Toast.LENGTH_SHORT ).show();
				}
			} );
		}
		else if ( new ImgurService().isMultiImageUri( albumData ) )
		{
			create( new ImgurService().getImagesFromMultiImageUri( albumData ) );
		}
		else if ( new InstagramService().isInstagramProfile( albumData ) )
		{
			loadInstagramProfile();
		}
	}
	
	protected void loadInstagramProfile()
	{
		new InstagramProfileService().getProfile( albumData, new InstagramProfileResolverListener()
		{
			@Override
			public void onProfileResolved( InstagramProfileModel profile )
			{
				if ( profile.hasItems() )
				{
					ImgurImage[] images = profile.getImages();
					create( images );
				}
			}
			
			@Override
			public void onError( String error )
			{
				Toast.makeText( ImgurAlbumGalleryViewer.this, error, Toast.LENGTH_SHORT ).show();
			}
		} );
	}
	
	protected void createAdapter()
	{
		boolean isGridLayout = currentLayoutType == LayoutType.GRID;
		int layoutRows = App.getInstance().getPreferencesService().getGridLayoutColumns();
		
		progressBar.setVisibility( View.INVISIBLE );
		
		if ( isGridLayout )
		{
			recyclerView.setLayoutManager( new GridLayoutManager( this, layoutRows ) );
		}
		else
		{
			recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
		}
		
		Point viewSize = isGridLayout ?
			new Point( recyclerView.getMeasuredWidth() / layoutRows, recyclerView.getMeasuredWidth() / layoutRows ) :
			new Point( recyclerView.getMeasuredWidth(), (int)( recyclerView.getMeasuredWidth() * 0.89f ) );
		
		albumAdapter = new ImgurAlbumAdapter( R.layout.item_album_photo, isGridLayout, layoutRows, images, floatingMenu.getMeasuredHeight(), viewSize );
		albumAdapter.setOrientationLandscape( new DeviceService().isLandscapeOrientation( this ) );
		
		recyclerView.setAdapter( albumAdapter );
		recyclerView.setNestedScrollingEnabled( false );
	}
	
	protected void create( ImgurImage[] images )
	{
		if ( null != images && images.length == 1 && TextUtils.isEmpty( images[0].getTitle() ) && TextUtils.isEmpty( images[0].getDescription() ) )
		{
			Intent intent = new Intent( this, ImageViewer.class );
			intent.putExtra( ImageViewer.PARAM_RESOURCE_PATH, images[0].getLink() );
			startActivity( intent );
			finish();
			return;
		}
		
		if ( null == albumAdapter )
		{
			this.images = images;
			
			createAdapter();
		}
		else if ( null != images )
		{
			if ( null != this.images && this.images.length > 0 && images.length > 0 && this.images[0].getLink().equals( images[0].getLink() ) )
			{
				// TODO: Find why this happens
				return;
			}
			else if ( this.images == null )
			{
				this.images = images;
			}
			else
			{
				ImgurImage[] newImages = new ImgurImage[ this.images.length + images.length ];
				
				int i = 0;
				
				for ( ImgurImage img : this.images )
				{
					newImages[ i ] = img;
					i++;
				}
				
				for ( ImgurImage img : images )
				{
					newImages[ i ] = img;
					i++;
				}
				
				this.images = newImages;
			}
			
			albumAdapter.appendImages( images );
		}
		
		PreferencesService preferencesService = App.getInstance().getPreferencesService();
		
		if ( preferencesService.gesturesEnabled() )
		{
			Slidr.attach( this, new SlidrConfig.Builder().listener( new SlidrListener()
			{
				@Override
				public void onSlideStateChanged( int state ) {}
				
				@Override
				public void onSlideChange( float percent )
				{
					albumContainer.setBackgroundColor( (int) ( percent * 255.0f + 0.5f ) << 24 );
				}
				
				@Override
				public void onSlideOpened() {}
				
				@Override
				public boolean onSlideClosed() { return false; }
			} ).position( SlidrPositionHelper.fromString( preferencesService.getGesturesGalleryView() ) ).build() );
		}
	}
	
	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		
		setFloatingMenuOrientation( newConfig.orientation );
		
		if ( null == albumAdapter )
		{
			return;
		}
		
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
	
	public void showSettings( View v )
	{
		startActivity( new Intent( this, SettingsActivity.class ) );
	}
	
	private void setLayoutType( LayoutType layoutType )
	{
		if ( layoutType == LayoutType.LIST )
		{
			layoutTypeView.setImageResource( R.drawable.ic_view_comfy_white_24dp );
		}
		else
		{
			layoutTypeView.setImageResource( R.drawable.ic_view_headline_white_24dp );
		}
		
		currentLayoutType = layoutType;
	}
	
	public void onLayoutTypeClick( View v )
	{
		setLayoutType( LayoutType.GRID == currentLayoutType ? LayoutType.LIST : LayoutType.GRID );
		
		createAdapter();
	}
	
	@Override
	public void onRequestPermissionsResult( final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults )
	{
		super.onRequestPermissionsResult( requestCode, permissions, grantResults );
		
		if ( requestCode == PermissionService.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION )
		{
			download();
		}
	}
	
	public void download()
	{
		if ( null != images )
		{
			for ( ImgurImage image : images )
			{
				new DownloadService( this ).download( image.getFullImageLinkUri(), URLUtil.guessFileName( image.getLink(), null, null ) );
			}
		}
	}
	
	public void downloadImage( View v )
	{
		if ( !new PermissionService().askExternalStorageAccess( this ) )
		{
			download();
		}
	}
	
	public void shareImage( View v )
	{
		if ( albumData != null )
		{
			IntentUtils.shareMessage( this, getString( R.string.share ), albumData.toString(), getString( R.string.shareUsing ) );
		}
	}
	
	protected void setFloatingMenuOrientation( int orientation )
	{
		if ( null != floatingMenu )
		{
			if ( orientation == Configuration.ORIENTATION_PORTRAIT )
			{
				floatingMenu.setPadding( 0, MetricsHelper.dpToPx( this, 8 ), 0, 0 );
				ViewHelper.setMargins( floatingMenu, 0, MetricsHelper.getStatusBarHeight( this ), 0, 0 );
			}
			else if ( orientation == Configuration.ORIENTATION_LANDSCAPE )
			{
				floatingMenu.setPadding( 0, MetricsHelper.dpToPx( this, 8 ), 0, 0 );
				ViewHelper.setMargins( floatingMenu, 0, MetricsHelper.getStatusBarHeight( this ), MetricsHelper.getNavigationBarWidth( this ), 0 );
			}
		}
	}
}
