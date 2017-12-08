package com.ensoft.imgurviewer.model;

import com.google.gson.annotations.SerializedName;

public class InstagramProfileModel
{
	@SerializedName( "nodes" )
	protected InstagramItem[] items;
	
	public InstagramItem[] getItems()
	{
		return items;
	}
	
	public boolean hasItems()
	{
		return null != items && items.length > 0;
	}
	
	public ImgurImage[] getImages()
	{
		InstagramItem[] items = getItems();
		ImgurImage[] images = new ImgurImage[ getItems().length ];
		int c = 0;
		
		for ( InstagramItem item : items )
		{
			if ( item.isVideo() )
			{
				images[ c ] = new ImgurImage( item.getId(), item.getImage().toString(), item.getThumbnail(), null, item.getTitle() );
			}
			else
			{
				images[ c ] = new ImgurImage( item.getId(), item.getImage().toString(), item.getThumbnail(), item.getTitle() );
			}
			
			c++;
		}
		
		return images;
	}
}
