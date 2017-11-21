package com.ensoft.imgurviewer.view.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ensoft.imgurviewer.model.ImgurImage;

public class ImageAlbumPager extends FragmentPagerAdapter
{
	private ImgurImage[] images;
	
	public ImageAlbumPager( FragmentManager fragmentManager, ImgurImage[] images )
	{
		super( fragmentManager );
		
		this.images = images;
	}
	
	@Override
	public Fragment getItem( int position )
	{
		ImgurImage image = images[ position ];
		
		return ImageViewerFragment.newInstance( image.hasVideo() ? image.getVideoUri().toString() : image.getLink() );
	}
	
	@Override
	public int getCount()
	{
		return null != images ? images.length : 0;
	}
}