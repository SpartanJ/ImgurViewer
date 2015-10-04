package com.ensoft.imgurviewer.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueService
{
	private static RequestQueueService mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;

	private RequestQueueService(Context context)
	{
		mCtx = context;
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
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue( mCtx.getApplicationContext() );
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req)
	{
		getRequestQueue().add(req);
	}
}
