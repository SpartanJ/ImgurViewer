package com.ensoft.imgurviewer.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.UriUtils;
import com.ensoft.imgurviewer.service.resource.ImgurService;
import com.google.gson.annotations.SerializedName;

public class ImgurImage implements Parcelable
{
	@SerializedName( "id" )
	protected String id;
	
	@SerializedName( "title" )
	protected String title;
	
	@SerializedName( "description" )
	protected String description;
	
	@SerializedName( "datetime" )
	protected long dateTime;
	
	@SerializedName( "width" )
	protected int width;
	
	@SerializedName( "height" )
	protected int height;
	
	@SerializedName( "size" )
	protected long size;
	
	@SerializedName( "link" )
	protected String link;
	
	@SerializedName( "mp4" )
	protected String mp4;
	
	@SerializedName( "hls" )
	protected String hls;
	
	protected Uri thumbnailUri;
	
	protected Uri videoUri;
	
	protected String fullSizeLink;
	
	public ImgurImage( String id, String link )
	{
		this.id = id;
		this.link = link;
	}
	
	public ImgurImage( String id, String link, Uri thumbnailUri, Uri videoUri, String title, String description )
	{
		this.id = id;
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.videoUri = videoUri;
		this.title = title;
		this.description = description;
	}
	
	public ImgurImage( String id, String link, Uri thumbnailUri, Uri videoUri, String title )
	{
		this.id = id;
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.videoUri = videoUri;
		this.title = title;
	}
	
	public ImgurImage( String id, String link, Uri thumbnailUri, String title )
	{
		this.id = id;
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.title = title;
	}
	
	public ImgurImage( String link, Uri thumbnailUri, String title, String description )
	{
		this.link = link;
		this.thumbnailUri = thumbnailUri;
		this.title = title;
		this.description = description;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public long getDateTime()
	{
		return dateTime;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public long getSize()
	{
		return size;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public Uri getFullImageLinkUri()
	{
		return Uri.parse( getLink() );
	}
	
	public Uri getImageUri()
	{
		Uri link = Uri.parse( getLink() );
		
		if ( this.link.contains( ImgurService.IMGUR_DOMAIN ) )
		{
			return App.getInstance().getPreferencesService().thumbnailSizeOnGallery() != ThumbnailSize.FULL_IMAGE ?
				new ImgurService().getThumbnailPath( link, App.getInstance().getPreferencesService().thumbnailSizeOnGallery() ) : new ImgurService().getThumbnailPath( link, ThumbnailSize.FULL_IMAGE );
		}
		else
		{
			return link;
		}
	}
	
	public Uri getLinkUri()
	{
		Uri link = Uri.parse( getLink() );
		
		return App.getInstance().getPreferencesService().thumbnailSizeOnGallery() != ThumbnailSize.FULL_IMAGE && this.link.contains( ImgurService.IMGUR_DOMAIN ) ?
			new ImgurService().getThumbnailPath( link, App.getInstance().getPreferencesService().thumbnailSizeOnGallery() ) : link;
	}
	
	public Uri getThumbnailLinkUri()
	{
		return null != thumbnailUri ? thumbnailUri : new ImgurService().getThumbnailPath( getLinkUri(), ThumbnailSize.SMALL_SQUARE );
	}
	
	public boolean hasVideo()
	{
		return null != videoUri || null != hls || null != mp4 || UriUtils.isVideoUrl( getLinkUri() );
	}
	
	public Uri getVideoUri()
	{
		if ( null != mp4 )
			return Uri.parse( mp4 );
		
		if ( null != videoUri )
			return videoUri;
		
		Uri link = Uri.parse( getLink() );
		
		String lastPathSegment = link.getLastPathSegment();
		
		if ( null != lastPathSegment )
		{
			int dotPos = lastPathSegment.lastIndexOf( "." );
			
			String newPathSegment = lastPathSegment.substring( 0, dotPos ) + ".mp4";
			
			return Uri.parse( link.toString().replace( lastPathSegment, newPathSegment ) );
		}
		
		return null;
	}
	
	public String getFullSizeLink()
	{
		return fullSizeLink;
	}
	
	public void setFullSizeLink( String fullSizeLink )
	{
		this.fullSizeLink = fullSizeLink;
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}
	
	@Override
	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeString( this.id );
		dest.writeString( this.title );
		dest.writeString( this.description );
		dest.writeLong( this.dateTime );
		dest.writeInt( this.width );
		dest.writeInt( this.height );
		dest.writeLong( this.size );
		dest.writeString( this.link );
		dest.writeString( this.mp4 );
		dest.writeString( this.hls );
		dest.writeParcelable( this.thumbnailUri, flags );
		dest.writeParcelable( this.videoUri, flags );
		dest.writeString( this.fullSizeLink );
	}
	
	protected ImgurImage( Parcel in )
	{
		this.id = in.readString();
		this.title = in.readString();
		this.description = in.readString();
		this.dateTime = in.readLong();
		this.width = in.readInt();
		this.height = in.readInt();
		this.size = in.readLong();
		this.link = in.readString();
		this.mp4 = in.readString();
		this.hls = in.readString();
		this.thumbnailUri = in.readParcelable( Uri.class.getClassLoader() );
		this.videoUri = in.readParcelable( Uri.class.getClassLoader() );
		this.fullSizeLink = in.readString();
	}
	
	public static final Creator<ImgurImage> CREATOR = new Creator<ImgurImage>()
	{
		@Override
		public ImgurImage createFromParcel( Parcel source )
		{
			return new ImgurImage( source );
		}
		
		@Override
		public ImgurImage[] newArray( int size )
		{
			return new ImgurImage[ size ];
		}
	};
}
