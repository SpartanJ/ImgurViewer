package com.ensoft.imgurviewer.model;

public enum ThumbnailSize
{
	/**
	 * s = Small Square (90×90) as seen in the example above
	 * b = Big Square (160×160)
	 * t = Small Thumbnail (160×160)
	 * m = Medium Thumbnail (320×320)
	 * l = Large Thumbnail (640×640) as seen in the example above
	 * h = Huge Thumbnail (1024×1024)
	 */
	FULL_IMAGE(""),
	SMALL_SQUARE("s"),
	BIG_SQUARE("b"),
	SMALL_THUMBNAIL("t"),
	MEDIUM_THUMBNAIL("m"),
	LARGE_THUMBNAIL("l"),
	HUGE_THUMBNAIL("h");
	
	public final String value;
	
	ThumbnailSize( final String value )
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
	
	public static ThumbnailSize fromString( String s )
	{
		switch ( s )
		{
			case "s": return SMALL_SQUARE;
			case "b": return BIG_SQUARE;
			case "t": return SMALL_THUMBNAIL;
			case "m": return MEDIUM_THUMBNAIL;
			case "l": return LARGE_THUMBNAIL;
			case "h": return HUGE_THUMBNAIL;
			default:
			case "": return FULL_IMAGE;
		}
	}
}
