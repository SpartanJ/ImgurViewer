package com.ensoft.imgurviewer.service.listener;

import com.ensoft.imgurviewer.model.ImgurImage;

public interface AlbumSolverListener
{
	void onAlbumResolved( ImgurImage[] album );
	
	void onImageResolved( ImgurImage image );
	
	void onAlbumError( String error );
}
