package com.ensoft.imgurviewer.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.imgurviewer.R;

public class ImageViewForcedHeight extends DraweeView<GenericDraweeHierarchy>
{
	protected float heightRatio = 1.f;
	
	protected void obtainRatio( final Context context, final AttributeSet attrs )
	{
		TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ImageViewForcedHeight );
		
		try
		{
			heightRatio = a.getFloat( R.styleable.ImageViewForcedHeight_heightRatio, heightRatio );
		}
		finally
		{
			a.recycle();
		}
	}
	
	public ImageViewForcedHeight( final Context context )
	{
		super( context );
	}
	
	public ImageViewForcedHeight( final Context context, final AttributeSet attrs )
	{
		super( context, attrs );
		
		obtainRatio( context, attrs );
	}
	
	public ImageViewForcedHeight( final Context context, final AttributeSet attrs,
								  final int defStyle )
	{
		super( context, attrs, defStyle );
		obtainRatio( context, attrs );
	}
	
	@Override
	protected void onMeasure( int width, int height )
	{
		super.onMeasure( width, height );
		int measuredWidth = getMeasuredWidth();
		
		setMeasuredDimension( measuredWidth, (int) ( (float) measuredWidth * heightRatio ) );
	}
	
	public void setHeightRatio( float heightRatio )
	{
		if ( this.heightRatio != heightRatio )
		{
			this.heightRatio = heightRatio;
			
			requestLayout();
			invalidate();
		}
	}
}
