package com.ensoft.imgurviewer.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
	public static int countMatches( String haystack, String needle )
	{
		return haystack.length() - haystack.replace( needle, "" ).length();
	}
	
	public static String regexString( String string )
	{
		return string.replaceAll( "[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]", "\\\\$0" );
	}
	
	public static Matcher getStringMatcher( String haystack, String needleStart, String needleEnds )
	{
		String regexString = String.format( "%s(.*?)%s", regexString(needleStart), regexString(needleEnds) );
		Pattern regex = Pattern.compile( regexString );
		return regex.matcher( haystack );
	}
	
	public static String getLastStringMatch( String haystack, String needleStart, String needleEnds )
	{
		Matcher m = getStringMatcher( haystack, needleStart, needleEnds );
		
		if ( m.find() )
		{
			String res;
			
			do
			{
				res = m.group();
			} while ( m.find() );
			
			return res.substring( needleStart.length(), res.length() - needleEnds.length() );
		}
		
		return null;
	}
	
	public static String getFirstStringMatch( String haystack, String needleStart, String needleEnds )
	{
		Matcher m = getStringMatcher( haystack, needleStart, needleEnds );
		
		if ( m.find() )
		{
			String res = m.group();
			return res.substring( needleStart.length(), res.length() - needleEnds.length() );
		}
		
		return null;
	}
}
