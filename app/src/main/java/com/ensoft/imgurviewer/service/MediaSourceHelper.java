package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.net.Uri;

import com.ensoft.imgurviewer.model.MediaType;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class MediaSourceHelper
{
	private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
	private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY = new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0 (Chrome)", BANDWIDTH_METER );
	
	public static MediaSource buildMediaSource( Context context, Uri uri, MediaType mediaType )
	{
		if ( MediaType.STREAM_SS == mediaType )
			return new SsMediaSource.Factory( new DefaultSsChunkSource.Factory( DATA_SOURCE_FACTORY ), DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( MediaType.STREAM_DASH == mediaType )
			return new DashMediaSource.Factory( new DefaultDashChunkSource.Factory( DATA_SOURCE_FACTORY ), DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( MediaType.STREAM_HLS == mediaType )
			return new HlsMediaSource.Factory( DATA_SOURCE_FACTORY ).createMediaSource(uri);
		
		if ( UriUtils.isFileUri( uri ) )
			return new ExtractorMediaSource.Factory( new DefaultDataSourceFactory( context, "ua" ) ).setExtractorsFactory( new DefaultExtractorsFactory() ).createMediaSource( uri );
		
		return new ExtractorMediaSource.Factory( DATA_SOURCE_FACTORY ).createMediaSource(uri);
	}
	
	public static MediaSource buildLoopingMediaSource( Context context, Uri uri, MediaType mediaType )
	{
		return new LoopingMediaSource( MediaSourceHelper.buildMediaSource( context, uri, mediaType ) );
	}
}
