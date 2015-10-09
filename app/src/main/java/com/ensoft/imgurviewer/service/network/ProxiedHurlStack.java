package com.ensoft.imgurviewer.service.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.android.volley.toolbox.HurlStack;

public class ProxiedHurlStack extends HurlStack
{
	protected String proxyHost;
	protected int proxyPort;

	public ProxiedHurlStack( String host, int port )
	{
		super();
		proxyHost = host;
		proxyPort = port;
	}

	@Override
	protected HttpURLConnection createConnection(URL url) throws IOException
	{
		// Start the connection by specifying a proxy server
		Proxy proxy = new Proxy( Proxy.Type.HTTP, InetSocketAddress.createUnresolved( proxyHost, proxyPort ) );

		return (HttpURLConnection) url.openConnection(proxy);
	}
}