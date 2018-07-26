package com.ensoft.imgurviewer.service;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

public class PermissionService
{
	public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
	public static int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 0x0F3124;
	public static int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 0x0F3125;
	
	public boolean isExternalStorageAccess( Context context )
	{
		return ContextCompat.checkSelfPermission( context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED;
	}
	
	public boolean askExternalStorageAccess( Activity activity )
	{
		if ( !isExternalStorageAccess( activity ) )
		{
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			{
				activity.requestPermissions( new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION );
				return true;
			}
		}
		
		return false;
	}
	
	public boolean askExternalStorageAccess( Fragment fragment )
	{
		if ( !isExternalStorageAccess( fragment.getActivity() ) )
		{
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			{
				fragment.requestPermissions( new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION );
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isReadExternalStorageAccess( Context context )
	{
		return ContextCompat.checkSelfPermission( context, READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED;
	}
	
	public boolean askReadExternalStorageAccess( Activity activity )
	{
		if ( !isReadExternalStorageAccess( activity ) )
		{
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			{
				activity.requestPermissions( new String[]{ READ_EXTERNAL_STORAGE }, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION );
				return true;
			}
		}
		
		return false;
	}
	
	public boolean askReadExternalStorageAccess( Fragment fragment )
	{
		if ( !isReadExternalStorageAccess( fragment.getActivity() ) )
		{
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			{
				fragment.requestPermissions( new String[]{ READ_EXTERNAL_STORAGE }, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION );
				return true;
			}
		}
		
		return false;
	}
}
