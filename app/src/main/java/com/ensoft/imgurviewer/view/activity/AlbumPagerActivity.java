package com.ensoft.imgurviewer.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.view.adapter.ImagesAlbumPagerAdapter;
import com.imgurviewer.R;

import java.util.Arrays;

public class AlbumPagerActivity extends AppActivity
{
	public static final String PARAM_IMAGES = "images";
	public static final String PARAM_IMAGES_CUR_POSITION = "imagesCurPosition";
	
	public static void newInstance( Context context, ImgurImage[] images, int imagesCurPosition )
	{
		Intent intent = new Intent( context, AlbumPagerActivity.class );
		intent.putExtra( PARAM_IMAGES, images );
		intent.putExtra( PARAM_IMAGES_CUR_POSITION, imagesCurPosition );
		context.startActivity( intent );
	}
	
	private ViewPager pager;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_albumpager );
		
		pager = findViewById( R.id.view_pager );
	}
	
	@Override
	protected void onPostCreate( @Nullable Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		
		Bundle bundle = getIntent().getExtras();
		
		if ( bundle != null )
		{
			try
			{
				Parcelable[] imagesParceable = bundle.getParcelableArray( PARAM_IMAGES );
				
				ImgurImage[] images = Arrays.copyOf( imagesParceable, imagesParceable.length, ImgurImage[].class );
				
				int initialPosition = bundle.getInt( PARAM_IMAGES_CUR_POSITION );
				
				pager.setAdapter( new ImagesAlbumPagerAdapter( getFragmentManager(), images ) );
				pager.setCurrentItem( initialPosition );
			}
			catch ( Exception e )
			{
				Toast.makeText( getApplicationContext(), R.string.errorLoadingAlbum, Toast.LENGTH_LONG ).show();
				
				finish();
			}
		}
	}
}
