package com.ensoft.imgurviewer.service;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ensoft.imgurviewer.model.MediaType;

import java.util.List;

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
			uriStr.startsWith( "http://www." + domain + path ) ||
			uriStr.startsWith( "http://m." + domain + path ) ||
			uriStr.startsWith( "https://m." + domain + path ) );
	}
	
	public static boolean isVideoUrl( Uri uri )
	{
		if ( null != uri )
		{
			List<String> pathSegments = uri.getPathSegments();
			
			if ( null != pathSegments && pathSegments.size() > 0 )
			{
				String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
				
				return lastPathSegment.endsWith( ".gifv" ) ||
					lastPathSegment.endsWith( ".mp4" ) ||
					lastPathSegment.endsWith( ".avi" ) ||
					lastPathSegment.endsWith( ".flv" ) ||
					lastPathSegment.endsWith( ".mkv" ) ||
					lastPathSegment.endsWith( ".webm" ) ||
					lastPathSegment.endsWith( ".m3u8" ) ||
					lastPathSegment.endsWith( ".mpd" ) ||
					lastPathSegment.endsWith( ".ism" ) ||
					( lastPathSegment.endsWith( ".gif" ) && uri.toString().contains( "fm=mp4" ) );
			}
		}
		
		return false;
	}
	
	public static boolean isAudioUrl( Uri uri )
	{
		if ( null != uri )
		{
			List<String> pathSegments = uri.getPathSegments();
			
			if ( null != pathSegments && pathSegments.size() > 0 )
			{
				String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
				
				return lastPathSegment.endsWith( ".ogg" ) ||
					lastPathSegment.endsWith( ".mp3" ) ||
					lastPathSegment.endsWith( ".wav" ) ||
					lastPathSegment.endsWith( ".3gp" ) ||
					lastPathSegment.endsWith( ".m4a" ) ||
					lastPathSegment.endsWith( ".opus" ) ||
					lastPathSegment.endsWith( ".wma" ) ||
					lastPathSegment.endsWith( ".flac" );
			}
		}
		
		return false;
	}
	
	public static boolean isVideoUrl( String uri )
	{
		return isVideoUrl( Uri.parse( uri ) );
	}
	
	public static MediaType guessMediaTypeFromUri( Uri uri )
	{
		List<String> pathSegments = null != uri ? uri.getPathSegments() : null;
		
		if ( null != pathSegments && pathSegments.size() > 0 )
		{
			String lastPathSegment = pathSegments.get( pathSegments.size() - 1 );
			
			if ( lastPathSegment.endsWith( ".m3u8" ) )
			{
				return MediaType.STREAM_HLS;
			}
			else if ( lastPathSegment.endsWith( ".mpd" ) )
			{
				return MediaType.STREAM_DASH;
			}
			else if ( lastPathSegment.endsWith( ".ism" ) )
			{
				return MediaType.STREAM_SS;
			}
		}
		
		return isVideoUrl( uri ) ? MediaType.VIDEO_MP4 : MediaType.IMAGE;
	}
	
	public static boolean isContentUri( Uri uri )
	{
		return "content".equals( uri.getScheme() );
	}
	
	public static boolean isFileUri( Uri uri )
	{
		return "file".equals( uri.getScheme() );
	}
	
	public static Uri contentUriToFileUri( Context context, @NonNull Uri uri )
	{
		String filePath = null;
		
		try
		{
			if ( isContentUri( uri ) )
			{
				Cursor cursor = context.getContentResolver().query( uri, new String[]{ android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null );
				
				if ( null != cursor && cursor.moveToFirst() )
				{
					String pathSolved = cursor.getString( 0 );
					
					if ( null != pathSolved )
					{
						filePath = "file://" + pathSolved;
					}
					else
					{
						pathSolved = getPath( context, uri );
						
						if ( null != pathSolved )
						{
							filePath = "file://" + getPath( context, uri );
						}
					}
					
					cursor.close();
				}
			}
			else
			{
				filePath = "file://" + uri.getPath();
			}
			
			return Uri.parse( filePath );
		}
		catch ( Exception e )
		{
			Log.e( UriUtils.class.getCanonicalName(), "contentUriToFileUri: " + e.toString() );
		}
		
		return null;
	}
	
	
	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri     The Uri to query.
	 * @author paulburke
	 */
	public static String getPath( final Context context, final Uri uri )
	{
		
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		
		// DocumentProvider
		if ( isKitKat && DocumentsContract.isDocumentUri( context, uri ) )
		{
			// ExternalStorageProvider
			if ( isExternalStorageDocument( uri ) )
			{
				final String docId = DocumentsContract.getDocumentId( uri );
				final String[] split = docId.split( ":" );
				final String type = split[ 0 ];
				
				if ( "primary".equalsIgnoreCase( type ) )
				{
					return Environment.getExternalStorageDirectory() + "/" + split[ 1 ];
				}
				
				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if ( isDownloadsDocument( uri ) )
			{
				
				final String id = DocumentsContract.getDocumentId( uri );
				final Uri contentUri = ContentUris.withAppendedId(
					Uri.parse( "content://downloads/public_downloads" ), Long.valueOf( id ) );
				
				return getDataColumn( context, contentUri, null, null );
			}
			// MediaProvider
			else if ( isMediaDocument( uri ) )
			{
				final String docId = DocumentsContract.getDocumentId( uri );
				final String[] split = docId.split( ":" );
				final String type = split[ 0 ];
				
				Uri contentUri = null;
				if ( "image".equals( type ) )
				{
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				}
				else if ( "video".equals( type ) )
				{
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				}
				else if ( "audio".equals( type ) )
				{
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				
				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
					split[ 1 ]
				};
				
				return getDataColumn( context, contentUri, selection, selectionArgs );
			}
		}
		// MediaStore (and general)
		else if ( "content".equalsIgnoreCase( uri.getScheme() ) )
		{
			return getDataColumn( context, uri, null, null );
		}
		// File
		else if ( "file".equalsIgnoreCase( uri.getScheme() ) )
		{
			return uri.getPath();
		}
		
		return null;
	}
	
	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn( Context context, Uri uri, String selection,
										String[] selectionArgs )
	{
		
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
			column
		};
		
		try
		{
			cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs,
				null );
			if ( cursor != null && cursor.moveToFirst() )
			{
				final int column_index = cursor.getColumnIndexOrThrow( column );
				return cursor.getString( column_index );
			}
		}
		finally
		{
			if ( cursor != null )
				cursor.close();
		}
		return null;
	}
	
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument( Uri uri )
	{
		return "com.android.externalstorage.documents".equals( uri.getAuthority() );
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument( Uri uri )
	{
		return "com.android.providers.downloads.documents".equals( uri.getAuthority() );
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument( Uri uri )
	{
		return "com.android.providers.media.documents".equals( uri.getAuthority() );
	}
}
