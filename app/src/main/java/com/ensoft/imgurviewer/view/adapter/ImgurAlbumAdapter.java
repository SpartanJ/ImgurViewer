package com.ensoft.imgurviewer.view.adapter;

import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ensoft.imgurviewer.App;
import com.ensoft.imgurviewer.model.ImgurImage;
import com.ensoft.imgurviewer.service.FrescoService;
import com.ensoft.imgurviewer.service.listener.ControllerImageInfoListener;
import com.ensoft.imgurviewer.view.activity.AlbumPagerActivity;
import com.ensoft.imgurviewer.view.helper.MetricsHelper;
import com.ensoft.imgurviewer.view.widget.ImageViewForcedHeight;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.imgurviewer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImgurAlbumAdapter extends RecyclerView.Adapter<ImgurAlbumAdapter.AlbumImageHolder>
{
	public static final String TAG = ImgurAlbumAdapter.class.getCanonicalName();
	private int resourceId;
	private List<ImgurImage> dataSet = new ArrayList<>();
	private boolean isLandscape = false;
	private boolean isGridLayout;
	private int layoutRows;
	private int floatingMenuHeight;
	private Point resizeOption;
	
	public ImgurAlbumAdapter( int resource, boolean isGridLayout, int layoutRows, ImgurImage[] objects, int floatingMenuHeight, Point resizeOption )
	{
		resourceId = resource;
		this.isGridLayout = isGridLayout;
		this.layoutRows = layoutRows;
		this.floatingMenuHeight = floatingMenuHeight;
		this.resizeOption = resizeOption;
		
		appendImages( objects );
	}
	
	public void appendImages( ImgurImage[] images )
	{
		dataSet.addAll( Arrays.asList( images ) );
		
		notifyDataSetChanged();
	}
	
	public void setOrientationLandscape( boolean landscape )
	{
		isLandscape = landscape;
	}
	
	@Override
	@NonNull
	public AlbumImageHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType )
	{
		View v = LayoutInflater.from( parent.getContext() ).inflate( resourceId, parent, false );
		return new AlbumImageHolder( v );
	}
	
	@Override
	public void onBindViewHolder( @NonNull AlbumImageHolder holder, int position )
	{
		holder.setData( dataSet, dataSet.get( position ), position, getItemCount(), isLandscape, isGridLayout, layoutRows, floatingMenuHeight, resizeOption );
	}
	
	@Override
	public int getItemCount()
	{
		return dataSet.size();
	}
	
	static class AlbumImageHolder extends RecyclerView.ViewHolder
	{
		LinearLayout container;
		ImgurImage image;
		ImageViewForcedHeight imageView;
		TextView title;
		TextView description;
		ProgressBar progressBar;
		
		private AlbumImageHolder( final View view )
		{
			super( view );
			
			container = view.findViewById( R.id.albumPhoto_container );
			imageView = view.findViewById( R.id.albumPhoto_photo );
			progressBar = view.findViewById( R.id.albumPhoto_progressBar );
			title = view.findViewById( R.id.albumPhoto_title );
			description = view.findViewById( R.id.albumPhoto_description );
			
			GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder( view.getResources() )
				.setActualImageScaleType( ScalingUtils.ScaleType.CENTER_CROP )
				.build();
			
			imageView.setHierarchy( hierarchy );
		}
		
		private void setData( List<ImgurImage> dataSet, ImgurImage img, int position, int count, boolean isLandscape, boolean isGridLayout, int layoutRows, int floatingMenuHeight, Point resizeOption )
		{
			image = img;
			imageView.setOnClickListener( v -> AlbumPagerActivity.newInstance( v.getContext(), dataSet.toArray( new ImgurImage[ dataSet.size() ] ), position ) );
			
			Log.v( TAG, "Loading album image: " + image.getLink() );
			
			progressBar.setVisibility( View.VISIBLE );
			
			if ( null != image.getTitle() && !isGridLayout )
			{
				title.setVisibility( View.VISIBLE );
				title.setText( image.getTitle() );
			}
			else
			{
				title.setVisibility( View.GONE );
			}
			
			if ( null != image.getDescription() && !isGridLayout )
			{
				description.setVisibility( View.VISIBLE );
				
				description.setText( image.getDescription() );
			}
			else
			{
				description.setVisibility( View.GONE );
			}
			
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
			{
				int top = MetricsHelper.getStatusBarHeight( App.getInstance() ) + floatingMenuHeight;
				
				if ( !isGridLayout )
				{
					if ( position == 0 )
					{
						if ( null != image.getTitle() )
						{
							title.setPadding( 0, top, 0, 0 );
						}
						else if ( null != image.getDescription() )
						{
							description.setPadding( 0, top, 0, 0 );
						}
						else
						{
							imageView.setPadding( 0, top, 0, 0 );
						}
					}
					else if ( position == count - 1 && !isLandscape )
					{
						imageView.setPadding( 0, 0, 0, MetricsHelper.getNavigationBarHeight( App.getInstance() ) );
					}
					else
					{
						imageView.setPadding( 0, 0, 0, 0 );
					}
				}
				else
				{
					container.setPadding( 0, 0, 0, 0 );
					
					imageView.setHeightRatio( 1 );
					
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
					
					layoutParams.setMargins( 0, ( position < layoutRows ) ? top : 0, 0, ( position >= count - layoutRows ) && !isLandscape ? MetricsHelper.getNavigationBarHeight( App.getInstance() ) : 0 );
					
					imageView.setLayoutParams( layoutParams );
					progressBar.setLayoutParams( layoutParams );
				}
			}
			
			new FrescoService().loadImage( img.getLinkUri(), img.getThumbnailLinkUri(), imageView, new ControllerImageInfoListener()
			{
				@Override
				public void onFinalImageSet( String id, ImageInfo imageInfo, Animatable animatable )
				{
					progressBar.setVisibility( View.INVISIBLE );
				}
				
				@Override
				public void onFailure( String id, Throwable throwable )
				{
					Log.v( TAG, throwable.toString() );
				}
			}, resizeOption );
		}
	}
}
