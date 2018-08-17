package com.ensoft.imgurviewer.service;

import android.content.Context;

import java.util.Locale;

public class TimeService
{
	protected Context context;
	
	public TimeService( Context context )
	{
		this.context = context;
	}
	
	public String timeFormatter( long totalSeconds )
	{
		if ( totalSeconds < 60 )
		{
			return String.format( Locale.getDefault(), "%02d", 0 ) + ":" + String.format( Locale.getDefault(), "%02d", totalSeconds );
		}
		
		long minutesLeft = totalSeconds / 60;
		long secondsLeft = totalSeconds - minutesLeft * 60;
		
		if ( minutesLeft < 60 )
		{
			return String.format( Locale.getDefault(), "%02d", minutesLeft ) + ":" + String.format( Locale.getDefault(), "%02d", secondsLeft );
		}
		else
		{
			long hoursLeft = minutesLeft / 60;
			minutesLeft = minutesLeft - hoursLeft * 60;
			
			return String.format( Locale.getDefault(), "%02d", hoursLeft ) + String.format( Locale.getDefault(), "%02d", minutesLeft ) + ":" + String.format( Locale.getDefault(), "%02d", secondsLeft );
		}
	}
	
	public String timeLeftFormatter( long videoMs, long videoCurPositionMs )
	{
		return timeFormatter( ( videoMs - videoCurPositionMs ) / 1000L );
	}
}
