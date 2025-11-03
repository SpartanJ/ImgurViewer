package com.ensoft.imgurviewer.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.imgurviewer.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

@SuppressLint( "Registered" )
public class AppActivity extends FragmentActivity
{
	public static final String ALBUM_DATA = "albumData";
	
	protected SystemBarTintManager tintManager;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setDimAmount( 0.f );
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
		{
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
		}
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R )
		{
			getWindow().setDecorFitsSystemWindows( false );
		}
	}
	
	@Override
	protected void onPostCreate( @Nullable Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		
		if ( tintManager == null )
			tintManager = new SystemBarTintManager(this);
	}
	
	public void statusBarTint()
	{
		if ( tintManager != null )
		{
			tintManager.setStatusBarTintEnabled( true );
			tintManager.setTintColor( getResources().getColor( R.color.toolbar_background ) );
		}
	}
	
	public void statusBarUntint()
	{
		if ( tintManager != null )
		{
			tintManager.setTintColor( Color.TRANSPARENT );
		}
	}
}
