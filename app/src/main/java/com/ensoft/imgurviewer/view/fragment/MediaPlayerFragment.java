package com.ensoft.imgurviewer.view.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.view.helper.MetricsHelper;
import com.ensoft.imgurviewer.view.helper.ViewHelper;
import com.imgurviewer.R;

public class MediaPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener
{
	private static final String TAG = MediaPlayerFragment.class.getCanonicalName();
	
	protected VideoView videoView;
	protected ImageView playPauseView;
	protected ImageView audioOnOffView;
	protected TextView timeTextView;
	protected SeekBar seekBarView;
	protected MediaPlayer.OnPreparedListener userOnPreparedListener;
	protected Handler seekBarHandler = new Handler();
	protected Rect margins = new Rect( 0, 0, 0, 0 );
	protected boolean initialized = false;
	protected MediaPlayer mediaPlayer;
	protected boolean isMuted = false;

	protected void updatePlayPauseState()
	{
		if ( null != videoView )
		{
			if ( videoView.isPlaying() )
			{
				videoView.pause();
				playPauseView.setImageResource( R.drawable.ic_play_circle_outline_white_48dp );
			}
			else
			{
				videoView.start();
				playPauseView.setImageResource( R.drawable.ic_pause_circle_outline_white_48dp );
			}
		}
	}
	
	protected void updateAudioOnOffState()
	{
		if ( null != videoView && null != mediaPlayer )
		{
			if ( isMuted )
			{
				unmute();
				audioOnOffView.setImageResource( R.drawable.ic_volume_up_white_48dp );
			}
			else
			{
				mute();
				audioOnOffView.setImageResource( R.drawable.ic_volume_off_white_48dp );
			}
		}
	}

	protected View.OnClickListener playPauseOnClickListener = v -> updatePlayPauseState();
	
	protected View.OnClickListener audioOnOffListener = v -> updateAudioOnOffState();

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.mediaplayer, container, false );
	}

	@Override
	public void onViewCreated( View view, @Nullable Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		if ( null != getResources() && null != getResources().getConfiguration() )
		{
			setOrientationMargins( getResources().getConfiguration().orientation );
		}

		playPauseView = view.findViewById( R.id.mediaPlayer_playPause );
		playPauseView.setOnClickListener( playPauseOnClickListener );
		
		audioOnOffView = view.findViewById( R.id.mediaPlayer_audioOnOff );
		audioOnOffView.setOnClickListener( audioOnOffListener );

		timeTextView = view.findViewById( R.id.mediaPlayer_time );

		seekBarView = view.findViewById( R.id.mediaPlayer_seekBar );
		seekBarView.setOnSeekBarChangeListener( this );

		PorterDuffColorFilter colorFilter = new PorterDuffColorFilter( getResources().getColor( R.color.imgur_color ), PorterDuff.Mode.SRC_IN );

		seekBarView.getProgressDrawable().setColorFilter( colorFilter );

		if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN )
		{
			seekBarView.getThumb().setColorFilter( colorFilter );
		}

		if ( null != videoView )
		{
			init();
		}
	}

	public void setOnPreparedListener( MediaPlayer.OnPreparedListener onPreparedListener )
	{
		userOnPreparedListener = onPreparedListener;
	}

	public void setVideoView( VideoView videoView )
	{
		this.videoView = videoView;
		this.videoView.setOnPreparedListener( this );

		if ( null != playPauseView )
		{
			init();
		}
	}

	public void updateProgressBar()
	{
		seekBarHandler.postDelayed( seekBarRunnable, 100 );
	}

	private Runnable seekBarRunnable = new Runnable()
	{
		public void run()
		{
			try
			{
				if ( isAdded() )
				{
					long timeLeft = ( videoView.getDuration() - videoView.getCurrentPosition() ) / 1000L;
					String timeLeftStr = String.valueOf( timeLeft ) + getString( R.string.seconds_abbr );
					
					timeTextView.setText( timeLeftStr );
					
					seekBarView.setProgress( videoView.getCurrentPosition() );
				}
			}
			catch ( Exception e )
			{
				Log.e( TAG, e.getMessage() );
			}
			
			seekBarHandler.postDelayed( this, 100 );
		}
	};

	protected void init()
	{
		if ( null != seekBarView && null != videoView )
		{
			seekBarView.setMax( videoView.getDuration() );

			updateProgressBar();

			initialized = true;
		}
	}

	@Override
	public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
	{
	}

	@Override
	public void onStartTrackingTouch( SeekBar seekBar )
	{
		seekBarHandler.removeCallbacks( seekBarRunnable );
	}

	@Override
	public void onStopTrackingTouch( SeekBar seekBar )
	{
		seekBarHandler.removeCallbacks( seekBarRunnable );

		videoView.seekTo( seekBar.getProgress() );

		updateProgressBar();
	}

	@Override
	public void onPrepared( MediaPlayer mp )
	{
		init();
		
		updatePlayPauseState();
		
		mediaPlayer = mp;
		
		userOnPreparedListener.onPrepared( mp );
		
		isMuted = !App.getInstance().getPreferencesService().videosMuted();
		
		updateAudioOnOffState();
	}

	public void setVisibility(int visibility)
	{
		if ( null != getView() )
		{
			getView().setVisibility( visibility );
		}
	}

	public void startAnimation( Animation animation )
	{
		if ( null != getView() )
		{
			getView().startAnimation( animation );
		}
	}

	public void setMargins( int left, int top, int right, int bottom )
	{
		View v = getView();

		if ( null != v )
		{
			ViewHelper.setMargins( v, left, top, right, bottom );
		}
		else
		{
			margins = new Rect( left, top, right, bottom );
		}
	}

	public void setOrientationMargins( int orientation )
	{
		if ( orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			setMargins( 0, 0, MetricsHelper.getNavigationBarWidth( getActivity() ), 0 );
		}
		else if ( orientation == Configuration.ORIENTATION_PORTRAIT )
		{
			setMargins( 0, 0, 0, MetricsHelper.getNavigationBarHeight( getActivity() ) +
									( ViewHelper.hasImmersive( getActivity() ) ? MetricsHelper.dpToPx( getActivity(), 8 ) : 0 )
			);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();

		if ( initialized )
		{
			seekBarHandler.removeCallbacks( seekBarRunnable );
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if ( initialized )
		{
			updateProgressBar();
		}
	}
	
	public void mute()
	{
		setVolume(0);
		isMuted = true;
	}
	
	public void unmute()
	{
		setVolume(100);
		isMuted = false;
	}
	
	public void setVolume(int amount)
	{
		try
		{
			if ( null != mediaPlayer )
			{
				final int max = 100;
				final double numerator = max - amount > 0 ? Math.log( max - amount ) : 0;
				final float volume = (float) ( 1 - ( numerator / Math.log( max ) ) );
				mediaPlayer.setVolume( volume, volume );
			}
		}
		catch ( Exception e )
		{
			Log.e( TAG, e.getMessage() );
		}
	}
}
