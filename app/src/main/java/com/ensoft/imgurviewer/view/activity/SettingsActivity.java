package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.imgurviewer.R;

public class SettingsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		if ( null != getSupportActionBar() )
		{
			getSupportActionBar().show();
			getSupportActionBar().setTitle( R.string.settings );
			getSupportActionBar().setDisplayHomeAsUpEnabled( true );
			getSupportActionBar().setDisplayShowTitleEnabled( true );
		}
		
		getSupportFragmentManager().beginTransaction().replace( android.R.id.content, new AppPreferenceFragment() ).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case android.R.id.home:
			{
				onBackPressed();
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}

	public static class AppPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences( Bundle bundle, String rootKey )
		{
			final PreferencesService preferencesService = App.getInstance().getPreferencesService();
			
			setPreferencesFromResource( R.xml.preferences, rootKey );
			
			final CheckBoxPreference keepNavigationVisible = (CheckBoxPreference) findPreference( "keepNavigationVisible" );
			keepNavigationVisible.setChecked( preferencesService.isNavigationBarKeptVisible() );
			keepNavigationVisible.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setNavigationBarKeptVisible( !preferencesService.isNavigationBarKeptVisible() );
				keepNavigationVisible.setChecked( preferencesService.isNavigationBarKeptVisible() );
				
				return true;
			} );
			
			final CheckBoxPreference disableWindowTransparency = (CheckBoxPreference) findPreference( "disableWindowTransparency" );
			disableWindowTransparency.setChecked( preferencesService.getDisableWindowTransparency() );
			disableWindowTransparency.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setDisableWindowTransparency( !preferencesService.getDisableWindowTransparency() );
				keepNavigationVisible.setChecked( preferencesService.getDisableWindowTransparency() );
				
				return true;
			} );
			
			Preference proxyHost = findPreference( "proxyHost" );
			
			proxyHost.setDefaultValue( preferencesService.getProxyHost() );
			proxyHost.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setProxyHost( (String) newValue );
				return true;
			} );
			
			Preference proxyPort = findPreference( "proxyPort" );
			proxyPort.setDefaultValue( Integer.toString( preferencesService.getProxyPort() ) );
			proxyPort.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				if ( newValue != null && !( (String) newValue ).isEmpty() )
				{
					try
					{
						int intValue = Integer.valueOf( (String) newValue );
						
						preferencesService.setProxyPort( intValue );
					}
					catch ( Exception e )
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				
				return true;
			} );
			
			final CheckBoxPreference muteVideos = (CheckBoxPreference) findPreference( "muteVideos" );
			muteVideos.setChecked( preferencesService.videosMuted() );
			muteVideos.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setMuteVideos( !preferencesService.videosMuted() );
				muteVideos.setChecked( preferencesService.videosMuted() );
				
				return true;
			} );
			
			Preference clearCache = findPreference( "clearCache" );
			clearCache.setOnPreferenceClickListener( ( preference ) ->
			{
				new FrescoService().clearCaches();
				Toast.makeText( AppPreferenceFragment.this.getActivity(), R.string.cacheCleared, Toast.LENGTH_SHORT ).show();
				return true;
			} );
			
			final ListPreference defaultGalleryViewStyle = (ListPreference) findPreference( "defaultGalleryViewStyle" );
			defaultGalleryViewStyle.setValue( preferencesService.getDefaultGalleryLayout() );
			defaultGalleryViewStyle.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setDefaultGalleryLayout( newValue.toString() );
				
				return true;
			} );
			
			Preference gridViewRows = findPreference( "gridViewRows" );
			gridViewRows.setDefaultValue( Integer.toString( preferencesService.getGridLayoutColumns() ) );
			gridViewRows.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				if ( newValue != null && !( (String) newValue ).isEmpty() )
				{
					try
					{
						int intValue = Integer.valueOf( (String) newValue );
						
						preferencesService.setGridLayoutColumns( intValue );
					}
					catch ( Exception e )
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				
				return true;
			} );
			
			final CheckBoxPreference gesturesEnabled = (CheckBoxPreference) findPreference( "gesturesEnabled" );
			gesturesEnabled.setChecked( preferencesService.gesturesEnabled() );
			gesturesEnabled.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setGesturesEnabled( !preferencesService.gesturesEnabled() );
				gesturesEnabled.setChecked( preferencesService.gesturesEnabled() );
				
				return true;
			} );
			
			final ListPreference imageGesturePref = (ListPreference) findPreference( "gestureImageView" );
			imageGesturePref.setValue( preferencesService.getGesturesImageView() );
			imageGesturePref.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setGesturesImageView( newValue.toString() );
				
				return true;
			} );
			
			final ListPreference galleryGesturePref = (ListPreference) findPreference( "gestureGalleryView" );
			galleryGesturePref.setValue( preferencesService.getGesturesGalleryView() );
			galleryGesturePref.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setGesturesGalleryView( newValue.toString() );
				
				return true;
			});
			
			final CheckBoxPreference screenLockEnabled = (CheckBoxPreference) findPreference( "screenLockEnabled" );
			screenLockEnabled.setChecked( preferencesService.screenLockButton() );
			screenLockEnabled.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setScreenLockButton( !preferencesService.screenLockButton() );
				screenLockEnabled.setChecked( preferencesService.screenLockButton() );
				
				return true;
			} );
			
			final CheckBoxPreference fullscreenEnabled = (CheckBoxPreference) findPreference( "fullscreenEnabled" );
			fullscreenEnabled.setChecked( preferencesService.fullscreenButton() );
			fullscreenEnabled.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setFullscreenButton( !preferencesService.fullscreenButton() );
				fullscreenEnabled.setChecked( preferencesService.fullscreenButton() );
				
				return true;
			} );
			
			final ListPreference listViewImageScaleType = (ListPreference) findPreference( "listViewImageScaleType" );
			listViewImageScaleType.setValue( preferencesService.getListViewImageScaleType() );
			listViewImageScaleType.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setListViewImageScaleType( newValue.toString() );
				
				return true;
			});
			
			final ListPreference gridViewImageScaleType = (ListPreference) findPreference( "gridViewImageScaleType" );
			gridViewImageScaleType.setValue( preferencesService.getGridViewImageScaleType() );
			gridViewImageScaleType.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setGridViewImageScaleType( newValue.toString() );
				
				return true;
			});
			
			final ListPreference thumbnailSizeOnGallery = (ListPreference) findPreference( "thumbnailsSizeOnGallery" );
			thumbnailSizeOnGallery.setValue( preferencesService.thumbnailSizeOnGallery().toString() );
			thumbnailSizeOnGallery.setOnPreferenceChangeListener( ( preference, newValue ) ->
			{
				preferencesService.setThumbnailSizeOnGallery( newValue.toString() );
				
				return true;
			});
			
			final Preference versionPreference = findPreference( "version" );
			versionPreference.setTitle( App.getInstance().getVersionName() );
		}
	}
}