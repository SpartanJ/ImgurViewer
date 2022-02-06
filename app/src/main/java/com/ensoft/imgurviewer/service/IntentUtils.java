package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.imgurviewer.R;

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
			Intent sharingIntent = new Intent( Intent.ACTION_SEND );
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
	
	public static void shareAsBitmap( Context context, Bitmap bitmap, String chooserTitle )
	{
		String path = MediaStore.Images.Media.insertImage( context.getContentResolver(), bitmap, null, null);
		
		Uri bmpUri = Uri.parse( path );
		
		Intent shareIntent = new Intent( Intent.ACTION_SEND );
		shareIntent.setType( "image/*" );
		shareIntent.putExtra( Intent.EXTRA_STREAM, bmpUri );
		context.startActivity( Intent.createChooser( shareIntent, chooserTitle ) );
	}
	
	public static void shareAsBitmapFromUri( Context context, Uri currentResource, String chooserTitle )
	{
		try
		{
			ImagePipeline imagePipeline = Fresco.getImagePipeline();
			
			ImageRequest imageRequest = ImageRequestBuilder
				.newBuilderWithSource( currentResource )
				.setRequestPriority( Priority.HIGH )
				.setLowestPermittedRequestLevel( ImageRequest.RequestLevel.FULL_FETCH )
				.build();
			
			DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage( imageRequest, context );
			
			dataSource.subscribe( new BaseBitmapDataSubscriber()
			{
				@Override
				public void onNewResultImpl( @Nullable Bitmap bitmap )
				{
					if ( bitmap == null )
					{
						Toast.makeText( context, R.string.could_not_detect_mime_type, Toast.LENGTH_LONG ).show();
						return;
					}
					
					shareAsBitmap( context, bitmap, chooserTitle );
				}
				
				@Override
				public void onFailureImpl( DataSource dataSource )
				{
					Toast.makeText( context, R.string.could_not_detect_mime_type, Toast.LENGTH_LONG ).show();
				}
			}, CallerThreadExecutor.getInstance() );
		}
		catch ( Exception ignored )
		{
			Toast.makeText( context, R.string.could_not_detect_mime_type, Toast.LENGTH_LONG ).show();
		}
	}
}
