package com.ensoft.imgurviewer.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ensoft.imgurviewer.model.MediaType;
import com.ensoft.imgurviewer.service.ResourceSolver;
import com.ensoft.imgurviewer.service.listener.ResourceLoadListener;
import com.imgurviewer.R;

public class TutorialActivity extends Activity implements View.OnClickListener
{
	public static final String TAG = TutorialActivity.class.getCanonicalName();
	protected ImageView settingsImage;
	protected TextView tutorialText;
	protected TextView tutorialNext;
	protected ImageView tutorialImage;
	protected EditText linkPasteInput;
	protected int step = 0;
	protected TextWatcher pasteWatcher = new TextWatcher()
	{
		boolean ignore = false;
		
		@Override
		public void beforeTextChanged( CharSequence s, int start, int count, int after )
		{}
		
		@Override
		public void onTextChanged( CharSequence s, int start, int before, int count )
		{}
		
		@Override
		public void afterTextChanged( Editable s )
		{
			if ( ignore )
			{
				ignore = false;
				return;
			}
			
			String url = s.toString();
			
			if ( URLUtil.isValidUrl( url ) )
			{
				new ResourceSolver( new ResourceLoadListener()
				{
					@Override
					public void loadVideo( Uri uri, MediaType mediaType, Uri referer )
					{
						Intent intent = new Intent( TutorialActivity.this, ImageViewer.class );
						intent.putExtra( ImageViewer.PARAM_RESOURCE_PATH, url );
						startActivity( intent );
						ignore = true;
						linkPasteInput.setText( "" );
					}
					
					@Override
					public void loadImage( Uri uri, Uri thumbnail )
					{
						Intent intent = new Intent( TutorialActivity.this, ImageViewer.class );
						intent.putExtra( ImageViewer.PARAM_RESOURCE_PATH, url );
						startActivity( intent );
						ignore = true;
						linkPasteInput.setText( "" );
					}
					
					@Override
					public void loadAlbum( Uri uri, Class<?> view )
					{
						Intent intent = new Intent( TutorialActivity.this, view );
						intent.putExtra( AppActivity.ALBUM_DATA, uri.toString() );
						startActivity( intent );
						ignore = true;
						linkPasteInput.setText( "" );
					}
					
					@Override
					public void loadFailed( Uri uri, String error )
					{
						Log.v( TAG, error );
						Toast.makeText( TutorialActivity.this, error, Toast.LENGTH_SHORT ).show();
						ignore = true;
						linkPasteInput.setText( "" );
					}
				} ).solve( Uri.parse( url ) );
			}
			else
			{
				Toast.makeText( TutorialActivity.this, getString( R.string.invalidUrl ), Toast.LENGTH_SHORT ).show();
				ignore = true;
				linkPasteInput.setText( "" );
			}
		}
	};
	
	
	public static void newInstance( Context context )
	{
		Intent intent = new Intent( context, TutorialActivity.class );
		
		context.startActivity( intent );
	}
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_tutorial );
	}
	
	public void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		
		settingsImage = findViewById( R.id.settings );
		tutorialText = findViewById( R.id.tutorial_text );
		tutorialNext = findViewById( R.id.tutorial_next );
		tutorialImage = findViewById( R.id.tutorial_image );
		linkPasteInput = findViewById( R.id.linkPasteInput );
		
		settingsImage.setOnClickListener( this::onSettingsClick );
		tutorialNext.setOnClickListener( this );
		linkPasteInput.addTextChangedListener( pasteWatcher );
	}
	
	public void onSettingsClick( View v )
	{
		startActivity( new Intent( this, SettingsActivity.class ) );
	}
	
	@Override
	public void onClick( View v )
	{
		step++;
		
		if ( 1 == step )
		{
			tutorialText.setText( getString( R.string.tutorial_open_with ) );
			tutorialImage.setImageResource( R.drawable.tut2 );
		}
		else if ( 2 == step )
		{
			tutorialText.setText( getString( R.string.tutorial_that_is_it ) );
			tutorialImage.setImageResource( R.drawable.ic_tag_faces_white_48px );
			tutorialNext.setText( getString( R.string.tutorial_ended ) );
		}
		else
		{
			RotateAnimation rotate = new RotateAnimation( 0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
			rotate.setDuration( 250 );
			rotate.setRepeatCount( 1 );
			tutorialImage.startAnimation( rotate );
		}
	}
}
