package com.ensoft.imgurviewer.service;

import android.net.Uri;
import android.text.TextUtils;

public class UriUtils
{
	public static Uri getUriMatch( String haystack, String needleStart, String needleEnds )
	{
		String uriString = StringUtils.getStringMatch( haystack, needleStart, needleEnds );
		
		return !TextUtils.isEmpty( uriString ) ? Uri.parse( uriString ) : null;
	}
	
	public static boolean uriMatchesDomain( Uri uri, String domain )
	{
		return uriMatchesDomain( uri, domain, "" );
	}
	
	public static boolean uriMatchesDomain( Uri uri, String domain, String path )
	{
		String uriStr = uri.toString();
		
		return ( uriStr.startsWith( "https://" + domain + path ) ||
			uriStr.startsWith( "http://" + domain + path ) ||
			uriStr.startsWith( "https://www." + domain + path ) ||
			uriStr.startsWith( "http://www." + domain + path ) );
	}
}
