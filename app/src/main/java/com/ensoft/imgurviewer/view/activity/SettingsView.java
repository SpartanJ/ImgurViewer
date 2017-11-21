package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.PreferencesService;
import com.imgurviewer.R;

public class SettingsView extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}

	public static class MyPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			final PreferencesService preferencesService = App.getInstance().getPreferencesService();
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preferences );

			Preference proxyHost = findPreference("proxyHost");

			proxyHost.setDefaultValue( preferencesService.getProxyHost() );
			proxyHost.setOnPreferenceChangeListener( (preference, newValue) ->
			{
				preferencesService.setProxyHost( (String)newValue );
				return true;
			} );

			Preference proxyPort = findPreference( "proxyPort" );
			proxyPort.setDefaultValue( Integer.toString( preferencesService.getProxyPort() ) );
			proxyPort.setOnPreferenceChangeListener( (preference, newValue) ->
			{
				if ( newValue != null && !((String)newValue).isEmpty() )
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
			
			final CheckBoxPreference muteVideos = (CheckBoxPreference)findPreference( "muteVideos" );
			muteVideos.setChecked( preferencesService.videosMuted() );
			muteVideos.setOnPreferenceChangeListener( (preference, newValue) ->
			{
				preferencesService.setMuteVideos( !preferencesService.videosMuted() );
				muteVideos.setChecked( preferencesService.videosMuted() );
				
				return true;
			} );
			
			Preference clearCache = findPreference( "clearCache" );
			clearCache.setOnPreferenceClickListener( ( preference ) ->
			{
				new FrescoService().clearCaches();
				Toast.makeText( MyPreferenceFragment.this.getActivity(), R.string.cacheCleared, Toast.LENGTH_SHORT ).show();
				return true;
			} );
		}
	}
}