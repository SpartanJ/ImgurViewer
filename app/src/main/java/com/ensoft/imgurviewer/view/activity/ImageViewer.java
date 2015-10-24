package com.ensoft.imgurviewer.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.ImgurAlbumService;
import com.ensoft.imgurviewer.service.ImgurGalleryService;
import com.ensoft.imgurviewer.service.ImgurService;
import com.ensoft.imgurviewer.service.interfaces.ImgurPathResolverListener;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.fresco.DefaultZoomableController;
import com.fresco.ZoomableDraweeView;

import com.helpers.MetricsHelper;
import com.imgurviewer.R;

public class ImageViewer extends AppActivity
{
	public static final String TAG = ImageViewer.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	private static final int UI_ANIMATION_DELAY = 300;

	private View contentView;
	private ProgressBar progressBar;
	private ZoomableDraweeView imageView;
	private VideoView videoView;
	private boolean visible;
	private PointF touchPos = new PointF();
	private float clickTolerance;
	private long lastClickTime;
	private ImageView settingsButton;

	protected View.OnTouchListener mTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			switch ( event.getActionMasked() )
			{
				case MotionEvent.ACTION_DOWN:
				{
					touchPos.x = event.getRawX();
					touchPos.y = event.getRawY();
					break;
				}
				case MotionEvent.ACTION_UP:
				{
					if ( Math.abs( touchPos.x - event.getRawX() ) <= clickTolerance && Math.abs( touchPos.y - event.getRawY() ) <= clickTolerance )
					{
						if ( System.currentTimeMillis() - lastClickTime <= UI_ANIMATION_DELAY )
						{
							if ( imageView.getZoomableController() instanceof DefaultZoomableController )
							{
								DefaultZoomableController zoomableController = (DefaultZoomableController) imageView.getZoomableController();

								if ( zoomableController.getScaleFactor() == 1.f )
								{
									zoomableController.animZoom( zoomableController.getScaleFactor(), 2.f, 150 );
								}
								else
								{
									zoomableController.animZoom( zoomableController.getScaleFactor(), 1.f, 150 );
								}
							}
						}
						else
						{
							new Handler().postDelayed( new Runnable()
							{
								@Override
								public void run()
								{
									long diff = System.currentTimeMillis() - lastClickTime;

									if ( diff >= UI_ANIMATION_DELAY )
									{
										toggle();
									}
								}
							}, UI_ANIMATION_DELAY );
						}
					}

					lastClickTime = System.currentTimeMillis();

					break;
				}
			}

