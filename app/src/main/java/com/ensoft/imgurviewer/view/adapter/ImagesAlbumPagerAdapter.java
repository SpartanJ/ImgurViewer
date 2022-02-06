package com.ensoft.imgurviewer.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.view.fragment.ImageViewerFragment;

public class ImagesAlbumPagerAdapter extends FragmentPagerAdapter
{
	private ImgurImage[] images;
	private ImageViewerFragment[] fragments;
	
	public ImagesAlbumPagerAdapter( FragmentManager fragmentManager, ImgurImage[] images )
	{
		super( fragmentManager );
		
		this.images = images;
		
		this.fragments = new ImageViewerFragment[ images.length ];
	}
	
	@Override
	public Fragment getItem( int position )
	{
		ImgurImage image = images[ position ];
		
		fragments[ position ] = ImageViewerFragment.newInstance( image.hasVideo() ? image.getVideoUri().toString() : ( null != image.getFullSizeLink() ? image.getFullSizeLink() : image.getLink() ), position );
		
		return fragments[ position ];
	}
	
	@Override
	public int getCount()
	{
		return null != images ? images.length : 0;
	}
	
	public ImageViewerFragment getImageViewerFragment( int position )
	{
		if ( position >= 0 && position < fragments.length )
		{
			return fragments[ position ];
		}
		
		return null;
	}
}
