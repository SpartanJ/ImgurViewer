package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.ensoft.imgurviewer.App;
import com.ensoft.restafari.network.service.NetworkPreferencesService;
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
			final NetworkPreferencesService preferencesService = App.getInstance().getPreferencesService();
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
				}
			});
		}
	}
}