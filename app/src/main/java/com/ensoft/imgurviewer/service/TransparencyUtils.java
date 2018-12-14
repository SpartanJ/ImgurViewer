package com.ensoft.imgurviewer.service;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import java.lang.reflect.Method;

public class TransparencyUtils
{
	public static void convertActivityFromTranslucent( Activity activity )
	{
		try
		{
			Method method = Activity.class.getDeclaredMethod( "convertFromTranslucent" );
			method.setAccessible( true );
			method.invoke( activity );
		}
		catch ( Throwable t )
		{
		}
	}
	
	public static void convertActivityToTranslucent( Activity activity )
	{
		if ( null == activity )
			return;
		
		activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		activity.getWindow().getDecorView().setBackgroundDrawable(null);
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
		{
			convertActivityToTranslucentAfterL( activity );
		}
		else
		{
			convertActivityToTranslucentBeforeL( activity );
		}
	}
	
	public static void convertActivityToTranslucentBeforeL( Activity activity )
	{
		try
		{
			Class<?>[] classes = Activity.class.getDeclaredClasses();
			Class<?> translucentConversionListenerClazz = null;
			for ( Class clazz : classes )
			{
				if ( clazz.getSimpleName().contains( "TranslucentConversionListener" ) )
				{
					translucentConversionListenerClazz = clazz;
				}
			}
			Method method = Activity.class.getDeclaredMethod( "convertToTranslucent", translucentConversionListenerClazz );
			method.setAccessible( true );
			method.invoke( activity, new Object[]{
				null
			} );
		}
		catch ( Throwable t )
		{
		}
	}
	
	private static void convertActivityToTranslucentAfterL( Activity activity )
	{
		try
		{
			Method getActivityOptions = Activity.class.getDeclaredMethod( "getActivityOptions" );
			getActivityOptions.setAccessible( true );
			Object options = getActivityOptions.invoke( activity );
			
			Class<?>[] classes = Activity.class.getDeclaredClasses();
			Class<?> translucentConversionListenerClazz = null;
			for ( Class clazz : classes )
			{
				if ( clazz.getSimpleName().contains( "TranslucentConversionListener" ) )
				{
					translucentConversionListenerClazz = clazz;
				}
			}
			Method convertToTranslucent = Activity.class.getDeclaredMethod( "convertToTranslucent", translucentConversionListenerClazz, ActivityOptions.class );
			convertToTranslucent.setAccessible( true );
			convertToTranslucent.invoke( activity, null, options );
		}
		catch ( Throwable t )
		{
		}
	}
}