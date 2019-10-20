package com.ensoft.imgurviewer.service.resource;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornHubService extends BasicVideoServiceSolver
{
	public static final String PORNHUB_DOMAIN = "pornhub.com";
	
	@Override
	public String getDomain()
	{
		return PORNHUB_DOMAIN;
	}
	
	@Override
	public String[] getDomainPath()
	{
		return new String[] { "/view_video.php?" };
	}
	
	@Override
	protected Map<String, String> getHeaders( Uri referer )
	{
		Map<String, String> headers = super.getHeaders( referer );
		headers.put( "Cookie", "platform=tv;" );
		return headers;
	}
	
	@Override
	public String[] getNeedleStart()
	{
		return new String[] {};
	}
	
	@Override
	public String[] getNeedleEnd()
	{
		return new String[] {};
	}
	
	private List<String> getJsVars(String mediaString)
	{
		ArrayList<String> list = new ArrayList<>();
		
		if ( mediaString != null )
		{
			String cleanString = mediaString.replaceAll( "[^:]//.*|/\\\\*((?!=*/)(?s:.))+\\\\*/", "" );
			String[] vars = cleanString.split( "\\+" );
			for ( String var : vars )
			{
				list.add( var.trim() );
			}
		}
		
		return list;
	}
	
	private String getJsVarValue(String response, String varName)
	{
		StringBuilder varValue = new StringBuilder();
		String varDefinition = String.format( "var %s=", varName );
		Pattern regex = Pattern.compile( String.format( "%s(.*?)\";", varDefinition ) );
		Matcher m = regex.matcher(response);
		
		if ( m.find() )
		{
			String varDeclaration = m.group();
			String varValues = varDeclaration.substring( varDefinition.length(), varDeclaration.length() - 1 );
			String[] values = varValues.split( "\\+" );
			
			for ( String val : values )
			{
				val = val.trim().replaceAll( "\"", "" );
				varValue.append( val );
			}
		}
		
		return varValue.toString();
	}
	
	@Override
	protected Uri getVideoUrlFromResponse( String response )
	{
		List<String> vars = getJsVars(getStringMatch( response, "var mediastring=", ";</script>" ));
		StringBuilder url = new StringBuilder();
		
		for ( String var : vars )
		{
			url.append( getJsVarValue( response, var ) );
		}
		
		return !url.toString().isEmpty() ? Uri.parse( url.toString() ) : null;
	}
}
