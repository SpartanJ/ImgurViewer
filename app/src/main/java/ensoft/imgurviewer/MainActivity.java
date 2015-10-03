package ensoft.imgurviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity
{
	public static final String TAG = MainActivity.class.getCanonicalName();
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;
	
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	
	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private static final int UI_ANIMATION_DELAY = 300;
	
	private View mContentView;
	private View mControlsView;
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	private boolean mVisible;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );

		mVisible = true;
		mControlsView = findViewById( R.id.fullscreen_content_controls );
		mContentView = findViewById( R.id.fullscreen_content );
		mImageView = (ImageView)findViewById( R.id.imageView );
		mProgressBar = (ProgressBar)findViewById( R.id.progressBar );

		mProgressBar.setVisibility( View.INVISIBLE );

		// Set up the user interaction to manually show or hide the system UI.
		mContentView.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				toggle();
			}
		} );

		if ( getIntent().getData() != null )
		{
			Uri data = getIntent().getData();//set a variable for the Intent

			Log.v( TAG, "Data is: " + data.toString() );

			String scheme = data.getScheme();//get the scheme (http,https)
			String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments

			if ( fullPath.startsWith( "imgur.com" ) )
			{
				fullPath = fullPath.replace( "imgur.com", "i.imgur.com" );
			}

			String url = scheme + ":" + fullPath; //combine to get a full URI

			if ( !fullPath.endsWith( ".png" ) && !fullPath.endsWith( ".jpg" ) && !fullPath.endsWith( ".jpeg" ) && !fullPath.endsWith( ".gif" ) )
			{
				if ( !fullPath.endsWith( ".gfv" ) )
				{
					url += ".jpg";
				}
				else
				{

				}
			}

			Log.v( TAG, "Loading: " + url );

			mProgressBar.setVisibility( View.VISIBLE );
			Glide.with( this ).load( url ).listener( new RequestListener<String, GlideDrawable>()
			{
				@Override
				public boolean onException( Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource )
				{
					Log.v( TAG, "Error loading image: " + e.toString() );
					mProgressBar.setVisibility( View.INVISIBLE );
					return false;
				}

				@Override
				public boolean onResourceReady( GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource )
				{
					Log.v( TAG, "Image Loaded" );
					mProgressBar.setVisibility( View.INVISIBLE );
					return false;
				}
			} ).crossFade().thumbnail(
				Glide.with( this )
					.load( url )
					.crossFade()
					.sizeMultiplier( 0.2f )
					.priority( Priority.HIGH )
			).into( mImageView );
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
		mControlsView.setVisibility( View.GONE );
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
			mControlsView.setVisibility( View.VISIBLE );
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
