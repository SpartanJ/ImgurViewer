package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.ensoft.imgurviewer.service.resource.ClippitUserService;
import com.ensoft.imgurviewer.service.resource.DeviantArtService;
import com.ensoft.imgurviewer.service.resource.EromeService;
import com.ensoft.imgurviewer.service.resource.FlickrService;
import com.ensoft.imgurviewer.service.resource.GfycatService;
import com.ensoft.imgurviewer.service.resource.GiphyService;
import com.ensoft.imgurviewer.service.resource.GyazoService;
import com.ensoft.imgurviewer.service.resource.ImgFlipService;
import com.ensoft.imgurviewer.service.resource.ImgurService;
import com.ensoft.imgurviewer.service.resource.InstagramService;
import com.ensoft.imgurviewer.service.resource.MediaServiceSolver;
import com.ensoft.imgurviewer.service.resource.PornHubService;
import com.ensoft.imgurviewer.service.resource.PornTubeService;
import com.ensoft.imgurviewer.service.resource.RedTubeService;
import com.ensoft.imgurviewer.service.resource.RedditUploadsService;
import com.ensoft.imgurviewer.service.resource.RedditVideoService;
import com.ensoft.imgurviewer.service.resource.ResourceServiceSolver;
import com.ensoft.imgurviewer.service.resource.SpankBangService;
import com.ensoft.imgurviewer.service.resource.StreamableService;
import com.ensoft.imgurviewer.service.resource.StreamjaService;
import com.ensoft.imgurviewer.service.resource.Tube8Service;
import com.ensoft.imgurviewer.service.resource.TumblrService;
import com.ensoft.imgurviewer.service.resource.TwitchClipsService;
import com.ensoft.imgurviewer.service.resource.VidmeService;
import com.ensoft.imgurviewer.service.resource.VimeoService;
import com.ensoft.imgurviewer.service.resource.XHamsterService;
import com.ensoft.imgurviewer.service.resource.XVideosService;
import com.ensoft.imgurviewer.service.resource.XnxxService;
import com.ensoft.imgurviewer.service.resource.YouPornService;
import com.ensoft.imgurviewer.view.activity.ImgurAlbumGalleryViewer;

import java.util.ArrayList;

public class ResourceSolver
{
	private ResourceLoadListener resourceLoadListener;
	private ArrayList<ResourceServiceSolver> resourceServiceSolvers = new ArrayList<>();
	
	public ResourceSolver( ResourceLoadListener resourceLoadListener )
	{
		this.resourceLoadListener = resourceLoadListener;
		
		loadServices();
	}
	
	private void addSolver( MediaServiceSolver serviceSolver )
	{
		addSolver( serviceSolver, null );
	}
	
	private void addSolver( MediaServiceSolver serviceSolver, Class<?> galleryViewClass )
	{
		resourceServiceSolvers.add( new ResourceServiceSolver( serviceSolver, resourceLoadListener, galleryViewClass ) );
	}
	
	private void loadServices()
	{
		addSolver( new ImgurService(), ImgurAlbumGalleryViewer.class );
		addSolver( new GyazoService() );
		addSolver( new ImgFlipService() );
		addSolver( new GfycatService() );
		addSolver( new RedditUploadsService() );
		addSolver( new StreamableService() );
		addSolver( new TwitchClipsService() );
		addSolver( new InstagramService(), ImgurAlbumGalleryViewer.class );
		addSolver( new VidmeService() );
		addSolver( new FlickrService(), ImgurAlbumGalleryViewer.class );
		addSolver( new GiphyService() );
		addSolver( new RedditVideoService() );
		addSolver( new StreamjaService() );
		addSolver( new VimeoService() );
		addSolver( new ClippitUserService() );
		addSolver( new DeviantArtService() );
		addSolver( new PornHubService() );
		addSolver( new XVideosService() );
		addSolver( new SpankBangService() );
		addSolver( new YouPornService() );
		addSolver( new RedTubeService() );
		addSolver( new Tube8Service() );
		addSolver( new PornTubeService() );
		addSolver( new EromeService() );
		addSolver( new XnxxService() );
		addSolver( new XHamsterService() );
		addSolver( new TumblrService() );
	}
	
	public void solve( Uri uri )
	{
		for ( ResourceServiceSolver resourceServiceSolver : resourceServiceSolvers )
		{
			if ( resourceServiceSolver.solve( uri ) )
			{
				return;
			}
		}
		
		if ( UriUtils.isVideoUrl( uri ) || UriUtils.isAudioUrl( uri ) )
		{
			resourceLoadListener.loadVideo( uri, UriUtils.guessMediaTypeFromUri( uri ), uri );
		}
		else
		{
			resourceLoadListener.loadImage( uri, null );
		}
	}
}
