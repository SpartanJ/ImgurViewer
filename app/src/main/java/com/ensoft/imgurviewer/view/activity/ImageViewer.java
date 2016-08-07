package com.ensoft.imgurviewer.view.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.ensoft.imgurviewer.service.DownloadService;
import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.IntentUtils;
import com.ensoft.imgurviewer.service.ResourceSolver;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.ensoft.imgurviewer.view.fragment.MediaPlayerFragment;
import com.ensoft.imgurviewer.view.helper.MetricsHelper;
import com.ensoft.imgurviewer.view.helper.ViewHelper;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.imgurviewer.R;

import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

public class ImageViewer extends AppActivity
{
	public static final String TAG = ImageViewer.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	private static final int UI_ANIMATION_DELAY = 300;

	private View contentView;
	private ProgressBar progressBar;
	private PhotoDraweeView imageView;
	private VideoView videoView;
	private boolean visible;
	private long lastClickTime;
	private LinearLayout floatingMenu;
	private Uri currentResource;

	protected OnViewTapListener touchListener = new OnViewTapListener()
	{
		@Override
		public void onViewTap(View view, float x, float y)
		{
			if ( !( System.currentTimeMillis() - lastClickTime <= UI_ANIMATION_DELAY ) )
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

			lastClickTime = System.currentTimeMillis();
		}
	};

	protected View.OnClickListener clickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			toggle();
		}
	};

	protected MediaPlayerFragment mediaPlayerFragment;

	protected void createMediaPlayer()
	{
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace( R.id.player, mediaPlayerFragment = new MediaPlayerFragment() );
		fragmentTransaction.commit();

		mediaPlayerFragment.setVideoView( videoView );
	}

	private void loadImage( Uri uri, Uri thumbnail )
	{
		currentResource = uri;

		Log.v( TAG, "Loading image: " + uri.toString() );

		new FrescoService().loadImage( uri, thumbnail, imageView, new ControllerImageInfoListener()
		{
			@Override
			public void onFinalImageSet( String id, ImageInfo imageInfo, Animatable animatable )
			{
				progressBar.setVisibility( View.INVISIBLE );

				imageView.update( imageInfo.getWidth(), imageInfo.getHeight() );
			}

			@Override
			public void onFailure( String id, Throwable throwable )
			{
				Toast.makeText( ImageViewer.this, throwable.getMessage(), Toast.LENGTH_SHORT ).show();
			}
		} );

		imageView.setOnViewTapListener( touchListener );

		delayedHide( 100 );
	}

	protected void loadVideo( Uri uri )
	{
		currentResource = uri;

		Log.v( TAG, "Loading video: " + uri.toString() );

		imageView.setVisibility( View.GONE );
		videoView.setVisibility( View.VISIBLE );
		videoView.setOnClickListener( clickListener );
		videoView.setVideoURI( uri );
		videoView.start();

		createMediaPlayer();

		mediaPlayerFragment.setOnPreparedListener( new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared( MediaPlayer mp )
			{
				mp.setLooping( true );

				progressBar.setVisibility( View.INVISIBLE );
			}
		} );

		delayedHide( 100 );
	}

	protected void loadResource( Uri uri )
	{
		new ResourceSolver( new ResourceLoadListener()
		{
			@Override
			public void loadVideo( Uri uri )
			{
				ImageViewer.this.loadVideo( uri );
			}

			@Override
			public void loadImage( Uri uri, Uri thumbnail )
			{
				ImageViewer.this.loadImage( uri, thumbnail );
			}

			@Override
			public void loadAlbum( Uri uri, Class<?> view )
			{
				Intent intent = new Intent( ImageViewer.this, view );
				intent.putExtra( AppActivity.ALBUM_DATA, uri.toString() );
				startActivity( intent );
				finish();
			}
		} ).solve( uri );
	}

	public void showSettings( View v )
	{
		startActivity( new Intent( ImageViewer.this, SettingsView.class ) );
	}

	public void downloadImage( View v )
	{
		if ( null != currentResource )
		{
			new DownloadService( this ).download( currentResource, URLUtil.guessFileName( currentResource.toString(), null, null ) );
		}
	}

	public void shareImage( View v )
	{
		if ( currentResource != null )
		{
			IntentUtils.shareMessage( this, getString( R.string.share ), currentResource.toString(), getString( R.string.shareUsing ) );
		}
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_imageviewer );

		visible = true;
		contentView = findViewById( R.id.fullscreen_content );
		imageView = (PhotoDraweeView)findViewById( R.id.imageView );
		videoView = (VideoView)findViewById( R.id.videoView );
		progressBar = (ProgressBar)findViewById( R.id.progressBar );
		floatingMenu = (LinearLayout)findViewById( R.id.floating_menu );

		if ( null != getResources() && null != getResources().getConfiguration() )
		{
			setFloatingMenuOrientation( getResources().getConfiguration().orientation );
		}

		ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
		progressBarDrawable.setColor( getResources().getColor( R.color.imgur_color ) );
		progressBarDrawable.setBarWidth( MetricsHelper.dpToPx( this, 4 ) );

		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
			.setActualImageScaleType( ScalingUtils.ScaleType.FIT_CENTER )
			.setProgressBarImage( progressBarDrawable )
			.build();

		imageView.setHierarchy( hierarchy );

		contentView.setOnClickListener( clickListener );

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
			TutorialActivity.newInstance( this );
			finish();
		}
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
		hideHandler.postDelayed( hidePart2Runnable, UI_ANIMATION_DELAY );
	}
	
	private final Runnable hidePart2Runnable = new Runnable()
	{
		@SuppressLint( "InlinedApi" )
		@Override
		public void run()
		{
			contentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

			floatingMenu.setVisibility( View.VISIBLE );

			if ( null != mediaPlayerFragment )
			{
				mediaPlayerFragment.setVisibility( View.VISIBLE );
			}

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
					floatingMenu.setVisibility( View.INVISIBLE );

					if ( null != mediaPlayerFragment )
					{
						mediaPlayerFragment.setVisibility( View.INVISIBLE );
					}
				}

				@Override
				public void onAnimationRepeat( Animation animation )
				{
				}
			} );

			floatingMenu.startAnimation( alphaAnimation );

			if ( null != mediaPlayerFragment )
			{
				mediaPlayerFragment.startAnimation( alphaAnimation );
			}
		}
	};
	
	@SuppressLint( "InlinedApi" )
	private void show()
	{
		contentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
		visible = true;
		hideHandler.removeCallbacks( hidePart2Runnable );

		floatingMenu.setVisibility( View.VISIBLE );
		AlphaAnimation alphaAnimation = new AlphaAnimation( 0, 1 );
		alphaAnimation.setDuration( UI_ANIMATION_DELAY );
		floatingMenu.startAnimation( alphaAnimation );

		if ( null != mediaPlayerFragment )
		{
			mediaPlayerFragment.setVisibility( View.VISIBLE );
			mediaPlayerFragment.startAnimation( alphaAnimation );
		}
	}

	private final Handler hideHandler = new Handler();
	private final Runnable hideRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			hide();
		}
	};

	private void delayedHide( int delayMillis )
	{
		hideHandler.removeCallbacks( hideRunnable );
		hideHandler.postDelayed( hideRunnable, delayMillis );
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged( newConfig );

		if ( null != mediaPlayerFragment )
		{
			mediaPlayerFragment.setOrientationMargins( newConfig.orientation );
		}

		setFloatingMenuOrientation( newConfig.orientation );
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
