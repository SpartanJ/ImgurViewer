package com.ensoft.imgurviewer.service.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.service.PreferencesService;

public class RequestQueueService
{
	private static RequestQueueService instance;
	private RequestQueue requestQueue;
	private Context context;

	private RequestQueueService(Context context)
	{
		this.context = context;
		requestQueue = getRequestQueue();
	}

	public static synchronized RequestQueueService init( Context context )
	{
		if ( instance == null)
		{
			instance = new RequestQueueService(context);
		}

		return instance;
	}

	public static synchronized RequestQueueService getInstance()
	{
		return instance;
	}

	public RequestQueue getRequestQueue()
	{
		if ( requestQueue == null)
		{
			createRequestQueue();
		}

		return requestQueue;
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
		requestQueue = Volley.newRequestQueue( context.getApplicationContext(), httpStack );
	}
}
