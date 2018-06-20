package com.ensoft.imgurviewer.model;

import com.google.android.exoplayer2.util.MimeTypes;

public enum MediaType
{
	IMAGE( "image" ),
	STREAM_DASH( MimeTypes.APPLICATION_MPD ),
	STREAM_HLS( MimeTypes.APPLICATION_M3U8 ),
	STREAM_SS( MimeTypes.APPLICATION_SS ),
	VIDEO_MP4( MimeTypes.VIDEO_MP4 );
	
	String value;
	
	MediaType( String mediaType )
	{
		this.value = mediaType;
	}
}
