package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

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
			super.onCreate(savedInstanceState);
			addPreferencesFromResource( R.xml.preferences );

			Preference proxyHost = findPreference("proxyHost");
			proxyHost.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{

					return true;
				}
			});

			Preference proxyPort = findPreference( "proxyPort" );
			proxyPort.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue)
				{

					return true;
				}
			});
		}
	}
}