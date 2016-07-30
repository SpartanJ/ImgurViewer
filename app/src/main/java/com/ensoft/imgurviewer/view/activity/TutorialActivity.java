package com.ensoft.imgurviewer.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.imgurviewer.R;

public class TutorialActivity extends Activity implements View.OnClickListener
{
	protected TextView tutorialText;
	protected TextView tutorialNext;
	protected ImageView tutorialImage;
	protected int step = 0;

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

		tutorialText = (TextView)findViewById( R.id.tutorial_text );
		tutorialNext = (TextView)findViewById( R.id.tutorial_next );
		tutorialImage = (ImageView)findViewById( R.id.tutorial_image );

		tutorialNext.setOnClickListener( this );
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
			rotate.setDuration(250);
			rotate.setRepeatCount(1);
			tutorialImage.startAnimation(rotate);
		}
	}
}
