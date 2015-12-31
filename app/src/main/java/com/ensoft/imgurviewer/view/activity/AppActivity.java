package com.ensoft.imgurviewer.view.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.ensoft.imgurviewer.App;

public class AppActivity extends Activity
{
	public static final String ALBUM_DATA = "albumData";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
		{
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
		}

		App.getInstance().addActivity();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		App.getInstance().destroyActivity();
	}
}
