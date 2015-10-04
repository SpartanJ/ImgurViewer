package com.ensoft.imgurviewer.view.activity;

import android.os.Bundle;

import com.ensoft.imgurviewer.App;

public class AppActivity extends android.app.Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		App.getInstance().addActivity();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		App.getInstance().destroyActivity();
	}
}
