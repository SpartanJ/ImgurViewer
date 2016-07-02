package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.content.Intent;

public class IntentUtils
{
	public static void shareMessage( Context context, String title, String message, String chooserTitle )
	{
		Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
		sharingIntent.setType( "text/plain" );
		sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT, title );
		sharingIntent.putExtra( android.content.Intent.EXTRA_TEXT, message );
		context.startActivity( Intent.createChooser( sharingIntent, chooserTitle ) );
	}
}
