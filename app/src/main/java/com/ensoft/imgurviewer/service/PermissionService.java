package com.ensoft.imgurviewer.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

public class PermissionService
{
	public static int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 0x0F3124;
	
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
	
}
