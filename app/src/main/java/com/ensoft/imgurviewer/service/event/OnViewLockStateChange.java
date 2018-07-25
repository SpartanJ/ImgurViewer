package com.ensoft.imgurviewer.service.event;

public class OnViewLockStateChange
{
	private boolean isLocked;
	
	public OnViewLockStateChange( boolean locked )
	{
		this.isLocked = locked;
	}
	
	public boolean isLocked()
	{
		return this.isLocked;
	}
}
