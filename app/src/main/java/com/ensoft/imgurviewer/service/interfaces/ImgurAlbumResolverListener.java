package com.ensoft.imgurviewer.service.interfaces;

import com.ensoft.imgurviewer.model.ImgurAlbum;

public interface ImgurAlbumResolverListener
{
	void onAlbumResolved( ImgurAlbum album );

	void onError( String error );
}
