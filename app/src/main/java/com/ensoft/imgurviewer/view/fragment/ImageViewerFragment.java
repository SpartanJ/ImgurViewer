package com.ensoft.imgurviewer.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
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

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.DownloadService;
import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.IntentUtils;
import com.ensoft.imgurviewer.service.PermissionService;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.ResourceSolver;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.event.OnViewLockStateChange;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.ensoft.imgurviewer.view.activity.AppActivity;
import com.ensoft.imgurviewer.view.activity.SettingsActivity;
import com.ensoft.imgurviewer.view.helper.MetricsHelper;
import com.ensoft.imgurviewer.view.helper.SlidrPositionHelper;
import com.ensoft.imgurviewer.view.helper.ViewHelper;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.imgurviewer.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class ImageViewerFragment extends Fragment
{
	public static final String TAG = ImageViewerFragment.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	private static final int UI_ANIMATION_DELAY = 300;
	
	private AppActivity context;
	
	private View contentContainer;
	private View contentView;
	private ProgressBar progressBar;
	private View progressLine;
	private BigImageView imageView;
	private SimpleDraweeView imageViewFallback;
	private VideoView videoView;
	private boolean visible;
	private long lastClickTime;
	private LinearLayout floatingMenu;
	private Uri currentResource;
	private SlidrInterface slidrInterface;
	private boolean viewLocked = false;
	private boolean requestingPermissions = false;
	
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
		
		context = (AppActivity)getActivity();
		context.statusBarTint();
		visible = true;
		contentContainer = view.findViewById( R.id.content_container );
		contentView = view.findViewById( R.id.fullscreen_content );
		imageView = view.findViewById( R.id.imageView );
		imageViewFallback = view.findViewById( R.id.imageViewFallback );
		videoView = view.findViewById( R.id.videoView );
		progressBar = view.findViewById( R.id.progressBar );
		progressLine = view.findViewById( R.id.progressLine );
		floatingMenu = view.findViewById( R.id.floating_menu );
		
		view.findViewById( R.id.settings ).setOnClickListener( v -> showSettings() );
		view.findViewById( R.id.download ).setOnClickListener( v -> downloadImage() );
		view.findViewById( R.id.share ).setOnClickListener( v -> shareImage() );
		
		if ( null != getResources() && null != getResources().getConfiguration() )
		{
			setFloatingMenuOrientation( getResources().getConfiguration().orientation );
		}
		
		contentView.setOnClickListener( v -> toggle() );
		
		Bundle args = getArguments();
		
		String path = args.getString( PARAM_RESOURCE_PATH );
		
		if ( null != path )
		{
			Uri uri = Uri.parse( path );
			
			if ( UriUtils.isContentUri( uri ) )
			{
				solveContentUri( uri );
			}
			else
			{
				loadResource( uri );
			}
		}
	}
	
	protected void solveContentUri( Uri uri )
	{
		try
		{
			if ( !new PermissionService().askReadExternalStorageAccess( this ) )
			{
				loadResource( UriUtils.contentUriToFileUri( context, uri ) );
			}
			else
			{
				requestingPermissions = true;
				currentResource = uri;
			}
		}
		catch ( Exception e )
		{
			getActivity().finish();
		}
	}
	
	public void loadResource( Uri uri )
	{
		new ResourceSolver( new ResourceLoadListener()
		{
			@Override
			public void loadVideo( Uri uri, MediaType mediaType, Uri referer )
			{
				ImageViewerFragment.this.loadVideo( uri, mediaType );
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
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		PreferencesService preferencesService = App.getInstance().getPreferencesService();
		
		if ( preferencesService.gesturesEnabled() )
		{
			if ( null != slidrInterface && requestingPermissions )
			{
				requestingPermissions = false;
				
				return;
			}
			
			slidrInterface = Slidr.replace( contentContainer, new SlidrConfig.Builder().listener( new SlidrListener()
			{
				@Override
				public void onSlideStateChanged( int state ) {}
				
				@Override
				public void onSlideChange( float percent )
				{
					contentContainer.setBackgroundColor( (int) ( percent * 255.0f + 0.5f ) << 24 );
				}
				
				@Override
				public void onSlideOpened() {}
				
				@Override
				public boolean onSlideClosed() { return false; }
			} ).position( SlidrPositionHelper.fromString( preferencesService.getGesturesImageView() ) ).build() );
		}
	}
	
	protected void onImageClick()
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
	
	protected MediaPlayerFragment mediaPlayerFragment;
	
	protected void createMediaPlayer()
	{
		PreferencesService preferencesService = App.getInstance().getPreferencesService();
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace( R.id.player, mediaPlayerFragment = MediaPlayerFragment.newInstance( preferencesService.fullscreenButton(), preferencesService.screenLockButton() ) );
		fragmentTransaction.commitAllowingStateLoss();
		
		mediaPlayerFragment.setVideoView( videoView );
	}
	
	public void loadImageFallback( Uri uri, Uri thumbnail )
	{
		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder( imageViewFallback.getResources() )
			.setActualImageScaleType( ScalingUtils.ScaleType.FIT_CENTER )
			.build();
		
		imageView.setVisibility( View.GONE );
		
		progressBar.setVisibility( View.VISIBLE );
		
		progressLine.setVisibility( View.GONE );
		
		imageViewFallback.setHierarchy( hierarchy );
		
		imageViewFallback.setVisibility( View.VISIBLE );
		
		new FrescoService().loadImage( uri, thumbnail, imageViewFallback, new ControllerImageInfoListener()
		{
			@Override
			public void onFinalImageSet( String id, ImageInfo imageInfo, Animatable animatable )
			{
				progressBar.setVisibility( View.INVISIBLE );
			}
			
			@Override
			public void onFailure( String id, Throwable throwable )
			{
				Log.v( TAG, throwable.toString() );
				
				Toast.makeText( context, throwable.toString(), Toast.LENGTH_SHORT ).show();
			}
		}, new Point( 0,0 ) );
	}
	
	public void loadImage( Uri uri, Uri thumbnail )
	{
		currentResource = uri;
		
		Log.v( TAG, "Loading image: " + uri.toString() );
		
		videoView.setVisibility( View.GONE );
		
		imageView.setOptimizeDisplay( false );
		imageView.setOnClickListener( v -> onImageClick() );
		
		imageView.setImageLoaderCallback( new ImageLoader.Callback()
		{
			@Override
			public void onCacheHit( File image ) {}
			
			@Override
			public void onCacheMiss( File image ) {}
			
			@Override
			public void onStart()
			{
				progressLine.setVisibility( View.VISIBLE );
			}
			
			@Override
			public void onProgress( int progress )
			{
				progressLine.getLayoutParams().width = ( (int)( ((View)progressLine.getParent()).getWidth() * ( progress / 100.f ) ) );
				progressLine.requestLayout();
			}
			
			@Override
			public void onFinish()
			{}
			
			@Override
			public void onSuccess( File image )
			{}
			
			@Override
			public void onFail( Exception error )
			{
				imageView.setVisibility( View.GONE );
				
				Toast.makeText( context, error.getMessage(), Toast.LENGTH_SHORT ).show();
			}
		} );
		
		imageView.setOnImageEventListener( new SubsamplingScaleImageView.OnImageEventListener()
		{
			@Override
			public void onReady()
			{}
			
			@Override
			public void onImageLoaded()
			{
				progressBar.setVisibility( View.GONE );
				progressLine.setVisibility( View.GONE );
			}
			
			@Override
			public void onPreviewLoadError( Exception e ) {}
			
			@Override
			public void onImageLoadError( Exception e )
			{
				loadImageFallback( uri, thumbnail );
			}
			
			@Override
			public void onTileLoadError( Exception e )
			{
				loadImageFallback( uri, thumbnail );
			}
			
			@Override
			public void onPreviewReleased() {}
		} );
		
		imageView.showImage( uri.toString().hashCode(), null != thumbnail ? thumbnail : Uri.EMPTY, uri );
		
		delayedHide();
	}
	
	private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
	private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY = new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0 (Chrome)", BANDWIDTH_METER );
	
	private static MediaSource buildMediaSource( Context context, Uri uri, MediaType mediaType )
	{
		if ( MediaType.STREAM_SS == mediaType )
			return new SsMediaSource.Factory( new DefaultSsChunkSource.Factory( DATA_SOURCE_FACTORY ), DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( MediaType.STREAM_DASH == mediaType )
			return new DashMediaSource.Factory( new DefaultDashChunkSource.Factory( DATA_SOURCE_FACTORY ), DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( MediaType.STREAM_HLS == mediaType )
			return new HlsMediaSource.Factory( DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( UriUtils.isFileUri( uri ) )
			return new ExtractorMediaSource.Factory( new DefaultDataSourceFactory( context, "ua" ) ).setExtractorsFactory( new DefaultExtractorsFactory() ).createMediaSource( uri );
		
		return new ExtractorMediaSource.Factory( DATA_SOURCE_FACTORY ).createMediaSource(uri);
	}
	
	public void loadVideo( Uri uri, MediaType mediaType )
	{
		currentResource = uri;
		
		Log.v( TAG, "Loading video: " + uri.toString() );
		
		imageView.setVisibility( View.GONE );
		
		videoView.setOnClickListener( v -> toggle() );
		
		MediaSource mediaSource;
		
		if ( UriUtils.isAudioUrl( uri ) )
		{
			mediaSource = buildMediaSource( getActivity(), uri, mediaType );
		}
		else
		{
			mediaSource = new LoopingMediaSource( buildMediaSource( getActivity(), uri, mediaType ) );
		}
		
		createMediaPlayer();
		
		mediaPlayerFragment.setOnPreparedListener( () ->
		{
			progressBar.setVisibility( View.INVISIBLE );
			
			videoView.setVisibility( View.VISIBLE );
			
			videoView.setBackgroundColor( Color.TRANSPARENT );
		} );
		
		videoView.setVideoURI( uri, mediaSource );
		
		delayedHide();
	}
	
	@Override
	public void onRequestPermissionsResult( final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults )
	{
		super.onRequestPermissionsResult( requestCode, permissions, grantResults );
		
		if ( requestCode == PermissionService.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION )
		{
			if ( grantResults[0] == 0 )
			{
				download();
			}
			else
			{
				Toast.makeText( getActivity(), R.string.cantDownloadNoPermission, Toast.LENGTH_LONG ).show();
			}
		}
		else if ( requestCode == PermissionService.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION )
		{
			if ( grantResults[0] == 0 )
			{
				loadResource( UriUtils.contentUriToFileUri( context, currentResource ) );
			}
			else
			{
				Toast.makeText( getActivity(), R.string.cantDisplayResourceNoPermission, Toast.LENGTH_LONG ).show();
				
				getActivity().finish();
			}
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
		startActivity( new Intent( context, SettingsActivity.class ) );
	}
	
	public void downloadImage()
	{
		if ( !new PermissionService().askExternalStorageAccess( this ) )
		{
			download();
		}
		else
		{
			requestingPermissions = true;
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
		if ( viewLocked )
			return;
		
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
	
	private void setSystemUiVisibility()
	{
		int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
			flags |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
			flags |=  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
		contentView.setSystemUiVisibility( flags );
	}
	
	private void hideFast()
	{
		visible = false;
		
		setSystemUiVisibility();
		
		floatingMenu.setVisibility( View.INVISIBLE );
		
		context.statusBarUntint();
	}
	
	private final Runnable hidePart2Runnable = new Runnable()
	{
		@Override
		public void run()
		{
			setSystemUiVisibility();
			
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
					
					context.statusBarUntint();
					
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
		
		context.statusBarTint();
	}
	
	public View getContentContainer()
	{
		return contentContainer;
	}
	
	private final Handler hideHandler = new Handler();
	private final Runnable hideRunnable = this::hide;
	
	private void delayedHide()
	{
		hideHandler.removeCallbacks( hideRunnable );
		hideHandler.postDelayed( hideRunnable, 100 );
	}
	
	@Override
	public void onConfigurationChanged( Configuration newConfig )
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
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onStop()
	{
		EventBus.getDefault().unregister(this);
		
		super.onStop();
	}
	
	@Subscribe( threadMode = ThreadMode.MAIN )
	public void onMessageEvent( OnViewLockStateChange event )
	{
		viewLocked = event.isLocked();
		
		if ( viewLocked )
		{
			hideFast();
			
			if ( null != slidrInterface )
				slidrInterface.lock();
		}
		else
		{
			show();
			
			if ( null != slidrInterface )
				slidrInterface.unlock();
		}
	}
}
