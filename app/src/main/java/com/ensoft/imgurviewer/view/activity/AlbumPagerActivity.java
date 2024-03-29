package com.ensoft.imgurviewer.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.ensoft.imgurviewer.service.TransparencyUtils;
import com.ensoft.imgurviewer.service.listener.AlbumPagerProvider;
import com.ensoft.imgurviewer.view.adapter.ImagesAlbumPagerAdapter;
import com.ensoft.imgurviewer.view.fragment.ImageViewerFragment;
import com.ensoft.imgurviewer.view.helper.SlidrPositionHelper;
import com.imgurviewer.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.Arrays;

public class AlbumPagerActivity extends AppActivity implements AlbumPagerProvider
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
	private int currentPage;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		App.getInstance().waitForInitialization();
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
		
		if ( App.getInstance().getPreferencesService().getDisableWindowTransparency() )
			TransparencyUtils.convertActivityFromTranslucent( this );
		
		Bundle bundle = getIntent().getExtras();
		
		if ( bundle != null )
		{
			try
			{
				Parcelable[] imagesParcelable = bundle.getParcelableArray( PARAM_IMAGES );
				
				ImgurImage[] images = Arrays.copyOf( imagesParcelable, imagesParcelable.length, ImgurImage[].class );
				
				int initialPosition = bundle.getInt( PARAM_IMAGES_CUR_POSITION );
				
				pager.setAdapter( adapter = new ImagesAlbumPagerAdapter( getSupportFragmentManager(), images ) );
				pager.setCurrentItem( initialPosition );
				currentPage = initialPosition;
				pager.addOnPageChangeListener( new ViewPager.OnPageChangeListener()
				{
					@Override
					public void onPageScrolled( int i, float v, int i1 )
					{}
					
					@Override
					public void onPageSelected( int i )
					{
						if ( currentPage != i )
						{
							adapter.getImageViewerFragment( currentPage ).onViewHide();
						}
						
						adapter.getImageViewerFragment( i ).onViewShow();
						
						currentPage = i;
					}
					
					@Override
					public void onPageScrollStateChanged( int i )
					{}
				} );
			}
			catch ( Exception e )
			{
				Toast.makeText( getApplicationContext(), R.string.errorLoadingAlbum, Toast.LENGTH_LONG ).show();
				
				finish();
			}
		}
	}
	
	@Override
	public int getCurrentPage()
	{
		return currentPage;
	}
}
