package com.ensoft.imgurviewer.service;

import android.content.Context;

import com.ensoft.imgurviewer.model.LayoutType;
import com.ensoft.imgurviewer.model.ThumbnailSize;
import com.ensoft.restafari.network.service.NetworkPreferencesService;
import com.imgurviewer.R;

public class PreferencesService extends NetworkPreferencesService
{
	private static final String MUTE_VIDEOS = "muteVideos";
	private static final String GESTURES_ENABLED = "gesturesEnabled";
	private static final String GESTURE_IMAGE_VIEW = "getGesturesImageView";
	private static final String GESTURE_GALLERY_VIEW = "gesturesGalleryView";
	private static final String SCREEN_LOCK_BUTTON = "screenLockButton";
	private static final String FULLSCREEN_BUTTON = "fullscreenButton";
	private static final String DEFAULT_GALLERY_LAYOUT = "defaultGalleryLayout";
	private static final String GRID_LAYOUT_COLUMNS = "gridLayoutColumns";
	private static final String LIST_VIEW_IMAGE_SCALE_TYPE = "listViewImageScaleType";
	private static final String GRID_VIEW_IMAGE_SCALE_TYPE = "gridViewImageScaleType";
	private static final String THUMBNAIL_SIZE_ON_GALLERY = "thumbnailSizeOnGallery";
	private static final String HIDE_NAVIGATION_= "keepNavigationButtonsVisible";
	private static final String DISABLE_WINDOW_TRANSPARENCY = "disableWindowTransparency";
	
	public PreferencesService( Context context )
	{
		super( context );
	}
	
	public boolean videosMuted()
	{
		return getMuteVideos();
	}
	
	public boolean getMuteVideos()
	{
		return getDefaultSharedPreferences().getBoolean( MUTE_VIDEOS, false );
	}
	
	public void setMuteVideos( boolean muteVideos )
	{
		getDefaultSharedPreferences().edit().putBoolean( MUTE_VIDEOS, muteVideos ).apply();
	}
	
	public boolean getDisableWindowTransparency()
	{
		return getDefaultSharedPreferences().getBoolean( DISABLE_WINDOW_TRANSPARENCY, false );
	}
	
	public void setDisableWindowTransparency( boolean disableWindowTransparency )
	{
		getDefaultSharedPreferences().edit().putBoolean( DISABLE_WINDOW_TRANSPARENCY, disableWindowTransparency ).apply();
	}
	
	public boolean isNavigationBarKeptVisible()
	{
		return getDefaultSharedPreferences().getBoolean( HIDE_NAVIGATION_, false );
	}
	
	public void setNavigationBarKeptVisible( boolean navigationBarKeptVisible )
	{
		getDefaultSharedPreferences().edit().putBoolean( HIDE_NAVIGATION_, navigationBarKeptVisible ).apply();
	}
	
	public boolean gesturesEnabled()
	{
		return getDefaultSharedPreferences().getBoolean( GESTURES_ENABLED, true );
	}
	
	public void setGesturesEnabled( boolean gesturesEnabled )
	{
		getDefaultSharedPreferences().edit().putBoolean( GESTURES_ENABLED, gesturesEnabled ).apply();
	}
	
	public String getGesturesImageView()
	{
		return getDefaultSharedPreferences().getString( GESTURE_IMAGE_VIEW, context.getString( R.string.vertical_val ) );
	}
	
	public void setGesturesImageView( String gesturesImageView )
	{
		getDefaultSharedPreferences().edit().putString( GESTURE_IMAGE_VIEW, gesturesImageView ).apply();
	}
	
	public String getGesturesGalleryView()
	{
		return getDefaultSharedPreferences().getString( GESTURE_GALLERY_VIEW, context.getString( R.string.horizontal_val ) );
	}
	
	public void setGesturesGalleryView( String gesturesGalleryView )
	{
		getDefaultSharedPreferences().edit().putString( GESTURE_GALLERY_VIEW, gesturesGalleryView ).apply();
	}
	
	public String getListViewImageScaleType()
	{
		return getDefaultSharedPreferences().getString( LIST_VIEW_IMAGE_SCALE_TYPE, context.getString( R.string.list_view_default_scale_type ) );
	}
	
	public void setListViewImageScaleType( String listViewImageScaleType )
	{
		getDefaultSharedPreferences().edit().putString( LIST_VIEW_IMAGE_SCALE_TYPE, listViewImageScaleType ).apply();
	}
	
	public String getGridViewImageScaleType()
	{
		return getDefaultSharedPreferences().getString( GRID_VIEW_IMAGE_SCALE_TYPE, context.getString( R.string.grid_view_default_scale_type ) );
	}
	
	public void setGridViewImageScaleType( String gridViewImageScaleType )
	{
		getDefaultSharedPreferences().edit().putString( GRID_VIEW_IMAGE_SCALE_TYPE, gridViewImageScaleType ).apply();
	}
	
	public boolean screenLockButton()
	{
		return getDefaultSharedPreferences().getBoolean( SCREEN_LOCK_BUTTON, false );
	}
	
	public void setScreenLockButton( boolean screenLockButton )
	{
		getDefaultSharedPreferences().edit().putBoolean( SCREEN_LOCK_BUTTON, screenLockButton ).apply();
	}
	
	public boolean fullscreenButton()
	{
		return getDefaultSharedPreferences().getBoolean( FULLSCREEN_BUTTON, true );
	}
	
	public void setFullscreenButton( boolean fullscreenButton )
	{
		getDefaultSharedPreferences().edit().putBoolean( FULLSCREEN_BUTTON, fullscreenButton ).apply();
	}
	
	public String getDefaultGalleryLayout()
	{
		return getDefaultSharedPreferences().getString( DEFAULT_GALLERY_LAYOUT, "0" );
	}
	
	public void setDefaultGalleryLayout( String defaultGalleryLayout )
	{
		getDefaultSharedPreferences().edit().putString( DEFAULT_GALLERY_LAYOUT, defaultGalleryLayout ).apply();
	}
	
	public LayoutType getDefaultGalleryLayoutType()
	{
		return LayoutType.fromString( getDefaultGalleryLayout() );
	}
	
	public int getGridLayoutColumns()
	{
		return Integer.valueOf( getDefaultSharedPreferences().getString( GRID_LAYOUT_COLUMNS, "2" ) );
	}
	
	public void setGridLayoutColumns( int columns )
	{
		getDefaultSharedPreferences().edit().putString( GRID_LAYOUT_COLUMNS, Integer.toString( columns ) ).apply();
	}
	
	public ThumbnailSize thumbnailSizeOnGallery()
	{
		return ThumbnailSize.fromString( getDefaultSharedPreferences().getString( THUMBNAIL_SIZE_ON_GALLERY, context.getString( R.string.thumbnail_sizes_default ) ) );
	}
	
	public void setThumbnailSizeOnGallery( String thumbnailSizeOnGallery )
	{
		getDefaultSharedPreferences().edit().putString( THUMBNAIL_SIZE_ON_GALLERY, thumbnailSizeOnGallery ).apply();
	}
}
