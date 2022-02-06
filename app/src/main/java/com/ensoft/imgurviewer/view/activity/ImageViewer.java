package com.ensoft.imgurviewer.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.view.fragment.ImageViewerFragment;
import com.imgurviewer.R;

public class ImageViewer extends AppActivity
{
	public static final String TAG = ImageViewer.class.getCanonicalName();
	public static final String PARAM_RESOURCE_PATH = "resourcePath";
	
	protected ImageViewerFragment imageViewer;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_imageviewer );
	}
	
	@Override
	protected void onPostCreate( @Nullable Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		
		loadResource();
	}
	
	@Override
	protected void onDestroy()
	{
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R )
		{
			Window window = getWindow();
			WindowInsetsController controller = window.getInsetsController();
			
			if (controller != null)
			{
				controller.show( WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars() );
			}
		}
		
		super.onDestroy();
	}
	
	protected void loadFragment( Uri uri )
	{
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace( R.id.image_viewer, imageViewer = ImageViewerFragment.newInstance( uri.toString() ) );
		fragmentTransaction.commitAllowingStateLoss();
	}
	
	protected void loadResource()
	{
		if ( null != getIntent().getExtras() && null != getIntent().getExtras().getString( PARAM_RESOURCE_PATH ) )
		{
			loadFragment( Uri.parse( getIntent().getExtras().getString( PARAM_RESOURCE_PATH ) ) );
		}
		else if ( getIntent().getData() != null )
		{
			Uri data = getIntent().getData();
			
			Log.v( TAG, "Data is: " + data.toString() );
			
			loadFragment( data );
		}
		else  if ( Intent.ACTION_SEND.equals( getIntent().getAction() ) && getIntent() != null && "text/plain".equals( getIntent().getType() ) )
		{
			loadFragment( Uri.parse( getIntent().getStringExtra( Intent.EXTRA_TEXT ) ) );
		}
		else
		{
			TutorialActivity.newInstance( this );
			finish();
		}
	}
}
