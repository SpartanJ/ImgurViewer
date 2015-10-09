package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.ensoft.imgurviewer.App;
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
			proxyHost.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{
					preferencesService.setProxyHost( (String)newValue );
					return true;
				}
			});

			Preference proxyPort = findPreference( "proxyPort" );
			proxyPort.setDefaultValue( Integer.toString( preferencesService.getProxyPort() ) );
			proxyPort.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{
					preferencesService.setProxyPort( Integer.valueOf( (String)newValue ) );
					return true;
				}
			});
		}
	}
}