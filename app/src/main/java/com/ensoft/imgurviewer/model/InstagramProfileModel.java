package com.ensoft.imgurviewer.model;

import android.net.Uri;

import com.ensoft.imgurviewer.model.instagram.Edge;
import com.ensoft.imgurviewer.model.instagram.InstagramProfileBaseModel;
import com.ensoft.imgurviewer.model.instagram.Node;

import java.util.List;

public class InstagramProfileModel
{
	protected List<Edge> items;
	
	public List<Edge> getItems()
	{
		return items;
	}
	
	public InstagramProfileModel( InstagramProfileBaseModel instagramProfileBaseModel )
	{
		try
		{
			this.items = instagramProfileBaseModel.entryData.profilePage.get( 0 ).graphql.user.edgeOwnerToTimelineMedia.edges;
		}
		catch ( Exception e )
		{}
	}
	
	public boolean hasItems()
	{
		return null != items && items.size() > 0;
	}
	
	public ImgurImage[] getImages()
	{
		List<Edge> items = getItems();
		ImgurImage[] images = new ImgurImage[ getItems().size() ];
		int c = 0;
		
		for ( Edge itemEdge : items )
		{
			Node item = itemEdge.node;
			String caption = ( null != item.edgeMediaToCaption && null != item.edgeMediaToCaption.edges && item.edgeMediaToCaption.edges.size() > 0 ) ? item.edgeMediaToCaption.edges.get( 0 ).node.text : "";
			
			if ( item.isVideo )
			{
				images[ c ] = new ImgurImage( item.id, item.displayUrl, Uri.parse( item.thumbnailSrc ), null, caption );
			}
			else
			{
				images[ c ] = new ImgurImage( item.id, item.displayUrl, Uri.parse( item.thumbnailSrc ), caption );
			}
			
			c++;
		}
		
		return images;
	}
}
