package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.service.resource.EromeService;
import com.ensoft.imgurviewer.service.resource.FlickrService;
import com.ensoft.imgurviewer.service.resource.ImgurAlbumService;
import com.ensoft.imgurviewer.service.resource.InstagramService;
import com.ensoft.imgurviewer.service.resource.NhentaiService;
import com.ensoft.imgurviewer.service.resource.RedditAlbumService;

public interface AlbumProvider
{
	void getAlbum( Uri uri, final AlbumSolverListener albumSolverListener );
	
	boolean isAlbum( Uri uri );
	
	static AlbumProvider[] getProviders()
	{
		return new AlbumProvider[] {
			new ImgurAlbumService(),
			new RedditAlbumService(),
			new InstagramService(),
			new FlickrService(),
			new EromeService(),
			new NhentaiService()
		};
	}
}
