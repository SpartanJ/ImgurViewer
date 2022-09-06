package com.ensoft.imgurviewer.service.resource;

import java.net.URLDecoder;

public class RedGifsService extends BasicVideoServiceSolver
{
	@Override
	public String getDomain()
	{
		return "redgifs.com";
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] { "<meta property=\"og:video\" content=\"" };
	}
	
	protected String parseUrlString( String urlString )
	{
		try
		{
			urlString = urlString.replaceAll( "&amp;", "&" );
			urlString = urlString.replace( "thumbs1", "thumbs3" );
			urlString = urlString.replace( "thumbs2", "thumbs3" );
			urlString = urlString.replace( "thumbs4", "thumbs3" );
			urlString = urlString.replace( "thumbs5", "thumbs3" );
			return urlString;
		}
		catch ( Exception ignored )
		{
			return urlString;
		}
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] { "\">" };
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/watch/" };
	}
}
