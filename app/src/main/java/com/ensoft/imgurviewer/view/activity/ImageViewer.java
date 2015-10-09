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
import com.ensoft.imgurviewer.service.ImgurService;
import com.ensoft.imgurviewer.service.interfaces.ImgurPathResolverListener;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.fresco.ZoomableDraweeView;

import com.helpers.MetricsHelper;
import com.imgurviewer.R;

public class ImageViewer extends AppActivity
{
	public static final String TAG = ImageViewer.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	private static final int UI_ANIMATION_DELAY = 300;

	private View mContentView;
	private ProgressBar mProgressBar;
	private ZoomableDraweeView mImageView;
	private VideoView mVideoView;
	private boolean mVisible;
	private PointF mPos = new PointF();
	private float mClickTolerance;
	private ImageView mSettingsButton;

	protected View.OnTouchListener mTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			switch ( event.getActionMasked() )
			{
				case MotionEvent.ACTION_DOWN:
				{
					mPos.x = event.getRawX();
					mPos.y = event.getRawY();
					break;
				}
				case MotionEvent.ACTION_UP:
				{
					if ( Math.abs( mPos.x - event.getRawX() ) <= mClickTolerance && Math.abs( mPos.y - event.getRawY() ) <= mClickTolerance )
					{
						toggle();
					}
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

		new FrescoService().loadImage( uri, thumbnail, mImageView, new ControllerImageInfoListener()
		{
			@Override
			public void onFinalImageSet( String id, ImageInfo imageInfo, Animatable animatable )
			{
				mProgressBar.setVisibility( View.INVISIBLE );
			}

			@Override
			public void onFailure( String id, Throwable throwable )
			{
				Toast.makeText( ImageViewer.this, throwable.getMessage(), Toast.LENGTH_SHORT ).show();
			}
		} );

		mImageView.setOnTouchListener( mTouchListener );
	}

	protected void loadVideo( Uri uri )
	{
		Log.v( TAG, "Loading video: " + uri.toString() );

		mImageView.setVisibility( View.GONE );
		mVideoView.setVisibility( View.VISIBLE );
		mVideoView.setOnClickListener( mClickListener );
		mVideoView.setVideoURI( uri );
		mVideoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared( MediaPlayer mp )
			{
				mp.setLooping( true );

				mProgressBar.setVisibility( View.INVISIBLE );
			}
		} );
		mVideoView.start();
	}

	protected void loadResource( Uri uri )
	{
		final ImgurService imgurService = new ImgurService();
		final ImgurAlbumService imgurAlbumService = new ImgurAlbumService();

		if ( imgurService.isImgurPath( uri ) )
		{
			if ( imgurAlbumService.isImgurAlbum( uri ) )
			{
				Intent intent = new Intent( ImageViewer.this, AlbumViewer.class );
				intent.putExtra( AlbumViewer.ALBUM_DATA, uri.toString() );
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

		mVisible = true;
		mClickTolerance = MetricsHelper.dpToPx( getBaseContext(), 50.f );
		mContentView = findViewById( R.id.fullscreen_content );
		mImageView = (ZoomableDraweeView)findViewById( R.id.imageView );
		mVideoView = (VideoView)findViewById( R.id.videoView );
		mProgressBar = (ProgressBar)findViewById( R.id.progressBar );
		mSettingsButton = (ImageView)findViewById( R.id.settings );
		mSettingsButton.setPadding( 0, MetricsHelper.getStatusBarHeight( this ) + MetricsHelper.dpToPx( this, 16 ), MetricsHelper.dpToPx( this, 16 ), 0 );

		ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
		progressBarDrawable.setColor( getResources().getColor( R.color.imgur_color ) );
		progressBarDrawable.setBarWidth( MetricsHelper.dpToPx( this, 4 ) );

		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
			.setActualImageScaleType( ScalingUtils.ScaleType.FIT_CENTER )
			.setProgressBarImage( progressBarDrawable )
			.build();

		mImageView.setHierarchy( hierarchy );

		mContentView.setOnClickListener( mClickListener );

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
			loadImage( Uri.parse( "http://imgurviewer.ensoft-dev.com/img/nyancat.gif" ), null );
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
		if ( mVisible )
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
		mVisible = false;
		mHideHandler.removeCallbacks( mShowPart2Runnable );
		mHideHandler.postDelayed( mHidePart2Runnable, UI_ANIMATION_DELAY );
	}
	
	private final Runnable mHidePart2Runnable = new Runnable()
	{
		@SuppressLint( "InlinedApi" )
		@Override
		public void run()
		{
			mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

			mSettingsButton.setVisibility( View.VISIBLE );
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
					mSettingsButton.setVisibility( View.INVISIBLE );
				}

				@Override
				public void onAnimationRepeat( Animation animation )
				{
				}
			} );

			mSettingsButton.startAnimation( alphaAnimation );
		}
	};
	
	@SuppressLint( "InlinedApi" )
	private void show()
	{
		mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
		mVisible = true;
		mHideHandler.removeCallbacks( mHidePart2Runnable );
		mHideHandler.postDelayed( mShowPart2Runnable, UI_ANIMATION_DELAY );

		mSettingsButton.setVisibility( View.VISIBLE );
		AlphaAnimation alphaAnimation = new AlphaAnimation( 0, 1 );
		alphaAnimation.setDuration( UI_ANIMATION_DELAY );
		mSettingsButton.startAnimation( alphaAnimation );
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
