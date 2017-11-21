package com.ensoft.imgurviewer.service.listener;

import com.ensoft.imgurviewer.model.InstagramProfileModel;

public interface InstagramProfileResolverListener
{
	void onProfileResolved( InstagramProfileModel profile );

	void onError( String error );
}
