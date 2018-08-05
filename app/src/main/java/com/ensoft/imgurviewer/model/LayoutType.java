package com.ensoft.imgurviewer.model;

public enum LayoutType
{
	LIST(0),
	GRID(1);
	
	int value;
	
	LayoutType( int value )
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static LayoutType fromInt( int value )
	{
		return value == 0 ? LIST : GRID;
	}
	
	public static LayoutType fromString( String value )
	{
		return "1".equals( value  ) ? GRID : LIST;
	}
}
