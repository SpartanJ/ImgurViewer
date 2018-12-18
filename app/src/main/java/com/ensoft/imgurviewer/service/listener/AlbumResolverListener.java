package com.ensoft.imgurviewer.service.listener;

import com.ensoft.imgurviewer.model.ImgurImage;

public interface AlbumResolverListener
{
	void onAlbumResolved( ImgurImage[] album );
	
	void onAlbumError( String error );
}
