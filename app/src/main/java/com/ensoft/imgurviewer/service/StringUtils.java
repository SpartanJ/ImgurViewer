package com.ensoft.imgurviewer.service;

public class StringUtils
{
	public static int countMatches( String haystack, String needle )
	{
		return haystack.length() - haystack.replace( needle, "" ).length();
	}
	
	public static String getStringMatch( String haystack, String needleStart, String needleEnds )
	{
		int pos = haystack.lastIndexOf( needleStart );
		
		if ( -1 != pos )
		{
			int endPos = haystack.indexOf( needleEnds, pos + needleStart.length() );
			
			if ( -1 != endPos )
			{
				return haystack.substring( pos + needleStart.length(), endPos );
			}
		}
		
		return null;
	}
}
