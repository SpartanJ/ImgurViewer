package com.ensoft.imgurviewer.service;

public class StringUtils
{
	public static int countMatches( String haystack, String needle )
	{
		return haystack.length() - haystack.replace( needle, "" ).length();
	}
}
