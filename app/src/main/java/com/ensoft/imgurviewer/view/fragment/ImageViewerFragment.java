package com.ensoft.imgurviewer.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ensoft.imgurviewer.service.PermissionService;
import com.ensoft.imgurviewer.service.ResourceSolver;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.ensoft.imgurviewer.view.activity.AppActivity;
import com.ensoft.imgurviewer.view.activity.SettingsView;
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

public class ImageViewerFragment extends Fragment
{
	public static final String TAG = ImageViewerFragment.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	private static final int UI_ANIMATION_DELAY = 300;
	
	private Context context;
	private View contentView;
	private ProgressBar progressBar;
	private PhotoDraweeView imageView;
	private VideoView videoView;
	private boolean visible;
	private long lastClickTime;
	private LinearLayout floatingMenu;
	private Uri currentResource;
	
	public static ImageViewerFragment newInstance( String resource )
	{
		ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
		Bundle args = new Bundle();
		args.putString( PARAM_RESOURCE_PATH, resource );
		imageViewerFragment.setArguments( args );
		return imageViewerFragment;
	}
	
	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.fragment_imageviewer, container, false );
	}
	
	@Override
	public void onViewCreated( View view, @Nullable Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );
		
		context = getActivity();
		visible = true;
		contentView = view.findViewById( R.id.fullscreen_content );
		imageView = view.findViewById( R.id.imageView );
		videoView = view.findViewById( R.id.videoView );
		progressBar = view.findViewById( R.id.progressBar );
		floatingMenu = view.findViewById( R.id.floating_menu );
		
		view.findViewById( R.id.settings ).setOnClickListener( v -> showSettings() );
		view.findViewById( R.id.download ).setOnClickListener( v -> downloadImage() );
		view.findViewById( R.id.share ).setOnClickListener( v -> shareImage() );
		
		if ( null != getResources() && null != getResources().getConfiguration() )
		{
			setFloatingMenuOrientation( getResources().getConfiguration().orientation );
		}
		
		ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
		progressBarDrawable.setColor( getResources().getColor( R.color.imgur_color ) );
		progressBarDrawable.setBarWidth( MetricsHelper.dpToPx( context, 4 ) );
		
		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
			.setActualImageScaleType( ScalingUtils.ScaleType.FIT_CENTER )
			.setProgressBarImage( progressBarDrawable )
			.build();
		
		imageView.setHierarchy( hierarchy );
		
		contentView.setOnClickListener( v -> toggle() );
		
		Bundle args = getArguments();
		
		String path = args.getString( PARAM_RESOURCE_PATH );
		
		if ( null != path )
		{
			loadResource( Uri.parse( path ) );
		}
	}
	
	public void loadResource( Uri uri )
	{
		new ResourceSolver( new ResourceLoadListener()
		{
			@Override
			public void loadVideo( Uri uri )
			{
				ImageViewerFragment.this.loadVideo( uri );
			}
			
			@Override
			public void loadImage( Uri uri, Uri thumbnail )
			{
				ImageViewerFragment.this.loadImage( uri, thumbnail );
			}
			
			@Override
			public void loadAlbum( Uri uri, Class<?> view )
			{
				Intent intent = new Intent( context, view );
				intent.putExtra( AppActivity.ALBUM_DATA, uri.toString() );
				startActivity( intent );
				getActivity().finish();
			}
		} ).solve( uri );
	}
	
	protected OnViewTapListener touchListener = new OnViewTapListener()
	{
		@Override
		public void onViewTap(View view, float x, float y)
		{
			if ( !( System.currentTimeMillis() - lastClickTime <= UI_ANIMATION_DELAY ) )
			{
				new Handler().postDelayed( () ->
				{
					long diff = System.currentTimeMillis() - lastClickTime;
					
					if ( diff >= UI_ANIMATION_DELAY )
					{
						toggle();
					}
				}, UI_ANIMATION_DELAY );
			}
			
			lastClickTime = System.currentTimeMillis();
		}
	};
	
	protected MediaPlayerFragment mediaPlayerFragment;
	
	protected void createMediaPlayer()
	{
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace( R.id.player, mediaPlayerFragment = new MediaPlayerFragment() );
		fragmentTransaction.commitAllowingStateLoss();
		
		mediaPlayerFragment.setVideoView( videoView );
	}
	
	public void loadImage( Uri uri, Uri thumbnail )
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
				Toast.makeText( context, throwable.getMessage(), Toast.LENGTH_SHORT ).show();
			}
		} );
		
		imageView.setOnViewTapListener( touchListener );
		
		delayedHide();
	}
	
	public void loadVideo( Uri uri )
	{
		currentResource = uri;
		
		Log.v( TAG, "Loading video: " + uri.toString() );
		
		imageView.setVisibility( View.GONE );
		videoView.setVisibility( View.VISIBLE );
		videoView.setOnClickListener( v -> toggle() );
		videoView.setVideoURI( uri );
		videoView.start();
		
		createMediaPlayer();
		
		mediaPlayerFragment.setOnPreparedListener( mp ->
		{
				mp.setLooping( true );
				
				progressBar.setVisibility( View.INVISIBLE );
		} );
		
		delayedHide();
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
		if ( null != currentResource )
		{
			new DownloadService( context ).download( currentResource, URLUtil.guessFileName( currentResource.toString(), null, null ) );
		}
	}
	
	public void showSettings()
	{
		startActivity( new Intent( context, SettingsView.class ) );
	}
	
	public void downloadImage()
	{
		if ( !new PermissionService().askExternalStorageAccess( this ) )
		{
			download();
		}
	}
	
	public void shareImage()
	{
		if ( currentResource != null )
		{
			IntentUtils.shareMessage( context, getString( R.string.share ), currentResource.toString(), getString( R.string.shareUsing ) );
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
	private final Runnable hideRunnable = this::hide;
	
	private void delayedHide()
	{
		hideHandler.removeCallbacks( hideRunnable );
		hideHandler.postDelayed( hideRunnable, 100 );
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
				floatingMenu.setPadding( 0, MetricsHelper.dpToPx( context, 8 ), 0, 0 );
				ViewHelper.setMargins( floatingMenu, 0, MetricsHelper.getStatusBarHeight( context ), 0, 0 );
			}
			else if ( orientation == Configuration.ORIENTATION_LANDSCAPE )
			{
				floatingMenu.setPadding( 0, MetricsHelper.dpToPx( context, 8 ), 0, 0 );
				ViewHelper.setMargins( floatingMenu, 0, MetricsHelper.getStatusBarHeight( context ), MetricsHelper.getNavigationBarWidth( context ), 0 );
			}
		}
	}
}
