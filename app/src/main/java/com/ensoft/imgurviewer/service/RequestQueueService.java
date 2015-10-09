package com.ensoft.imgurviewer.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.network.ProxiedHurlStack;

public class RequestQueueService
{
	private static RequestQueueService mInstance;
	private RequestQueue mRequestQueue;
	private static Context mContext;

	private RequestQueueService(Context context)
	{
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized RequestQueueService init( Context context )
	{
		if (mInstance == null)
		{
			mInstance = new RequestQueueService(context);
		}

		return mInstance;
	}

	public static synchronized RequestQueueService getInstance()
	{
		return mInstance;
	}

	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			createRequestQueue();
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		getRequestQueue().add(req);
	}

	public void createRequestQueue()
	{
		PreferencesService preferencesService = App.getInstance().getPreferencesService();
		String proxyHost = preferencesService.getProxyHost();
		HttpStack httpStack = null;

		if ( !proxyHost.isEmpty() )
		{
			httpStack = new ProxiedHurlStack( proxyHost, preferencesService.getProxyPort() );
		}

		// getApplicationContext() is key, it keeps you from leaking the
		// Activity or BroadcastReceiver if someone passes one in.
		mRequestQueue = Volley.newRequestQueue( mContext.getApplicationContext(), httpStack );
	}
}
