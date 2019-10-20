package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;

import com.ensoft.imgurviewer.service.resource.EromeService;
import com.ensoft.imgurviewer.service.resource.FlickrService;
import com.ensoft.imgurviewer.service.resource.ImgurAlbumService;
import com.ensoft.imgurviewer.service.resource.InstagramService;

public interface AlbumProvider
{
	void getAlbum( Uri uri, final AlbumSolverListener albumSolverListener );
	
	boolean isAlbum( Uri uri );
	
	static AlbumProvider[] getProviders()
	{
		return new AlbumProvider[] {
			new ImgurAlbumService(),
			new InstagramService(),
			new FlickrService(),
			new EromeService()
		};
	}
}
