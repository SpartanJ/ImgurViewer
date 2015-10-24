package com.ensoft.imgurviewer.service.interfaces;

import com.ensoft.imgurviewer.model.ImgurAlbum;
import com.ensoft.imgurviewer.model.ImgurImage;

public interface ImgurGalleryResolverListener
{
	void onAlbumResolved( ImgurAlbum album );

	void onImageResolved( ImgurImage image );

	void onError( String error );
}
