package com.ensoft.imgurviewer.service;

import android.net.Uri;

import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.ensoft.imgurviewer.service.resource.ClippitUserService;
import com.ensoft.imgurviewer.service.resource.FlickrService;
import com.ensoft.imgurviewer.service.resource.GfycatService;
import com.ensoft.imgurviewer.service.resource.GiphyService;
import com.ensoft.imgurviewer.service.resource.GyazoService;
import com.ensoft.imgurviewer.service.resource.ImageServiceSolver;
import com.ensoft.imgurviewer.service.resource.ImgurService;
import com.ensoft.imgurviewer.service.resource.InstagramService;
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
import com.ensoft.imgurviewer.service.resource.TwitchClipsService;
import com.ensoft.imgurviewer.service.resource.VidmeService;
import com.ensoft.imgurviewer.service.resource.VimeoService;
import com.ensoft.imgurviewer.service.resource.XVideosService;
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
	
	protected void loadServices()
	{
		resourceServiceSolvers.add( new ResourceServiceSolver( new ImgurService(), resourceLoadListener, ImgurAlbumGalleryViewer.class ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new GyazoService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new GfycatService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new RedditUploadsService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new StreamableService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new TwitchClipsService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new InstagramService(), resourceLoadListener, ImgurAlbumGalleryViewer.class ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new VidmeService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new FlickrService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new GiphyService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new RedditVideoService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new StreamjaService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new VimeoService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new ClippitUserService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new PornHubService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new XVideosService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new SpankBangService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new YouPornService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new RedTubeService(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new Tube8Service(), resourceLoadListener, null ) );
		resourceServiceSolvers.add( new ResourceServiceSolver( new PornTubeService(), resourceLoadListener, null ) );
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
		
		if ( ImageServiceSolver.isVideoUrl( uri ) )
		{
			resourceLoadListener.loadVideo( uri, uri );
		}
		else
		{
			resourceLoadListener.loadImage( uri, null );
		}
	}
}