			return false;
		}
	};

	protected View.OnClickListener mClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			toggle();
		}
	};

	private void loadImage( Uri uri, Uri thumbnail )
	{
		Log.v( TAG, "Loading image: " + uri.toString() );

		new FrescoService().loadImage( uri, thumbnail, imageView, new ControllerImageInfoListener()
		{
			@Override
			public void onFinalImageSet( String id, ImageInfo imageInfo, Animatable animatable )
			{
				progressBar.setVisibility( View.INVISIBLE );
			}

			@Override
			public void onFailure( String id, Throwable throwable )
			{
				Toast.makeText( ImageViewer.this, throwable.getMessage(), Toast.LENGTH_SHORT ).show();
			}
		} );

		imageView.setOnTouchListener( mTouchListener );
	}

	protected void loadVideo( Uri uri )
	{
		Log.v( TAG, "Loading video: " + uri.toString() );

		imageView.setVisibility( View.GONE );
		videoView.setVisibility( View.VISIBLE );
		videoView.setOnClickListener( mClickListener );
		videoView.setVideoURI( uri );
		videoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared( MediaPlayer mp )
			{
				mp.setLooping( true );

				progressBar.setVisibility( View.INVISIBLE );
			}
		} );
		videoView.start();
	}

	protected void loadResource( Uri uri )
	{
		final ImgurService imgurService = new ImgurService();
		final ImgurAlbumService imgurAlbumService = new ImgurAlbumService();
		final ImgurGalleryService imgurGalleryService = new ImgurGalleryService();

		if ( imgurService.isImgurPath( uri ) )
		{
			if ( imgurAlbumService.isImgurAlbum( uri ) || imgurGalleryService.isImgurGallery( uri ) || imgurService.isMultiImageUri( uri ) )
			{
				Intent intent = new Intent( ImageViewer.this, AlbumGalleryViewer.class );
				intent.putExtra( AlbumGalleryViewer.ALBUM_DATA, uri.toString() );
				startActivity( intent );
				finish();
			}
			else
			{
				imgurService.getPathUri( uri, new ImgurPathResolverListener()
				{
					@Override
					public void onPathResolved( Uri url, Uri thumbnail )
					{
						if ( imgurService.isVideo( url ) )
						{
							loadVideo( url );
						}
						else
						{
							loadImage( url, thumbnail );
						}
					}

					@Override
					public void onPathError( String error )
					{
						Log.v( TAG, error );
						Toast.makeText( ImageViewer.this, error, Toast.LENGTH_SHORT ).show();
					}
				} );
			}
		}
		else
		{
			loadImage( uri, null );
		}
	}

	public void showPopup(View v)
	{
		startActivity( new Intent( ImageViewer.this, SettingsView.class ) );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_imageviewer );

		visible = true;
		clickTolerance = MetricsHelper.dpToPx( getBaseContext(), 50.f );
		contentView = findViewById( R.id.fullscreen_content );
		imageView = (ZoomableDraweeView)findViewById( R.id.imageView );
		videoView = (VideoView)findViewById( R.id.videoView );
		progressBar = (ProgressBar)findViewById( R.id.progressBar );
		settingsButton = (ImageView)findViewById( R.id.settings );
		settingsButton.setPadding( 0, MetricsHelper.getStatusBarHeight( this ) + MetricsHelper.dpToPx( this, 16 ), MetricsHelper.dpToPx( this, 16 ), 0 );

		ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
		progressBarDrawable.setColor( getResources().getColor( R.color.imgur_color ) );
		progressBarDrawable.setBarWidth( MetricsHelper.dpToPx( this, 4 ) );

		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
			.setActualImageScaleType( ScalingUtils.ScaleType.FIT_CENTER )
			.setProgressBarImage( progressBarDrawable )
			.build();

		imageView.setHierarchy( hierarchy );

		contentView.setOnClickListener( mClickListener );

		if ( null != getIntent().getExtras() && null != getIntent().getExtras().getString( PARAM_RESOURCE_PATH ) )
		{
			loadResource( Uri.parse( getIntent().getExtras().getString( PARAM_RESOURCE_PATH ) ) );
		}
		else if ( getIntent().getData() != null )
		{
			Uri data = getIntent().getData();

			Log.v( TAG, "Data is: " + data.toString() );

			loadResource( data );
		}
		else
		{
			//loadImage( Uri.parse( "http://imgurviewer.ensoft-dev.com/img/nyancat.gif" ), null );
			//loadResource( Uri.parse( "http://imgur.com/gallery/xite5zY" ) );
			loadResource( Uri.parse( "http://imgur.com/cP3Q3eV,lJ8GISh,HpgzAs6,NOn7Jhn,Xy5KW9h,Xa23ZyF,udjV0dY" ) );
		}
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );

		delayedHide( 100 );
	}

	private void toggle()
	{
		if ( visible )
		{
			hide();
		}
		else
		{
			show();
		}
	}
	
	private void hide()
	{
		visible = false;
		mHideHandler.removeCallbacks( mShowPart2Runnable );
		mHideHandler.postDelayed( mHidePart2Runnable, UI_ANIMATION_DELAY );
	}
	
	private final Runnable mHidePart2Runnable = new Runnable()
	{
		@SuppressLint( "InlinedApi" )
		@Override
		public void run()
		{
			contentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

			settingsButton.setVisibility( View.VISIBLE );
			AlphaAnimation alphaAnimation = new AlphaAnimation( 1, 0 );
			alphaAnimation.setDuration( UI_ANIMATION_DELAY );
			alphaAnimation.setAnimationListener( new Animation.AnimationListener()
			{
				@Override
				public void onAnimationStart( Animation animation )
				{
				}

				@Override
				public void onAnimationEnd( Animation animation )
				{
					settingsButton.setVisibility( View.INVISIBLE );
				}

				@Override
				public void onAnimationRepeat( Animation animation )
				{
				}
			} );

			settingsButton.startAnimation( alphaAnimation );
		}
	};
	
	@SuppressLint( "InlinedApi" )
	private void show()
	{
		contentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
		visible = true;
		mHideHandler.removeCallbacks( mHidePart2Runnable );
		mHideHandler.postDelayed( mShowPart2Runnable, UI_ANIMATION_DELAY );

		settingsButton.setVisibility( View.VISIBLE );
		AlphaAnimation alphaAnimation = new AlphaAnimation( 0, 1 );
		alphaAnimation.setDuration( UI_ANIMATION_DELAY );
		settingsButton.startAnimation( alphaAnimation );
	}
	
	private final Runnable mShowPart2Runnable = new Runnable()
	{
		@Override
		public void run()
		{
		}
	};
	
	private final Handler mHideHandler = new Handler();
	private final Runnable mHideRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			hide();
		}
	};

	private void delayedHide( int delayMillis )
	{
		mHideHandler.removeCallbacks( mHideRunnable );
		mHideHandler.postDelayed( mHideRunnable, delayMillis );
	}
}
