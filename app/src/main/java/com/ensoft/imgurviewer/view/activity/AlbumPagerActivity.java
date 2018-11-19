package com.ensoft.imgurviewer.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.view.adapter.ImagesAlbumPagerAdapter;
import com.ensoft.imgurviewer.view.fragment.ImageViewerFragment;
import com.ensoft.imgurviewer.view.helper.SlidrPositionHelper;
import com.imgurviewer.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;

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
	private ImagesAlbumPagerAdapter adapter;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_albumpager );
		
		pager = findViewById( R.id.view_pager );
		
		PreferencesService preferencesService = App.getInstance().getPreferencesService();
		
		SlidrPosition slidrPosition = SlidrPositionHelper.fromString( preferencesService.getGesturesImageView() );
		
		if ( preferencesService.gesturesEnabled() && ( slidrPosition == SlidrPosition.HORIZONTAL || slidrPosition == SlidrPosition.LEFT || slidrPosition == SlidrPosition.RIGHT ) )
		{
			Slidr.attach( this, new SlidrConfig.Builder().listener( new SlidrListener()
			{
				@Override
				public void onSlideStateChanged( int state ) {}
				
				@Override
				public void onSlideChange( float percent )
				{
					if ( null != adapter && null != adapter.getImageViewerFragment( pager.getCurrentItem() ) )
					{
						ImageViewerFragment fragment = adapter.getImageViewerFragment( pager.getCurrentItem() );
						
						if ( null != fragment.getContentContainer() )
						{
							fragment.getContentContainer().setBackgroundColor( (int) ( percent * 255.0f + 0.5f ) << 24 );
						}
					}
				}
				
				@Override
				public void onSlideOpened() {}
				
				@Override
				public boolean onSlideClosed() { return false; }
			} ).position( SlidrPositionHelper.fromString( preferencesService.getGesturesImageView() ) ).build() );
		}
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
				Parcelable[] imagesParcelable = bundle.getParcelableArray( PARAM_IMAGES );
				
				ImgurImage[] images = Arrays.copyOf( imagesParcelable, imagesParcelable.length, ImgurImage[].class );
				
				int initialPosition = bundle.getInt( PARAM_IMAGES_CUR_POSITION );
				
				pager.setAdapter( adapter = new ImagesAlbumPagerAdapter( getFragmentManager(), images ) );
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
