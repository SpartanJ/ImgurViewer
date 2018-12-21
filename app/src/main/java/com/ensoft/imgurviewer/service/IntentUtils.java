package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils
{
	public static void shareAsTextMessage( Context context, String title, String message, String chooserTitle )
	{
		Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
		sharingIntent.setType( "text/plain" );
		sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT, title );
		sharingIntent.putExtra( android.content.Intent.EXTRA_TEXT, message );
		context.startActivity( Intent.createChooser( sharingIntent, chooserTitle ) );
	}
	
	public static boolean shareAsMedia( Context context, Uri resource, String chooserTitle )
	{
		String mimeType = UriUtils.getMimeType( resource.toString() );
		
		if ( null != mimeType )
		{
			Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
			
			sharingIntent.setAction( Intent.ACTION_SEND );
			sharingIntent.setType( mimeType );
			sharingIntent.putExtra( android.content.Intent.EXTRA_STREAM, resource );
			context.startActivity( Intent.createChooser( sharingIntent, chooserTitle ) );
			
			return true;
		}
		else
		{
			return false;
		}
	}
}
