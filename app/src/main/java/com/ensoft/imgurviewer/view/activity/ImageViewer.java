package com.ensoft.imgurviewer.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.ImgurService;
import com.ensoft.imgurviewer.service.RequestQueueService;
import com.ensoft.imgurviewer.service.interfaces.ImgurPathResolverListener;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.fresco.ZoomableDraweeView;

import com.helpers.MetricsHelper;
import com.imgurviewer.R;

public class ImageViewer extends Activity
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

	private void loadImage( Uri uri )
	{
		Log.v( TAG, "Loading image: " + uri.toString() );

		new FrescoService().loadImage( uri, mImageView, new ControllerImageInfoListener()
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
		});
		mVideoView.start();
	}

	protected void loadResource( Uri uri )
	{
		final ImgurService imgurService = new ImgurService();

		if ( imgurService.isImgurPath( uri ) )
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
						loadImage( url );
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
		else
		{
			loadImage( uri );
		}
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
			Uri data = getIntent().getData();//set a variable for the Intent

			Log.v( TAG, "Data is: " + data.toString() );

			loadResource( data );
		}
		else
		{
			loadImage( Uri.parse( "https://media3.giphy.com/media/JLQUx1mbgv2hO/200.gif" ) );
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Fresco.shutDown();
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
		}
	};
	
	@SuppressLint( "InlinedApi" )
	private void show()
	{
		mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
		mVisible = true;
		mHideHandler.removeCallbacks( mHidePart2Runnable );
		mHideHandler.postDelayed( mShowPart2Runnable, UI_ANIMATION_DELAY );
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
