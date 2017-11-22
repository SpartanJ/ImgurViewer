package com.ensoft.imgurviewer.view.adapter;

import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

public class ImgurAlbumAdapter extends RecyclerView.Adapter<ImgurAlbumAdapter.AlbumImageHolder>
{
	public static final String TAG = ImgurAlbumAdapter.class.getCanonicalName();
	protected int resourceId;
	protected ImgurImage[] dataSet;
	private boolean isLandscape = false;

	public ImgurAlbumAdapter( int resource, ImgurImage[] objects )
	{
		resourceId = resource;
		dataSet = objects;
	}

	public void setOrientationLandscape( boolean landscape )
	{
		isLandscape = landscape;
	}

	@Override
	public AlbumImageHolder onCreateViewHolder( ViewGroup parent, int viewType )
	{
		View v = LayoutInflater.from(parent.getContext()).inflate( resourceId, parent, false );
		return new AlbumImageHolder( v );
	}

	@Override
	public void onBindViewHolder( AlbumImageHolder holder, int position )
	{
		holder.setData( dataSet, dataSet[position], position, getItemCount(), isLandscape );
	}

	@Override
	public int getItemCount()
	{
		return dataSet.length;
	}

	static class AlbumImageHolder extends RecyclerView.ViewHolder
	{
		ImgurImage image;
		ImageViewForcedHeight imageView;
		TextView title;
		ProgressBar progressBar;

		private AlbumImageHolder( final View view )
		{
			super( view );

			imageView = view.findViewById( R.id.albumPhoto_photo );
			progressBar = view.findViewById( R.id.albumPhoto_progressBar );
			title = view.findViewById( R.id.albumPhoto_title );

			GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder( view.getResources() )
				.setActualImageScaleType( ScalingUtils.ScaleType.CENTER_CROP )
				.build();

			imageView.setHierarchy( hierarchy );
		}

		private void setData( ImgurImage[] dataSet, ImgurImage img, int position, int count, boolean isLandscape )
		{
			image = img;
			imageView.setOnClickListener( v -> AlbumPagerActivity.newInstance( v.getContext(), dataSet, position ) );
			
			Log.v( TAG, "Loading album image: " + image.getLink() );

			progressBar.setVisibility( View.VISIBLE );

			if ( null != image.getTitle() )
			{
				title.setVisibility( View.VISIBLE );
				title.setText( image.getTitle() );
			}
			else
			{
				title.setVisibility( View.GONE );
			}

			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
			{
				if ( position == 0 )
				{
					if ( null != image.getTitle() )
					{
						title.setPadding( 0, MetricsHelper.getStatusBarHeight( App.getInstance() ), 0, 0 );
					}
					else
					{
						imageView.setPadding( 0, MetricsHelper.getStatusBarHeight( App.getInstance() ), 0, 0 );
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
			} );
		}
	}
}
